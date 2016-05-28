package me.lennartVH01;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.UUID;









import me.lennartVH01.game.BasicAbbaGame;
import me.lennartVH01.game.ContrabandScanner;
import me.lennartVH01.game.GameManager;
import me.lennartVH01.game.AbbaGame;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;



public class CommandHandler implements CommandExecutor, TabCompleter{
	private JavaPlugin plugin;
	private Map<String, CommandFunc> commands = new HashMap<String, CommandFunc>();
	private Map<UUID, BoolRunnable> playerCooldowns = new HashMap<UUID, BoolRunnable>();
	private static UUID serverUUID = UUID.randomUUID();
	
	public CommandHandler(JavaPlugin plugin){
		this.plugin = plugin;
		
		commands.put("join", joinCmd);
		commands.put("leave", leaveCmd);
		commands.put("info", infoCmd);
		commands.put("list", listCmd);
		commands.put("confirm", confirmCmd);
		
		commands.put("create", createCmd);
		commands.put("remove", removeCmd);
		commands.put("open", openCmd);
		commands.put("close", closeCmd);
		commands.put("start", startCmd);
		commands.put("pause", pauseCmd);
		commands.put("reload", reloadCmd);
		commands.put("config", configCmd);
	}
	
	private CommandFunc joinCmd = new CommandFunc(){
		@Override public boolean hasPermission(CommandSender sender){return Permission.BASIC.has(sender);}
		@Override public boolean run(CommandSender sender, String[] args){
			
			if(sender instanceof Player){
				Player p = (Player) sender;
				AbbaGame game;
				
				if(args.length >= 2){
					game = GameManager.getGame(args[1]);
					if(game != null){
						return GameManager.join(p, game);
					}else{
						sender.sendMessage(String.format(Messages.errorGameNotFound, args[1]));
						return false;
					}
				}else{
					game = GameManager.getGame();
					if(game != null){
						return GameManager.join(p, game);
					}else{
						sender.sendMessage(Messages.errorNoGames);
						return false;
					}
				}
			}else{
				sender.sendMessage(Messages.errorMustBeInGame);
				return false;
			}
		}
		@Override public List<String> tabComplete(CommandSender sender, String[] args){
			if(args.length == 2)
				return GameManager.getGamesAbbreviated(args[1]);
			else
				return Collections.emptyList();
		}
	};
	private CommandFunc leaveCmd = new CommandFunc(){
		@Override public boolean hasPermission(CommandSender sender){return Permission.BASIC.has(sender);}
		@Override public boolean run(CommandSender sender, String[] args){
			
			if(sender instanceof Player){
				Player p = (Player) sender;
				AbbaGame game = GameManager.leave(p);
				if(game != null){
					return true;
				}else{
					p.sendMessage(Messages.errorMustBeInGame);
					return false;
				}
			}else{
				sender.sendMessage(Messages.errorMustBeInGame);
				return false;
			}
		}
		@Override public List<String> tabComplete(CommandSender sender, String[] args){
			return Collections.emptyList();
		}
	};
	private CommandFunc infoCmd = new CommandFunc(){
		@Override public boolean hasPermission(CommandSender sender){return Permission.BASIC.has(sender);}
		@Override public boolean run(CommandSender sender, String[] args){
			
			AbbaGame game = getGame(args, sender);
			if(game == null)
				return false;
			String message = (game.isOpen() ? "§a" : "§c") + "[" + game.getName() + "] (" + game.getPlayerCount() + "/" + game.getPlayerCap() + ")" + game.getState().toString() + "\nPlayers:";
			for(Player p:game.getPlayers()){
				message += "\n" + p.getName();
			}
			sender.sendMessage(message);
			return true;
		}
		@Override public List<String> tabComplete(CommandSender sender, String[] args){
			if(args.length == 2)
				return GameManager.getGamesAbbreviated(args[1]);
			else
				return Collections.emptyList();
		}
	};
	private CommandFunc listCmd = new CommandFunc(){
		@Override public boolean hasPermission(CommandSender sender){return Permission.BASIC.has(sender);}
		@Override public boolean run(CommandSender sender, String[] args){
			sender.sendMessage("Games:");
			for(AbbaGame g:GameManager.getGames()){
				if(g.isOpen()){
					sender.sendMessage("- §a" + g.getName() + " (" + g.getPlayerCount() + "/" + g.getPlayerCap() + ")");
				}else{
					sender.sendMessage("- §7§o" + g.getName() + " (" + g.getPlayerCount() + "/" + g.getPlayerCap() + ")");
				}
			}
			return true;
		}
		@Override public List<String> tabComplete(CommandSender sender, String[] args){
			return Collections.emptyList();
			
		}
	};
	
