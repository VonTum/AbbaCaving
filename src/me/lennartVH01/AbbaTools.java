package me.lennartVH01;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.lennartVH01.AbbaGame.JoinResult;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;



public class AbbaTools{
	public static Main plugin;
	public static List<ValueItemPair> itemPairs;
	private static List<AbbaGame> games = new ArrayList<AbbaGame>();
	
	private static Map<UUID, AbbaGame> playerGameMap = new HashMap<UUID, AbbaGame>();
	//private static Map<Location, AbbaGame> chestGameMap = new HashMap<Location, AbbaGame>();
	
	
	
	public static void initialize(Main plugin, List<ValueItemPair> valueItemPairs) {
		AbbaTools.plugin = plugin;
		itemPairs = valueItemPairs;
		
	}
	
	
	
	public static void create(String name, Location spawn){
		FileConfiguration config = plugin.getConfig();
		AbbaGame game = new AbbaGame(name, spawn, config.getInt("GameDuration"), config.getInt("PlayerCap"));
		games.add(game);
	}
	public static boolean removeAbbaGame(String name){
		AbbaGame game = getAbbaGame(name);
		return removeAbbaGame(game);
	}
		
	public static boolean removeAbbaGame(AbbaGame game){
		if(game == null){
			return false;
		}
		
		for(UUID id:game.getPlayerIDs()){
			playerGameMap.remove(id);// remove people from game Map
		}
		game.destroy();				// remove people from internal game Map
		games.remove(game);
		return true;
	}
	public static AbbaGame getAbbaGame(String name){
		for(AbbaGame game:games){
			if(game.name.equalsIgnoreCase(name)){
				return game;
			}
		}
		
		return null;
	}
	public static AbbaGame getAbbaGame(){
		if(games.size() >= 1){
			return games.get(0);
		}else{
			return null;
		}
	}
	public static AbbaGame getAbbaGame(Player p){
		return playerGameMap.get(p.getUniqueId());
	}
	
	public static AbbaGame leave(UUID id){
		AbbaGame game = playerGameMap.remove(plugin.getServer().getPlayer(id).getUniqueId());
		if(game != null){
			game.removePlayer(id);
			return game;
		}
		return null;
	}
	public static AbbaGame.JoinResult join(Player p, AbbaGame game){
		leave(p.getUniqueId());
		
		JoinResult result = game.addPlayer(p);
		if(result == JoinResult.SUCCESS){
			playerGameMap.put(p.getUniqueId(), game);
		}
		return result;
		
	}
	public static boolean isInGame(Player p){
		return playerGameMap.containsKey(p.getUniqueId());
	}
	
	
	public static ItemStack[] getContraband(Inventory i){
		return null;
	}
	
	
	
	public static CalculatedScore calcScore(Inventory inv){
		CalculatedScore points = new CalculatedScore(itemPairs);
		
		//might not be the most efficient, should probably use a HashMap for the itemPairs array
		for(int i = 0; i < itemPairs.size(); i++){
			ValueItemPair itemPair = itemPairs.get(i);
			ItemStack compStack = itemPair.getItemStack();
			int pointValue = itemPair.getValue();
			for(ItemStack invStack:inv.getStorageContents()){
				if(compStack.isSimilar(invStack)){
					points.add(i, invStack.getAmount(), pointValue);
				}
			}
		}
		
		return points;
	}
	public static List<AbbaGame> getGames() {
		return games;
	}
	public static void onChestOpen(InventoryOpenEvent e){
		
	}
	public static void onSignBreak(BlockBreakEvent e){
		
	}
	public static void onSignPlace(SignChangeEvent e){
		
	}
	
	
	public static void deserialize(List<AbbaGame> gameList){
		games = gameList;
		for(AbbaGame game:gameList){
			for(UUID id:game.getPlayerIDs()){
				playerGameMap.put(id, game);
			}
		}
	}
	
	
	
}
