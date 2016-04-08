package me.lennartVH01;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;



public class AbbaTools {
	public static List<ValueItemPair> itemPairs;
	public static List<AbbaGame> games = new ArrayList<AbbaGame>();
	
	public static void initialize(List<ValueItemPair> valueItemPairs) {
		itemPairs = valueItemPairs;
		
	}
	
	
	
	public static void create(String name, Location spawn, int duration){
		AbbaGame game = new AbbaGame(name, spawn, duration);
		games.add(game);
	}
	public static boolean removeAbbaGame(String name){
		for(int i = 0; i < games.size(); i++){
			if(games.get(i).name.equalsIgnoreCase(name)){
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
	
	public static calculatedScore calcScore(Inventory inv){
		calculatedScore points = new calculatedScore(itemPairs);
		
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
