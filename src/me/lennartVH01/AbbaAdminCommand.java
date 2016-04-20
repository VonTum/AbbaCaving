package me.lennartVH01;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class AbbaAdminCommand implements CommandExecutor, TabCompleter{
	public final String[] abbaSubCommands = new String[]{"calcscores", "close", "config", "create", "open", "reload", "remove", "start"};
	
	public Main plugin;
	
	public AbbaAdminCommand(Main plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args){
		if(args.length == 0){
			sender.sendMessage(Messages.adminHelpMessage);
			return false;
		}
		switch(args[0].toLowerCase()){
		case "create":
			if(args.length <= 5 && !(sender instanceof Player)){
				System.out.println("Must be ingame or specify world and coordinates");
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
					World w = plugin.getServer().getWorld(args[5]);
					if(w != null){
						
						gameSpawn = new Location(w, spawnX, spawnY, spawnZ);
						
					}else{
						sender.sendMessage("§4Unknown world: \"" + args[5] + "\"");
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
			
			
			
			while(AbbaTools.getAbbaGame(gameName) != null){
				gameName += "_";
			}
			//create game
			AbbaTools.create(gameName, gameSpawn);
			sender.sendMessage("Successfully created game \"" + gameName + "\"");
			return true;
			
			
			
		case "remove":
			if(args.length >= 2){
				if(AbbaTools.removeAbbaGame(args[1])){
					sender.sendMessage("Successfully removed game \"" + args[1] + "\"");
					return true;
				}else{
					sender.sendMessage(String.format(Messages.gameNotFoundError, args[1]));
					return false;
				}
			}else{
				sender.sendMessage("Usage: /abba remove <game>");
				return false;
			}
			
		case "open":
			AbbaGame game;
			if(args.length >= 2){
				game = AbbaTools.getAbbaGame(args[1]);
				if(game == null){
					sender.sendMessage(String.format(Messages.gameNotFoundError, args[1]));
					return false;
				}
			}else{
				game = AbbaTools.getAbbaGame();
				if(game == null){
					sender.sendMessage("§cNo game found");
					return false;
				}
			}
			game.setOpen(true);
			sender.sendMessage("Opened game \"" + args[1] + "\"");
			return true;
		case "close":
			AbbaGame aGame;
			if(args.length >= 2){
				aGame = AbbaTools.getAbbaGame(args[1]);
				if(aGame == null){
					sender.sendMessage(String.format(Messages.gameNotFoundError, args[1]));
					return false;
				}
			}else{
				aGame = AbbaTools.getAbbaGame();
				if(aGame == null){
					sender.sendMessage("§cNo game found");
					return false;
				}
			}
			aGame.setOpen(false);
			sender.sendMessage("Closed game \"" + args[1] + "\"");
			return true;
			
		case "start":
			AbbaGame abbaGame;
			if(args.length >= 2){
				abbaGame = AbbaTools.getAbbaGame(args[1]);
				if(abbaGame == null){
					sender.sendMessage(String.format(Messages.gameNotFoundError, args[1]));
					return false;
				}
			}else{
				abbaGame = AbbaTools.getAbbaGame();
				if(abbaGame == null){
					sender.sendMessage("§cNo games found");
					return false;
				}
			}
			switch(abbaGame.getState()){
			case WAITING:
				abbaGame.start();
				return true;
			case PAUSED:
				sender.sendMessage("§cGame paused!");
				return false;
			case COUNTDOWN:  //countdown and running both do the same
			case RUNNING:
				sender.sendMessage("§cGame already running!");
				return false;
			case FINISHED:
			case CONCLUDED:
				sender.sendMessage("§cGame finished!");
				return false;
			}
			return false;
		case "calcscores":
			AbbaGame abaGame;
			if(args.length >= 2){
				abaGame = AbbaTools.getAbbaGame(args[1]);
			}else{
				abaGame = AbbaTools.getAbbaGame();
			}
			if(abaGame.getState() == AbbaGame.GameState.FINISHED){
				abaGame.calcScores();
				
			}else{
				sender.sendMessage("§cGame not finished yet!");
				return false;
			}
			
		case "reload":
			sender.sendMessage("Reloading config...");
			plugin.reloadConfig();
			sender.sendMessage("Reload Complete.");
			return true;
			
			
			
		case "config":
			if(args.length >= 2){
				if(args.length >= 3){
					AbbaGame abGame = AbbaTools.getAbbaGame(args[1]);
					if(abGame == null){
						sender.sendMessage(Messages.gameNotFoundError);
					}
					switch(args[2].toLowerCase()){
					case "timer":
						
						if(args.length >= 4){
							int newTime;
						
							try{
								newTime = Integer.parseInt(args[3]);
							}catch(NumberFormatException e){
								sender.sendMessage("§cPlease specify a number when you use this command");
								return false;
							}
							if(newTime <= 0){
								sender.sendMessage("§cPlease specify a stricktly positive number");
								return false;
							}
							if(abGame.getState() == AbbaGame.GameState.RUNNING || abGame.getState() == AbbaGame.GameState.PAUSED){
								abGame.setEndTime(System.currentTimeMillis() + newTime * 1000);
							}else{
								abGame.setDuration(newTime);
							}
							sender.sendMessage("Time has been updated!");
							return true;
							
						}else{
							sender.sendMessage("§cPlease specify new time");
							return false;
						}
					case "addchest":
						if(sender instanceof Player){
							Block blockInFront = ((Player) sender).getTargetBlock((Set<Material>) null, 5);
							if(BlockUtils.isSign(blockInFront)){
								Block blockNextTo = BlockUtils.getAttachedBlock(blockInFront);
								if(BlockUtils.isChest(blockNextTo)){
									if(AbbaTools.getAbbaGame(args[1]).addChest((Chest) blockNextTo.getState(), (Sign) blockInFront.getState())){
										sender.sendMessage("Chest created");
										return true;
									}else{
										sender.sendMessage("§cChest already in a game!");
										return false;
									}
								}
								
								
								sender.sendMessage("§cPut a sign next to the chest!");
								return false;
							}else{
								sender.sendMessage("§cYou must look at a chest to register it!");
								return false;
							}
						}else{
							sender.sendMessage("§cMust be ingame to register chests!");
							return false;
						}
					default:
						sender.sendMessage("§cChoose an option!");
						return false;
					}
				}else{
					sender.sendMessage("§cChoose an option!");
					return false;
				}
			}else{
				sender.sendMessage(Messages.mustSpecifyGameError);
				return false;
			}
			
			
			
		default:
			sender.sendMessage(Messages.adminHelpMessage);
			return false;
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
		List<String> cmds = new ArrayList<String>();
		switch(args.length){
		case 1:
			for(String s:abbaSubCommands){
				if(s.startsWith(args[0])){
					cmds.add(s);
				}
			}
			break;
		case 2:
			for(AbbaGame game:AbbaTools.getGames()){
				if(game.getName().toLowerCase().startsWith(args[1].toLowerCase())){
					cmds.add(game.getName());
				}
			}
		}
		return cmds;
	}
	
	
}
