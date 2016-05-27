package me.lennartVH01.game;

import org.bukkit.inventory.ItemStack;

public class CalculatedScore{
	public int total = 0;
	public ItemStack[] itemValues;
	public int[] itemCount;
	public int[] itemPoints;
	
	public CalculatedScore(ItemStack[] itemValues){
		int size = itemValues.length;
		this.itemValues = itemValues;
		itemCount = new int[size];
		itemPoints = new int[size];
	}
	
	public void add(int i, int amount){
		itemPoints[i] += amount * itemValues[i].getAmount();
		itemCount[i] += amount;
		total += amount * itemValues[i].getAmount();
	}
	
	public int getTotal(){
		return total;
	}
	public ItemStack getItemStack(int i){
		return itemValues[i];
	}
	public int getItemCount(int i){
		return itemCount[i];
	}
	public int getItemPoints(int i){
		return itemPoints[i];
	}
	
	
	public int size(){
		return itemCount.length;
	}
}