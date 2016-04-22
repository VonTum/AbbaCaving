package me.lennartVH01;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.io.File;
import java.io.IOException;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	
	private static HashSet<Material> transparentBlocks = new HashSet<Material>();
	static{
		transparentBlocks.add(Material.AIR);
		transparentBlocks.add(Material.SIGN);
		transparentBlocks.add(Material.SIGN_POST);
		transparentBlocks.add(Material.WALL_SIGN);
	}
	
	
	
	
	
	public static Main plugin;
	
	public FileConfiguration config;
	
	
	
	
	@Override
	public void onEnable(){
		AbbaCommand abbaCmd = new AbbaCommand();
		this.getCommand("abba").setExecutor((CommandExecutor) abbaCmd);
		this.getCommand("abba").setTabCompleter((TabCompleter) abbaCmd);
		AbbaAdminCommand abbaAdminCmd = new AbbaAdminCommand(this);
		this.getCommand("abbaadmin").setExecutor((CommandExecutor) abbaAdminCmd);
		this.getCommand("abbaadmin").setTabCompleter((TabCompleter) abbaAdminCmd);
		
		
		EventListener.initialize(this);
		AbbaGame.initialize(this);
		ConfigurationSerialization.registerClass(AbbaGame.class);
		
		config = this.getConfig();
		// Event handler
		
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		
		
		//Config
		this.saveDefaultConfig();
		
		
		
		// get Item Point Values
		List<Map<?,?>> itemPointMaps = config.getMapList("ItemValues");
		List<ValueItemPair> valueItemPairs = new ArrayList<ValueItemPair>();
		List<ItemStack> contrabandList = new ArrayList<ItemStack>();
		for(Map<?,?> itemMapGeneric:itemPointMaps){
			
			//Put the torches and pitchforks away, config.getMapList() *should* return Map<String, Object> anyways
			@SuppressWarnings("unchecked")
			Map<String, Object> itemMap = (Map<String, Object>) itemMapGeneric;
			
			if(itemMap.get("Value") == null)
				continue;
			int value = (int) itemMap.remove("Value");
			ItemStack stack = ItemStack.deserialize(itemMap);
			contrabandList.add(stack);
			stack.setAmount(0);
			valueItemPairs.add(new ValueItemPair(stack, value));
		}
		List<Map<?,?>> contrabandMap = config.getMapList("Contraband");
		
		for(Map<?,?> itemMapGeneric:contrabandMap){
			//I did it agian :D
			@SuppressWarnings("unchecked")
			Map<String, Object> itemMap = (Map<String, Object>) itemMapGeneric;
			
			contrabandList.add(ItemStack.deserialize(itemMap));
		}
		
		AbbaTools.initialize(this, valueItemPairs, contrabandList);
		
		FileConfiguration persist = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "persist.yml"));
		
		//I can see the rage in thy eyes
		@SuppressWarnings("unchecked")
		List<AbbaGame> abbaList = (List<AbbaGame>) persist.getList("Games");
		if(abbaList != null){
			AbbaTools.deserialize((List<AbbaGame>) abbaList); 
		}
	}
	@Override
	public void onDisable(){
		File persistFile = new File(getDataFolder(), "persist.yml");
		
		
		FileConfiguration persist = new YamlConfiguration();
		
		persist.set("Games", AbbaTools.getGames());
		
		
		try{
			persist.save(persistFile);
		}catch(IOException e){
			System.out.println("[ERROR] Could not save to persist.yml! Reason:" + e.getMessage());
		}
	}
}
