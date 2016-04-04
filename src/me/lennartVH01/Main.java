package me.lennartVH01;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	public final Logger logger = Logger.getLogger("Minecraft");
	public final FileConfiguration config = this.getConfig();
	
	public static Main plugin;
	
	
	
	@Override
	public void onEnable(){
		//Config
		if(!config.contains("BlockValues")){
			config.addDefault("BlockValues.iron_ore", 1);
			config.addDefault("BlockValues.redstone_ore", 2);
			config.addDefault("BlockValues.gold_ore", 4);
			config.addDefault("BlockValues.lapis_ore", 8);
			config.addDefault("BlockValues.diamond_ore", 10);
			config.addDefault("BlockValues.emerald_ore", 10);
		}
		
		config.addDefault("GameIndex", 1);
		config.options().copyDefaults(true);
		this.saveConfig();
		
		
		
		
		
		PluginDescriptionFile pluginDescFile = this.getDescription();
		this.logger.info(pluginDescFile.getName() + " [" + pluginDescFile.getVersion() + "] Initialized");
	}
	@Override
	public void onDisable(){
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args){
		if(cmdLabel.equalsIgnoreCase("abba")){
			Player p;
			if(sender instanceof Player){
				p = (Player) sender;
			}else{
				logger.info("Must be ingame to use commands");
				return false;
			}
			if(args.length == 0){
				args = new String[]{"help"};
			}
			switch(args[0].toLowerCase()){
			case "join":
				
				break;
			case "leave":
				
				break;
			case "info":
				
				break;
			
			//Admin commands
			case "create":
				if(p.hasPermission("AbbaCaving.create")){
					String gameName;
					if(args.length > 1){
						gameName = args[1];
					}else{
						gameName = "Abba" + config.getInt("GameIndex");
					}
					config.set("GameIndex", config.getInt("GameIndex") + 1);
					AbbaGame abbaGame = new AbbaGame(p.getLocation(), gameName);
					
				}
				break;
			case "remove":
				
				break;
			
			//Abba help
			default:
				p.sendMessage("Usage:\n - §aabba join§r: Joins the Abba Match\n - §aabba leave§f: Leaves current Abba Game\n - §aabba info§f: Displays info about an Abba Match\n - §aabba create§f: Creates an Abba Game at current location\n - §aabba remove§f: Stops game\n - §aabba open§f: Opens a game\n - §aabba close§f: Closes a game");
				return false;
			}
		}
		
		
		
		
		return false;
	}
}
