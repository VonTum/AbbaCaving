package me.lennartVH01;

import java.util.List;
import java.util.Map;

import me.lennartVH01.game.StackTester;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

public class Config{
	private Config(){}
	
	public static StackTester[] itemValues;
	public static StackTester[] contraband;
	
	public static int defaultDuration;
	public static int defaultPlayerCap;
	
	public static boolean scanContraband;
	
	//@SuppressWarnings("unchecked")
	public static void reload(FileConfiguration config){
		Permission.debug = config.getBoolean("Debug");
		
		defaultDuration = config.getInt("Duration");
		defaultPlayerCap = config.getInt("PlayerCap");
		scanContraband = config.getBoolean("ScanContraband");
		
		List<Object> itemValueList = (List<Object>) config.getList("ItemValues");
		List<Object> contrabandList = (List<Object>) config.getList("Contraband");
		
		itemValues = new StackTester[itemValueList.size()];
		contraband = new StackTester[itemValueList.size() + contrabandList.size()];
		
		
		for(int i = 0; i < contrabandList.size(); i++){
			contraband[i] = toStackTester(contrabandList.get(i));
		}
		for(int i = 0; i < itemValueList.size(); i++){
			StackTester tester = toStackTester(itemValueList.get(i));
			itemValues[i] = tester;
			contraband[contrabandList.size() + i] = tester;
		}
		
		
	}
	private static StackTester toStackTester(Object input){
		try{
			if(input instanceof String){
				return new StackTester((String) input);
			}else if(input instanceof Map){
				return new StackTester((Map<String, Object>) input);
			}
		}catch(InvalidConfigurationException ex){
			ex.printStackTrace();
		}
		return null;
	}
}
