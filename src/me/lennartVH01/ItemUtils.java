package me.lennartVH01;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;


public class ItemUtils {
	public static boolean isMapInMap(Map<String, Object> testMap, Map<String, Object> elderMap){
		for(String key:elderMap.keySet()){
			Object o = testMap.get(key);
			if(o == null){
				return false;
			}
			
			if(o instanceof Map<?, ?>){
				
			}
		}
		return true;
	}
	public static ItemStack[] getItemStacks(ItemStack item){
		return getItemStacks(item, item.getAmount());
	}
	public static ItemStack[] getItemStacks(ItemStack item, int maxTakeOff){
		ItemStack[] stacks = new ItemStack[(maxTakeOff+63) / 64]; //take ceil of itemStack size/64
		
		item.setAmount(item.getAmount() - maxTakeOff);
		for(int i = 0; i < maxTakeOff / 64; i++){
			stacks[i] = item.clone();
			stacks[i].setAmount(64);
		}
		int remainder = maxTakeOff % 64;
		if(remainder != 0){
			stacks[stacks.length] = item.clone();
			stacks[stacks.length].setAmount(remainder);
			
		}
		return stacks;
	}
}