	private CommandFunc confirmCmd = new CommandFunc(){
		@Override public boolean hasPermission(CommandSender sender){return Permission.BASIC.has(sender);}
		@Override public boolean run(CommandSender sender, String[] args){
			BoolRunnable code;
			if(sender instanceof Player){
				code = playerCooldowns.get(((Player) sender).getUniqueId());
			}else{
				code = playerCooldowns.get(serverUUID);
			}
			
			if(code != null){
				return code.run();
			}else{
				sender.sendMessage(Messages.errorNoPendingOperation);
				return false;
			}
		}
		@Override public List<String> tabComplete(CommandSender sender, String[] args){
			return Collections.emptyList();
		}
	};
	
	
	private CommandFunc createCmd = new CommandFunc(){
		@Override public boolean hasPermission(CommandSender sender){return Permission.ADMIN.has(sender);}
		@Override public boolean run(CommandSender sender, String[] args){
			if(args.length <= 5 && !(sender instanceof Player)){
				System.out.println(Messages.errorMustBeInGame);
				return false;
			}
			String gameName;
			Location gameSpawn = null;
			
			// set gameName
			if(args.length >= 2){
				gameName = args[1];
			}else{
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				gameName = "Abba" + dateFormat.format(new Date());
				
			}
			
			// set gameSpawn
			if(args.length >= 5){
				double spawnX, spawnY, spawnZ;
				try{
					spawnX = Double.parseDouble(args[2]);
					spawnY = Double.parseDouble(args[3]);
					spawnZ = Double.parseDouble(args[4]);
				}catch(NumberFormatException e){
					return false;
				}
				if(args.length >= 6){
					World w = Bukkit.getWorld(args[5]);
					if(w != null){
						
						gameSpawn = new Location(w, spawnX, spawnY, spawnZ);
						
					}else{
						sender.sendMessage(String.format(Messages.errorWorldNotFound, args[5]));
						return false;
					}
				}
			}else{
				if(sender instanceof Player){
					gameSpawn = ((Player) sender).getLocation();
				}else{
					return false;
				}
			}
			
			
			
			while(GameManager.getGame(gameName) != null){
				gameName += "_";
			}
			//create game
			AbbaGame game = new BasicAbbaGame(plugin, gameName, gameSpawn, Config.defaultDuration, Config.defaultPlayerCap, new ContrabandScanner(Config.contraband), Config.itemValues);
			GameManager.registerGame(game);
			sender.sendMessage(String.format(Messages.commandCreateSuccess, gameName));
			//sender.sendMessage(String.format(Messages.commandCreateSuccess, gameName));
			return true;
		}
		@Override public List<String> tabComplete(CommandSender sender, String[] args){
			List<String> suggestions = new ArrayList<String>();
			switch(args.length){
			case 3:
				if(args[2].isEmpty())
					suggestions.add("" + ((Player) sender).getLocation().getBlockX());
				break;
			case 4:
				if(args[3].isEmpty())
					suggestions.add("" + ((Player) sender).getLocation().getBlockY());
				break;
			case 5:
				if(args[4].isEmpty())
					suggestions.add("" + ((Player) sender).getLocation().getBlockZ());
				break;
			case 6:
				for(World world:Bukkit.getWorlds())
					if(world.getName().toLowerCase().startsWith(args[5].toLowerCase()))
						suggestions.add(world.getName());
				break;
			}
			return suggestions;
		}
	};
	
	private CommandFunc removeCmd = new CommandFunc(){
		@Override public boolean hasPermission(CommandSender sender){return Permission.ADMIN.has(sender);}
		@Override public boolean run(CommandSender sender, String[] args){
			if(args.length >= 2){
				AbbaGame game = GameManager.getGame(args[1]);
				if(game != null){
					BoolRunnable commandRemoveRunnable = () -> {
						if(GameManager.removeGame(game)){
							sender.sendMessage(String.format(Messages.commandRemoveSuccess, game.getName()));
							return true;
						}else{
							sender.sendMessage(String.format(Messages.errorGameNotFound, game.getName()));
							return false;
						}
					};
					if(args.length >= 3 && args[2].toLowerCase().equals("force")){
						return commandRemoveRunnable.run();
					}
					
					if(sender instanceof Player){
						registerCooldown(((Player) sender).getUniqueId(), commandRemoveRunnable, 200);
					}else{
						registerCooldown(serverUUID, commandRemoveRunnable, 200);
					}
					sender.sendMessage(Messages.commandRemoveConfirm);
					return true;
				}else{
					sender.sendMessage(String.format(Messages.errorGameNotFound, args[1]));
					return false;
				}
				
			}else{
				sender.sendMessage(Messages.helpRemove);
				return false;
			}
		}
		@Override public List<String> tabComplete(CommandSender sender, String[] args){
			if(args.length == 2)
				return GameManager.getGamesAbbreviated(args[1]);
			else if(args.length == 3 && "force".startsWith(args[2].toLowerCase())){
				List<String> suggestions = new ArrayList<String>(1);
				suggestions.add("force");
				return suggestions;
			}
			
			return Collections.emptyList();
		}
	};
	private CommandFunc openCmd = new CommandFunc(){
		@Override public boolean hasPermission(CommandSender sender){return Permission.ADMIN.has(sender);}
		@Override public boolean run(CommandSender sender, String[] args){
			AbbaGame game = null;
			if(args.length >= 2){
				game = GameManager.getGame(args[1]);
				if(game == null){
					sender.sendMessage(String.format(Messages.errorGameNotFound, args[1]));
					return false;
				}
			}else{
				if(sender instanceof Player)
					game = GameManager.getGame((Player) sender);
				if(game == null)
					game = GameManager.getGame();
				if(game == null){
					sender.sendMessage(Messages.errorNoGames);
					return false;
				}
			}
			game.setOpen(true);
			sender.sendMessage(String.format(Messages.commandOpenSuccess, game.getName()));
			return true;
		}

		@Override public List<String> tabComplete(CommandSender sender, String[] args){
			if(args.length == 2)
				return GameManager.getGamesAbbreviated(args[1]);
			else
				return Collections.emptyList();
		}
		
	};
	
