package me.lennartVH01;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class AbbaCommand implements CommandExecutor, TabCompleter{
	public final String[] abbaSubCommands = new String[]{"calc", "info", "join", "leave", "list"};
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args){
		if(args.length == 0){
			return false;
		}
		
		switch(args[0].toLowerCase()){
		case "join":
			if(sender instanceof Player){
				Player p = (Player) sender;
				AbbaGame game;
				
				if(args.length >= 2){
					game = AbbaTools.getAbbaGame(args[1]);
				}else{
					game = AbbaTools.getAbbaGame();
				}
				if(game == null){
					sender.sendMessage("§cGame not found!");
					return false;
				}
				AbbaGame oldGame = AbbaTools.getAbbaGame(p);
				if(oldGame != null){
					p.sendMessage("Left game \"" + oldGame.getName() + "\"");
					
				}
				
				AbbaGame.JoinResult result = AbbaTools.join(p, game);
				switch(result){
				case SUCCESS:
					break;
				case FAIL_CLOSED:
					sender.sendMessage(Messages.errorGameClosed);
					return false;
				case FAIL_FULL:
					sender.sendMessage(Messages.errorGameFull);
					return false;
				case FAIL_NOCHEST:
					sender.sendMessage(Messages.errorNoChest);
					return false;
				case FAIL_WHITELIST:
					sender.sendMessage(Messages.errorNotWhitelisted);
					return false;
				case FAIL_CONTRABAND:
					return false;
				}
				p.sendMessage(String.format(Messages.gameJoinMessage, game.getName()));
				game.messageAll(String.format(Messages.playerJoinMessage, p.getName()));
				
				p.teleport(game.getSpawn());
				p.setGameMode(GameMode.SURVIVAL);
				return true;
			}else{
				sender.sendMessage(Messages.errorMustBeInGame);
				return false;
			}
			
			
			
		case "leave":
			if(sender instanceof Player){
				Player p = (Player) sender;
				AbbaGame game = AbbaTools.leave(p.getUniqueId());
				if(game != null){
					p.sendMessage(String.format(Messages.gameLeaveMessage, game.getName()));
					game.messageAll(String.format(Messages.playerLeaveMessage, p.getName()));
					return true;
				}else{
					p.sendMessage("§cYou aren't in a game right now!");
					return false;
				}
			}else{
				sender.sendMessage(Messages.errorMustBeInGame);
				return false;
			}
			
		case "list":
			sender.sendMessage("Games:");
			for(AbbaGame g:AbbaTools.getGames()){
				if(g.isOpen()){
					sender.sendMessage("- §a" + g.getName() + " (" + g.getPlayerCount() + "/" + g.getMaxPlayers() + ")");
				}else{
					sender.sendMessage("- §7§o" + g.getName() + " (" + g.getPlayerCount() + "/" + g.getMaxPlayers() + ")");
				}
			}
			return true;
		case "info":
			AbbaGame game = null;
			if(args.length >= 2){
				game = AbbaTools.getAbbaGame(args[1]);
				if(game == null){
					sender.sendMessage(String.format(Messages.errorGameNotFound, args[1]));
					return false;
				}
			}else{
				if(sender instanceof Player){
					game = AbbaTools.getAbbaGame((Player) sender);
				}
				if(game == null){
					game = AbbaTools.getAbbaGame();
				}
				if(game == null){
					sender.sendMessage(Messages.errorNoGames);
					return false;
				}
			}
			sender.sendMessage("Game \"" + game.getName() + "\" " + (game.isOpen() ? "§aOpen":"§cClosed"));
			return true;
		case "calc":
			Player calcPlayer;
			
			if(sender instanceof Player){
				calcPlayer = (Player) sender;
			}else{
				sender.sendMessage(Messages.helpCalc);
				return false;
			}
			
			CalculatedScore score = AbbaTools.calcScore(calcPlayer.getInventory());
			for(int i = 0; i < score.size(); i++){
				if(score.getItemCount(i) != 0)
					sender.sendMessage(score.getItemCount(i) + "x" + score.getItemStack(i).getType().toString() + ": " + score.getItemPoints(i));
			}
			sender.sendMessage("§aTotal Score: " + score.total);
			return true;
		}
		return false;
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
