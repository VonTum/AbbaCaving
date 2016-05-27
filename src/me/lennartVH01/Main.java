package me.lennartVH01;

import java.io.File;

import me.lennartVH01.game.GameManager;
import me.lennartVH01.util.ChatUtil;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	public static void main(String[] args){
		String penis = "{\"text\":\"Json Test\",\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"SQUID\"}}";
		System.out.println(ChatUtil.fromRawJSON(penis).toString());
	}
	@Override
	public void onEnable(){
		//copy files from jar to data folder
		saveDefaultConfig();
		saveResource("lang.yml", false);
		
		Config.reload(getConfig());
		Messages.reload(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "lang.yml")));
		
		
		CommandHandler abbaCmd = new CommandHandler(this);
		this.getCommand("abba").setExecutor((CommandExecutor) abbaCmd);
		this.getCommand("abba").setTabCompleter((TabCompleter) abbaCmd);
		
		getServer().getPluginManager().registerEvents(new SignEventHandler(), this);
		
	}
	@Override
	public void onDisable(){
		GameManager.cleanup();
		
		/*File persistFile = new File(getDataFolder(), "persist.yml");
		
		
		FileConfiguration persist = new YamlConfiguration();
		
		persist.set("Games", abbaFactory.getGames());
		
		
		try{
			persist.save(persistFile);
		}catch(IOException e){
			System.out.println("[ERROR] Could not save to persist.yml! Reason:" + e.getMessage());
		}*/
	}
}
