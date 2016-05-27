package me.lennartVH01.util;

import java.util.Map;


public class ItemUtil {
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
	/*public static ItemStack[] takeOffItems(ItemStack item){
		return takeOffItems(item, item.getAmount());
	}
	public static ItemStack[] takeOffItems(ItemStack item, int maxTakeOff){
		ItemStack[] stacks = new ItemStack[(maxTakeOff+item.getMaxStackSize()-1) / item.getMaxStackSize()]; //take ceil of itemStack size/64
		
		item.setAmount(item.getAmount() - maxTakeOff);
		for(int i = 0; i < maxTakeOff / item.getMaxStackSize(); i++){
			stacks[i] = item.clone();
			stacks[i].setAmount(item.getMaxStackSize());
		}
		int remainder = maxTakeOff % item.getMaxStackSize();
		if(remainder != 0){
			stacks[stacks.length] = item.clone();
			stacks[stacks.length].setAmount(remainder);
			
		}
		return stacks;
	}*/
}
