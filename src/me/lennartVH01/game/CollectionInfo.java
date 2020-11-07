package me.lennartVH01.game;

import org.bukkit.inventory.ItemStack;

public class CollectionInfo {
	public ItemStack item;
	public int count;
	
	public CollectionInfo(ItemStack item, int count){
		this.item = item;
		this.count = count;
	}
}
