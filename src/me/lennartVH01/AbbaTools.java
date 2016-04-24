package me.lennartVH01;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.lennartVH01.AbbaGame.JoinResult;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;




public class AbbaTools{
	public static Main plugin;
	public static List<ValueItemPair> itemPairs;
	public static List<ItemStack> contraband;
	private static List<AbbaGame> games = new ArrayList<AbbaGame>();
	
	private static Map<UUID, AbbaGame> playerGameMap = new HashMap<UUID, AbbaGame>();
	//private static Map<Location, AbbaGame> chestGameMap = new HashMap<Location, AbbaGame>();
	
	
	
	public static void initialize(Main plugin, List<ValueItemPair> valueItemPairs, List<ItemStack> contraband) {
		AbbaTools.plugin = plugin;
		itemPairs = valueItemPairs;
		AbbaTools.contraband = contraband;
	}
	
	
	
	public static void create(String name, Location spawn){
		FileConfiguration config = plugin.getConfig();
		AbbaGame game = new AbbaGame(name, spawn, config.getInt("GameDuration"), config.getInt("PlayerCap"));
		games.add(game);
	}
	public static boolean removeAbbaGame(String name){
		AbbaGame game = getAbbaGame(name);
		return removeAbbaGame(game);
	}
		
	public static boolean removeAbbaGame(AbbaGame game){
		if(game == null){
			return false;
		}
		
		for(UUID id:game.getPlayerIDs()){
			playerGameMap.remove(id);// remove people from game Map
		}
		game.destroy();				// remove people from internal game Map
		games.remove(game);
		return true;
	}
	public static AbbaGame getAbbaGame(String name){
		for(AbbaGame game:games){
			if(game.name.equalsIgnoreCase(name)){
				return game;
			}
		}
		
		return null;
	}
	public static AbbaGame getAbbaGame(){
		if(games.size() >= 1){
			return games.get(0);
		}else{
			return null;
		}
	}
	public static AbbaGame getAbbaGame(Player p){
		return playerGameMap.get(p.getUniqueId());
	}
	
	public static AbbaGame leave(UUID id){
		AbbaGame game = playerGameMap.remove(plugin.getServer().getPlayer(id).getUniqueId());
		if(game != null){
			game.removePlayer(id);
			return game;
		}
		return null;
	}
	public static AbbaGame.JoinResult join(Player p, AbbaGame game){
		leave(p.getUniqueId());
		
		JoinResult result = game.addPlayer(p);
		if(result == JoinResult.SUCCESS){
			playerGameMap.put(p.getUniqueId(), game);
		}
		return result;
		
	}
	public static boolean isInGame(Player p){
		return playerGameMap.containsKey(p.getUniqueId());
	}
	
	
	public static List<ItemStack> getContraband(Inventory i){
		List<ItemStack> totalContraband = new ArrayList<ItemStack>();
		if(!plugin.getConfig().getBoolean("ScanContraband")){
			return totalContraband;
		}
		for(ItemStack detectionStack:contraband){
			for(ItemStack testStack:i.getStorageContents()){
				if(detectionStack.isSimilar(testStack)){
					totalContraband.add(detectionStack);
					break;	//breaks out of inner loop to go to next detect stack to get 
				}
			}
		}
		return totalContraband;
	}
	public static boolean hasContraband(Inventory i){
		if(!plugin.getConfig().getBoolean("ScanContraband")){
			return false;
		}
		for(ItemStack detectionStack:contraband){
			for(ItemStack testStack:i.getStorageContents()){
				if(detectionStack.isSimilar(testStack)){
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	public static CalculatedScore calcScore(Inventory inv){
		CalculatedScore points = new CalculatedScore(itemPairs);
		
		//might not be the most efficient, should probably use a HashMap for the itemPairs array
		for(int i = 0; i < itemPairs.size(); i++){
			ValueItemPair itemPair = itemPairs.get(i);
			ItemStack compStack = itemPair.getItemStack();
			int pointValue = itemPair.getValue();
			for(ItemStack invStack:inv.getStorageContents()){
				if(compStack.isSimilar(invStack)){
					points.add(i, invStack.getAmount(), pointValue);
				}
			}
		}
		
		return points;
	}
	public static List<AbbaGame> getGames() {
		return games;
	}
	public static void onChestOpen(InventoryOpenEvent e){
		
	}
	public static void onSignBreak(BlockBreakEvent e){
		
	}
	public static void onSignPlace(SignChangeEvent e){
		if(e.getPlayer().hasPermission(Permission.REGISTER_CHEST.toString())){
			if(e.getLine(0).equalsIgnoreCase("[abba]")){
				AbbaGame game;
				if(e.getLine(1).equals("")){
					game = getAbbaGame();
				}else{
					game = getAbbaGame(e.getLine(1));
				}
				if(game != null){
					if(game.addChest((Chest) BlockUtils.getAttachedBlock(e.getBlock()), (Sign) e.getBlock())){
						e.setLine(0, "§9[Abba]");
					}
				}
			}
		}
	}
	
	
	public static void deserialize(List<AbbaGame> gameList){
		
		for(AbbaGame game:gameList){
			if(game != null){
				games.add(game);
				for(UUID id:game.getPlayerIDs()){
					playerGameMap.put(id, game);
				}
			}
		}
	}
	
	
	
}
