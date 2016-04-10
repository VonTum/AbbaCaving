package me.lennartVH01;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class CalculatedScore {
	public int total = 0;
	public ItemStack[] items;
	public int[] itemCount;
	public int[] itemPoints;
	
	public CalculatedScore(List<ValueItemPair> itemPairs){
		int size = itemPairs.size();
		items = new ItemStack[size];
		itemCount = new int[size];
		itemPoints = new int[size];
		for(int i = 0; i < itemPairs.size(); i++){
			items[i] = itemPairs.get(i).getItemStack();
		}
	}


	public void add(int i, int amount, int itemValue) {
		itemPoints[i] += amount * itemValue;
		itemCount[i] += amount;
		total += amount * itemValue;
	}


	public int getTotal() {
		return total;
	}
	public ItemStack getItemStack(int i){
		return items[i];
	}
	public int getItemCount(int i){
		return itemCount[i];
	}
	public int getItemPoints(int i){
		return itemPoints[i];
	}


	public int size() {
		return itemCount.length;
	}
}
