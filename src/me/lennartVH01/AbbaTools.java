package me.lennartVH01;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;



public class AbbaTools {
	public static Main plugin;
	public static List<ValueItemPair> itemPairs;
	public static List<AbbaGame> games = new ArrayList<AbbaGame>();
	
	
	public static void initialize(Main plugin, List<ValueItemPair> valueItemPairs) {
		AbbaTools.plugin = plugin;
		itemPairs = valueItemPairs;
		
	}
	
	
	
	public static void create(String name, Location spawn){
		FileConfiguration config = plugin.getConfig();
		AbbaGame game = new AbbaGame(plugin, name, spawn, config.getInt("GameDuration"), config.getInt("PlayerCap"), config.getInt("CountdownTime"));
		games.add(game);
	}
	public static boolean removeAbbaGame(String name){
		for(int i = 0; i < games.size(); i++){
			AbbaGame game = games.get(i);
			if(game.name.equalsIgnoreCase(name)){
				game.destroy();
				games.remove(i);
				return true;
			}
		}
		return false;
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


	
}
