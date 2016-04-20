package me.lennartVH01;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AbbaCommand implements CommandExecutor, TabCompleter{
	public final String[] abbaSubCommands = new String[]{"calc", "info", "join", "leave", "list"};
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args){
		if(args.length == 0){
			sender.sendMessage(Messages.basicHelpMessage);
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
				if(!(game.isOpen() || p.hasPermission("AbbaCaving.joinClosed"))){
					p.sendMessage("§cThis game is closed!");
					return false;
				}
				if(!(game.hasRoom() || p.hasPermission("AbbaCaving.joinFull"))){
					p.sendMessage("§cThis game is full!");
					return false;
				}
				
				if(!p.hasPermission("AbbaCaving.allowContraband")){
					ItemStack[] contraband = AbbaTools.getContraband(p.getInventory());
					if(contraband != null && contraband.length >= 1){
						p.sendMessage("§cYou cannot carry " + contraband[0].getType().toString() + " with you on an abba game!");
						return false;
					}
				}
				
				AbbaGame oldGame = AbbaTools.getAbbaGame(p);
				if(oldGame != null){
					p.sendMessage("Left game \"" + oldGame.getName() + "\"");
					
				}
				
				AbbaTools.join(p, game);
				p.sendMessage("Joined game \"" + game.getName() + "\"");
				
				p.teleport(game.getSpawn());
				p.setGameMode(GameMode.SURVIVAL);
				return true;
			}else{
				sender.sendMessage(Messages.mustBeInGameError);
				return false;
			}
			
			
			
		case "leave":
			if(sender instanceof Player){
				Player p = (Player) sender;
				AbbaGame game = AbbaTools.leave(p.getUniqueId());
				if(game != null){
					p.sendMessage("Left game \"" + game.getName() + "\"");
					return true;
				}else{
					p.sendMessage("§cYou aren't in a game right now!");
					return false;
				}
			}else{
				sender.sendMessage(Messages.mustBeInGameError);
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
					sender.sendMessage(String.format(Messages.gameNotFoundError, args[1]));
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
					sender.sendMessage("§cNo Games found!");
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
				sender.sendMessage("usage: /abba calc");
				return false;
			}
			
			CalculatedScore score = AbbaTools.calcScore(calcPlayer.getInventory());
			for(int i = 0; i < score.size(); i++){
				if(score.getItemCount(i) != 0)
					sender.sendMessage(score.getItemCount(i) + "x" + score.getItemStack(i).getType().toString() + ": " + score.getItemPoints(i));
			}
			sender.sendMessage("§aTotal Score: " + score.total);
			return true;
		default:
			sender.sendMessage(Messages.basicHelpMessage);
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
