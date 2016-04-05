package me.lennartVH01;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	
	/*public static void main(String[] args){
		ItemStack test = new ItemStack(Material.STONE);
		
		
		
		
		System.out.println(test.serialize());
		
	}*/
	
	
	
	
	
	
	
	public final FileConfiguration config = this.getConfig();
	
	public static Main plugin;
	
	
	
	@Override
	public void onEnable(){
		
		//Config
		config.options().copyDefaults(true);
		saveConfig();
		
		// get Item Point Values
		List<Map<?,?>> itemPointMaps = config.getMapList("ItemValues");
		List<ValueItemPair> valueItemPairs = new ArrayList<ValueItemPair>();
		for(Map<?,?> itemMapGeneric:itemPointMaps){
			
			//Put the torches and pitchforks away, config.getMapList() *should* return Map<String, Object> anyways
			@SuppressWarnings("unchecked")
			Map<String, Object> itemMap = (Map<String, Object>) itemMapGeneric;
			
			if(itemMap.get("Value") == null)
				continue;
			int value = (int) itemMap.remove("Value");
			ItemStack stack = ItemStack.deserialize(itemMap);
			stack.setAmount(0);
			valueItemPairs.add(new ValueItemPair(stack, value));
		}
		
		AbbaGame.initialize(valueItemPairs);
		
		
	}
	@Override
	public void onDisable(){
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args){
		if(cmdLabel.equalsIgnoreCase("abba")){
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
			case "calc":
				if(sender.hasPermission("AbbaCaving.calc")){
					Player calcPlayer;
					if(args.length >= 2){
						calcPlayer = getServer().getPlayer(args[1]);
						if(calcPlayer == null){
							sender.sendMessage("§cUnable to find player \"" + args[1] + "\"");
							return false;
						}
					}else{
						if(sender instanceof Player){
							calcPlayer = (Player) sender;
						}else{
							sender.sendMessage("usage: /abba calc <Player>");
							return false;
						}
					}
					
					calculatedScore score = AbbaGame.calcScore(calcPlayer.getInventory());
					for(int i = 0; i < score.size(); i++){
						if(score.getItemCount(i) != 0)
							sender.sendMessage(score.getItemCount(i) + "x" + score.getItemStack(i).getType().toString() + ": " + score.getItemPoints(i));
					}
					sender.sendMessage("§aTotal Score: " + score.total);
					
					
				}else{
					sender.sendMessage("§cYou don't have permission to use this command!");
					return false;
				}
				
				
				
				break;
				
				
			//Admin commands
			case "create":
				if(args.length <= 5 && !(sender instanceof Player)){
					System.out.println("Must be ingame or specify world and coordinates");
					return false;
				}
				if(sender.hasPermission("AbbaCaving.create")){
					String gameName;
					Location gameSpawn = null;
					
					// set gameName
					if(args.length >= 2){
						gameName = args[1];
					}else{
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
						gameName = "Abba" + dateFormat.format(new Date());
						
					}
					
					// set gameSpawn
					if(args.length >= 5){
						double spawnX, spawnY, spawnZ;
						try{
							spawnX = Double.parseDouble(args[2]);
							spawnY = Double.parseDouble(args[3]);
							spawnZ = Double.parseDouble(args[4]);
						}catch(NumberFormatException e){
							return false;
						}
						if(args.length >= 6){
							World w = getServer().getWorld(args[5]);
							if(w != null){
								
								gameSpawn = new Location(w, spawnX, spawnY, spawnZ);
								
							}else{
								sender.sendMessage("§4Unknown world: \"" + args[5] + "\"");
								return false;
							}
						}
					}else{
						if(sender instanceof Player){
							gameSpawn = ((Player) sender).getLocation();
						}else{
							return false;
						}
					}
					
					
					
					AbbaGame abbaGame = new AbbaGame(gameSpawn, gameName);
					
					
				}
				break;
			case "remove":
				
				break;
			
			
			
			
			//Abba help
			default:
				sender.sendMessage("Usage:\n - §aabba join§r: Joins the Abba Match\n - §aabba leave§f: Leaves current Abba Game\n - §aabba info§f: Displays info about an Abba Match\n - §aabba create§f: Creates an Abba Game at current location\n - §aabba remove§f: Stops game\n - §aabba open§f: Opens a game\n - §aabba close§f: Closes a game");
				return false;
			}
			
		}
		
		
		
		
		return false;
	}
}
