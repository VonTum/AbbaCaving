package me.lennartVH01.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class GameManager implements Listener{
	private GameManager(){} //private constructor, singleton
	private static List<AbbaGame> games = new ArrayList<AbbaGame>();
	private static Map<UUID, AbbaGame> playerGames = new HashMap<UUID, AbbaGame>();
	
	public static void registerGame(AbbaGame game){
		games.add(game);
	}
	public static boolean removeGame(String name){
		return removeGame(getGame(name));
	}
	public static boolean removeGame(AbbaGame game){
		if(game == null)
			return false;
		games.remove(game);
		game.destroy();
		return true;
	}
	
	public static boolean join(Player p, AbbaGame game){
		leave(p);
		
		if(game.join(p)){
			playerGames.put(p.getUniqueId(), game);
			return true;
		}else
			return false;
	}
	public static AbbaGame leave(Player p){
		AbbaGame game = playerGames.remove(p.getUniqueId());
		if(game != null){
			game.leave(p);
		}
		return game;
	}
	public static AbbaGame getGame(){
		if(games.isEmpty()){
			return null;
		}else{
			return games.get(0);
		}
	}
	public static AbbaGame getGame(String name){
		for(AbbaGame game:games){
			if(name.equals(game.getName()))
				return game;
		}
		return null;
	}
	public static AbbaGame getGame(Player p){
		return playerGames.get(p.getUniqueId());
	}
	public static List<String> getGamesAbbreviated(String shortenedName){
		List<String> gameNames = new ArrayList<String>();
		for(AbbaGame game:games){
			String name = game.getName();
			if(name.toLowerCase().startsWith(shortenedName.toLowerCase()))
				gameNames.add(name);
		}
		return gameNames;
	}
	
	public static List<AbbaGame> getGames(){
		return games;
	}
	
	public static void cleanup(){
		for(AbbaGame game:games){
			game.destroy();
		}
	}
}