	private CommandFunc closeCmd = new CommandFunc(){
		@Override public boolean hasPermission(CommandSender sender){return Permission.ADMIN.has(sender);}
		@Override public boolean run(CommandSender sender, String[] args){
			AbbaGame game = getGame(args, sender);
			if(game == null)
				return false;
			game.setOpen(false);
			sender.sendMessage(String.format(Messages.commandCloseSuccess, game.getName()));
			return true;
		}

		@Override public List<String> tabComplete(CommandSender sender, String[] args){
			List<String> suggestions = new ArrayList<String>(1);
			if(args.length == 2)
				suggestions.addAll(GameManager.getGamesAbbreviated(args[1]));
			return suggestions;
		}
		
	};
	
	private CommandFunc reloadCmd = new CommandFunc(){
		@Override public boolean hasPermission(CommandSender sender){return Permission.ADMIN.has(sender);}
		@Override public boolean run(CommandSender sender, String[] args) {
			sender.sendMessage("Reloading config...");
			plugin.reloadConfig();
			sender.sendMessage("Reload Complete.");
			return true;
		}

		@Override public List<String> tabComplete(CommandSender sender, String[] args) {
			return Collections.emptyList();
		}
		
	};
	private CommandFunc startCmd = new CommandFunc(){
		@Override public boolean hasPermission(CommandSender sender){return Permission.ADMIN.has(sender);}
		@Override public boolean run(CommandSender sender, String[] args) {
			AbbaGame game = getGame(args, sender);
			if(game == null)
				return false;
			
			return game.start(sender);
		}
		
		@Override public List<String> tabComplete(CommandSender sender, String[] args) {
			if(args.length == 2)
				return GameManager.getGamesAbbreviated(args[1]);
			else
				return Collections.emptyList();
		}
	};
	private CommandFunc pauseCmd = new CommandFunc(){
		@Override public boolean hasPermission(CommandSender sender){return Permission.ADMIN.has(sender);}
		@Override public boolean run(CommandSender sender, String[] args) {
			AbbaGame game = getGame(args, sender);
			if(game == null)
				return false;
			
			return game.pause(sender);
		}

		@Override public List<String> tabComplete(CommandSender sender, String[] args) {
			if(args.length == 2)
				return GameManager.getGamesAbbreviated(args[1]);
			else
				return Collections.emptyList();
		}
	};
	
	
	private CommandFunc configCmd = new CommandFunc(){
		private Map<String, CommandFunc> subCommands = new HashMap<String, CommandFunc>();
		
		
		
		//subcommands
		private CommandFunc timerCmd = new CommandFunc(){
			@Override public boolean hasPermission(CommandSender sender){return Permission.ADMIN.has(sender);}
			@Override public boolean run(CommandSender sender, String[] args){
				if(args.length >= 4){
					AbbaGame game = GameManager.getGame(args[1]);
					if(game == null){
						sender.sendMessage(String.format(Messages.errorGameNotFound, args[1]));
						return false;
					}
					int newTime;
					
					try{
						newTime = Integer.parseInt(args[3]);
					}catch(NumberFormatException e){
						sender.sendMessage(Messages.commandConfigTimerHelp);
						return false;
					}
					if(newTime <= 0){
						sender.sendMessage(Messages.commandConfigTimerHelp);
						return false;
					}
					game.setTimeLeft(newTime);
					sender.sendMessage(Messages.commandConfigTimerSuccess);
					return true;
					
				}else{
					sender.sendMessage(Messages.commandConfigTimerHelp);
					return false;
				}
			}
			
			@Override public List<String> tabComplete(CommandSender sender, String[] args){
				return Collections.emptyList();
			}
		};
		
		private CommandFunc playerCapCmd = new CommandFunc(){
			@Override public boolean hasPermission(CommandSender sender){return Permission.ADMIN.has(sender);}
			@Override public boolean run(CommandSender sender, String[] args){
				if(args.length >= 4){
					AbbaGame game = GameManager.getGame(args[1]);
					if(game == null){
						sender.sendMessage(String.format(Messages.errorGameNotFound, args[1]));
						return false;
					}
					int newPlayerCap;
					
					try{
						newPlayerCap = Integer.parseInt(args[3]);
					}catch(NumberFormatException e){
						sender.sendMessage(Messages.commandConfigPlayercapHelp);
						return false;
					}
					if(newPlayerCap < 0){
						newPlayerCap = -1;
					}
					game.setPlayerCap(newPlayerCap);
					sender.sendMessage(Messages.commandConfigPlayercapSuccess);
					return true;
					
				}else{
					sender.sendMessage(Messages.commandConfigPlayercapHelp);
					return false;
				}
			}
			
			@Override public List<String> tabComplete(CommandSender sender, String[] args){
				return Collections.emptyList();
			}
		};
		
		{
			subCommands.put("timer", timerCmd);
			subCommands.put("playercap", playerCapCmd);
			//TODO Other config subcommands
		}
		
		
		@Override public boolean hasPermission(CommandSender sender){
			for(CommandFunc command:subCommands.values()){
				if(command.hasPermission(sender))
					return true;
			}
			return false;
		}
		@Override public boolean run(CommandSender sender, String[] args){
			if(args.length <= 2){
				sender.sendMessage(Messages.commandConfigHelp);
				return false;
			}
			CommandFunc command = subCommands.get(args[2].toLowerCase());
			if(command != null){
				if(command.hasPermission(sender)){
					return command.run(sender, args);
				}else{
					sender.sendMessage(Messages.errorNoPermission);
					return false;
				}	
			}else{
				sender.sendMessage(Messages.commandConfigHelp);
				return false;
			}
		}
		
		@Override public List<String> tabComplete(CommandSender sender, String[] args) {
			if(args.length == 2)
				return GameManager.getGamesAbbreviated(args[1]);
			else if(args.length == 3){
				List<String> suggestions = new ArrayList<String>();
				subCommands.forEach((name, command) -> {
					if(name.startsWith(args[2].toLowerCase()) && command.hasPermission(sender))
						suggestions.add(name);
				});
				return suggestions;
			}else if(args.length >= 4){
				CommandFunc command = subCommands.get(args[3]);
				if(command != null && command.hasPermission(sender))
					return command.tabComplete(sender, args);
				else
					return Collections.emptyList();
			}else
				return Collections.emptyList();
		}
	};
	
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args){
		CommandFunc command = commands.get(args[0].toLowerCase());
		if(command != null){
			if(command.hasPermission(sender)){
				return command.run(sender, args);
			}else{
				sender.sendMessage(Messages.errorNoPermission);
				return false;
			}	
		}else{
			sender.sendMessage(Messages.helpAbba);
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args){
		if(args.length == 1){
			List<String> suggestions = new ArrayList<String>();
			commands.forEach((name, command) -> {
				if(name.startsWith(args[0].toLowerCase()) && command.hasPermission(sender))
					suggestions.add(name);
			});
			
			
			return suggestions;
		}else{
			CommandFunc command = commands.get(args[0]);
			if(command.hasPermission(sender)){
				return command.tabComplete(sender, args);
			}else{
				return Collections.emptyList();
			}
		}
	}
	
	private static interface CommandFunc{
		public boolean hasPermission(CommandSender sender);
		public boolean run(CommandSender sender, String[] args);
		public List<String> tabComplete(CommandSender sender, String[] args);
	}
	@FunctionalInterface
	private static interface BoolRunnable{
		public boolean run();
	}
	
	private void registerCooldown(UUID id, BoolRunnable code, int cooldown){
		playerCooldowns.put(id, code);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> playerCooldowns.remove(id), cooldown);
	}
	private AbbaGame getGame(String[] args, CommandSender sender){
		AbbaGame game = null;
		if(args.length >= 2){
			game = GameManager.getGame(args[1]);
			if(game == null)
				sender.sendMessage(String.format(Messages.errorGameNotFound, args[1]));
			return game;
		}
		else if(sender instanceof Player)
			game = GameManager.getGame((Player) sender);
		
		if(game == null)
			game = GameManager.getGame();
		if(game == null){
			sender.sendMessage(Messages.errorNoGames);
		}
		return game;
	}
}
