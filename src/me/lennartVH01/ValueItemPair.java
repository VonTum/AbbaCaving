package me.lennartVH01;

import org.bukkit.inventory.ItemStack;

public class ValueItemPair {
	public ItemStack itemStack;
	public int value;
	public ValueItemPair(ItemStack itemStack, int value){
		this.itemStack = itemStack;
		this.value = value;
	}
	public ItemStack getItemStack() {
		return itemStack;
	}
	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
}
