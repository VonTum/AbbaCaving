package me.lennartVH01;



import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class AbbaGame {
	public boolean open = false;
	public String name;
	public Location spawn;
	public static List<ValueItemPair> itemPairs;
	
	public static void initialize(List<ValueItemPair> valueItemPairs) {
		itemPairs = valueItemPairs;
		
	}
	
	
	
	
	public AbbaGame(Location spawn, String name){
		this.spawn = spawn;
		this.name = name;
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
	
	public void open(){
		open = true;
	}
	public void close(){
		open = false;
	}
	public boolean isOpen(){
		return open;
	}


	
}
