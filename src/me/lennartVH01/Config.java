package me.lennartVH01;

import java.util.List;
import java.util.Map;

import me.lennartVH01.game.StackTester;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

public class Config{
	private Config(){}
	
	public static StackTester[] itemValues;
	public static StackTester[] contraband;
	
	public static boolean redistributionEnabled;
	public static int[] topReturns;
	public static int otherPlayersValue;
	public static boolean otherPlayersShare;
	
	public static int defaultDuration;
	public static int defaultPlayerCap;
	
	public static boolean scanContraband;
	
	public static void reload(FileConfiguration config){
		Permission.debug = config.getBoolean("Debug");
		
		defaultDuration = config.getInt("Duration");
		defaultPlayerCap = config.getInt("PlayerCap");
		scanContraband = config.getBoolean("ScanContraband");
		
		
		@SuppressWarnings("unchecked")
		List<Object> itemValueList = (List<Object>) config.getList("ItemValues");
		@SuppressWarnings("unchecked")
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
		
		ConfigurationSection redistribution = config.getConfigurationSection("Redistribution");
		
		redistributionEnabled = redistribution.getBoolean("Enabled");
		@SuppressWarnings("unchecked")
		List<Integer> topRets = (List<Integer>) redistribution.getList("Top");
		topReturns = new int[topRets.size()];
		for(int i = 0; i < topRets.size(); i++) topReturns[i] = topRets.get(i);
		
		otherPlayersValue = redistribution.getInt("Other");
		otherPlayersShare = redistribution.getBoolean("OthersShare");
	}
	private static StackTester toStackTester(Object input){
		try{
			if(input instanceof String){
				return new StackTester((String) input);
			}else if(input instanceof Map){
				@SuppressWarnings("unchecked")
				Map<String, Object> mapInput = (Map<String, Object>) input;
				return new StackTester((Map<String, Object>) mapInput);
			}
		}catch(InvalidConfigurationException ex){
			ex.printStackTrace();
		}
		return null;
	}
}
