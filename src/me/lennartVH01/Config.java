package me.lennartVH01;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class Config{
	private Config(){}
	
	public static ItemStack[] itemValues;
	public static ItemStack[] contraband;
	
	public static int defaultDuration;
	public static int defaultPlayerCap;
	
	public static boolean scanContraband;
	
	@SuppressWarnings("unchecked")
	public static void reload(FileConfiguration config){
		Permission.debug = config.getBoolean("Debug");
		
		defaultDuration = config.getInt("Duration");
		defaultPlayerCap = config.getInt("PlayerCap");
		scanContraband = config.getBoolean("ScanContraband");
		
		List<Map<?, ?>> itemValueMapList = config.getMapList("ItemValues");
		List<Map<?, ?>> contrabandMapList = config.getMapList("Contraband");
		
		itemValues = new ItemStack[itemValueMapList.size()];
		contraband = new ItemStack[itemValueMapList.size() + contrabandMapList.size()];
		
		
		for(int i = 0; i < contrabandMapList.size(); i++){
			contraband[i] = ItemStack.deserialize((Map<String, Object>) contrabandMapList.get(i));
		}
		for(int i = 0; i < itemValueMapList.size(); i++){
			ItemStack stack = ItemStack.deserialize((Map<String, Object>) itemValueMapList.get(i));
			itemValues[i] = stack;
			contraband[contrabandMapList.size() + i] = stack;
		}
		
		
	}
}
