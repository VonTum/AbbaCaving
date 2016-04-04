package me.lennartVH01;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	public final FileConfiguration config = this.getConfig();
	
	public static Main plugin;
	
	
	
	@Override
	public void onEnable(){
		//Config
		if(config.get("BlockValues") == null){
			config.addDefault("BlockValues.iron_ore", 1);
			config.addDefault("BlockValues.redstone_ore", 2);
			config.addDefault("BlockValues.gold_ore", 4);
			config.addDefault("BlockValues.lapis_ore", 8);
			config.addDefault("BlockValues.diamond_ore", 10);
			config.addDefault("BlockValues.emerald_ore", 10);
		}
		
		config.options().copyDefaults(true);
		this.saveConfig();
		
		
		
		
		
		PluginDescriptionFile pluginDescFile = this.getDescription();
		System.out.println(pluginDescFile.getName() + " [" + pluginDescFile.getVersion() + "] Initialized");
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
				System.out.println("Must be ingame to use commands");
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
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
						gameName = "Abba" + dateFormat.format(new Date());
					}
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
