package me.lennartVH01;



import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;


public class AbbaGame implements ConfigurationSerializable{
	private static Comparator<Tuple2<UUID, CalculatedScore>> scoreComparer = new Comparator<Tuple2<UUID, CalculatedScore>>() {

		@Override
		public int compare(Tuple2<UUID, CalculatedScore> arg0, Tuple2<UUID, CalculatedScore> arg1) {
			return arg1.getArg2().getTotal() - arg0.getArg2().getTotal();
		}
		
	};
	
	
	private static Main plugin;
	public int taskId;
	public boolean open = false;
	public GameState state = GameState.WAITING;
	public String name;
	public Location spawn;
	public long endTime;
	public int duration;
	public int playerCap;
	public List<UUID> players;
	public List<Tuple2<UUID, CalculatedScore>> endStats = new ArrayList<Tuple2<UUID, CalculatedScore>>();
	public Map<UUID, Chest> playerChests = new HashMap<UUID, Chest>();
	public Map<UUID, List<ItemStack>> leftovers = new HashMap<UUID, List<ItemStack>>();
	public List<ItemStack> collectedItems = new ArrayList<ItemStack>();
	public List<Chest> chests = new ArrayList<Chest>();
	public List<Sign> signs = new ArrayList<Sign>();
	private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	private Objective abbaObjective = scoreboard.registerNewObjective("AbbaStats", "dummy");
	private Score timer = abbaObjective.getScore("Time Remaining");
	
	public static void initialize(Main plugin){
		AbbaGame.plugin = plugin;
	}
	public AbbaGame(String name, Location spawn, int duration, int playerCap){
		this.name = name;
		this.spawn = spawn;
		this.duration = duration;
		this.playerCap = playerCap;
		if(playerCap == -1){
			players = new ArrayList<UUID>();
		}else{
			players = new ArrayList<UUID>(playerCap);
		}
		abbaObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		for(ValueItemPair pair:AbbaTools.itemPairs){
			ItemStack stack = pair.getItemStack().clone();
			stack.setAmount(0);
			collectedItems.add(stack);
		}
	}
	
	
	public void destroy(){
		for(Sign s:signs){
			s.setLine(0, "");
			//s.setLine(1, "");
			//s.setLine(2, "");
			s.update();
		}
		for(int i = players.size() - 1; i >= 0; i--){	//loop array backwards for speed
			removePlayer(players.get(i));
			//probably tp people to spawn or something
			
		}
		stopClock();
	}
	
	public void start() {
		// TODO Add stuff like tp people to cave if neccecary
		if(state == GameState.WAITING){
			endTime = System.currentTimeMillis() + 5000;
			state = GameState.COUNTDOWN;
			for(UUID id:players){
				Player p = plugin.getServer().getPlayer(id);
				p.sendMessage("§cGame starting!");
			}
		}else if(state == GameState.PAUSED){
			endTime = System.currentTimeMillis() + 1000 * duration;
			state = GameState.RUNNING;
			for(UUID id:players){
				plugin.getServer().getPlayer(id).sendMessage("§cGame continuing!");
			}
			startClock(0);
		}
		
		
		
		startClock(20);
	}
	public void pause(){
		//TODO Implement pausing
		stopClock();
		state = GameState.PAUSED;
		duration = (int) ((endTime - System.currentTimeMillis())/1000);
	}
	
	
	private void startClock(long delay){
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				switch(state){
				case COUNTDOWN:
					if((endTime - System.currentTimeMillis())/1000 <= 0){
						state = GameState.RUNNING;
						endTime = System.currentTimeMillis() + duration * 1000;
						timer.setScore(duration);
						
						for(UUID id:players){
							Player p = plugin.getServer().getPlayer(id);
							p.sendMessage("§cGOGOGO!");
							p.setScoreboard(scoreboard);
						}
						//do stuff when countdown is finished here
						
					}else{
						String message = "§c" + (endTime - System.currentTimeMillis())/1000;
						for(UUID id:players){
							plugin.getServer().getPlayer(id).sendMessage(message);
						}
					}
					
					
					
					break;
				case RUNNING:
					int timeRemaining = (int) (endTime - System.currentTimeMillis())/1000;
					if(timeRemaining > 0){
						timer.setScore(timeRemaining);
					}else{
						//end game
						stopClock();
						state = GameState.FINISHED;
						scoreboard.resetScores("Time Remaining");
						
						for(UUID id:players){
							Player p = plugin.getServer().getPlayer(id);
							p.sendMessage("Game ended!");
							p.teleport(spawn);
							
							
							
						}
						
					}
					
					break;
				default:
					break;
				
				}
				
			}
		}, delay, 20);
	}
	private void stopClock(){
		Bukkit.getScheduler().cancelTask(taskId);
	}
	
	public void calcScores() {
		for(UUID uuid:playerChests.keySet()){
			Chest chest = playerChests.get(uuid);
			Sign sign = signs.get(chests.indexOf(chest));
			
			Player p = plugin.getServer().getPlayer(uuid);
			
			Score score = abbaObjective.getScore(p.getName());
			CalculatedScore stat = AbbaTools.calcScore(chest.getInventory());
			score.setScore(stat.getTotal());
			sign.setLine(2, "" + stat.getTotal());
			endStats.add(new Tuple2<UUID, CalculatedScore>(p.getUniqueId(), stat));
			
			ItemStack[] stacks = chest.getInventory().getStorageContents();
			
			for(ItemStack item:stacks){
				for(ItemStack total:collectedItems){
					if(total.isSimilar(item)){
						total.setAmount(total.getAmount() + item.getAmount());
						chest.getInventory().remove(item);
						continue;
					}
				}
			}
		}
		endStats.sort(scoreComparer);
		
		List<Integer> LeaderBoardWeights = plugin.getConfig().getIntegerList("WinWeights.Top");
		int AllPlayerWeight = plugin.getConfig().getInt("WinWeights.All");
		int totalWeight = 0;
		for(int i = 0; i < Math.min(LeaderBoardWeights.size(), playerChests.size()); i++){
			totalWeight += LeaderBoardWeights.get(i);
		}
		if(playerChests.size() > LeaderBoardWeights.size()){
			totalWeight += (playerChests.size() - LeaderBoardWeights.size()) * AllPlayerWeight;
		}
		
		for(int i = 0; i < Math.min(LeaderBoardWeights.size(), playerChests.size()); i++){
			Inventory chestInv = playerChests.get(endStats.get(i).arg1).getInventory();
			List<ItemStack> leftoverList = new ArrayList<ItemStack>();
			leftovers.put(endStats.get(i).arg1, leftoverList);
			for(ItemStack stack: collectedItems){
				ItemStack newStack = stack.clone();
				newStack.setAmount((int) Math.ceil(stack.getAmount() * LeaderBoardWeights.get(i) / totalWeight));
				stack.setAmount(stack.getAmount() - newStack.getAmount());
				
				if(newStack.getAmount() != 0){
					leftoverList.addAll(chestInv.addItem(newStack).values());
				}
				
				
				
			}
			totalWeight -= LeaderBoardWeights.get(i);
			
		}
		for(int i = LeaderBoardWeights.size(); i < playerChests.size(); i++){
			Inventory chestInv = playerChests.get(endStats.get(i).arg1).getInventory();
			List<ItemStack> leftoverList = new ArrayList<ItemStack>();
			leftovers.put(endStats.get(i).arg1, leftoverList);
			for(ItemStack stack: collectedItems){
				int newAmount = (int) (stack.getAmount() / (playerChests.size() - LeaderBoardWeights.size()));
				int left = stack.getAmount() % (playerChests.size() - LeaderBoardWeights.size());
				if(i - LeaderBoardWeights.size() < left){
					newAmount++;
				}
				ItemStack newStack = stack.clone();
				newStack.setAmount(newAmount);
				
				leftoverList.addAll(chestInv.addItem(newStack).values());
			}
		}
	}
	
	public void open(){
		open = true;
	}
	public void close(){
		open = false;
	}
	public boolean addChest(Chest chest, Sign sign){
		if(!chests.contains(chest)){
			chests.add(chest);
			signs.add(sign);
			sign.setLine(0, "§9[" + name + "]");
			sign.update();
			
			return true;
		}
		return false;
	}
	
	
	
	public boolean isOpen(){
		return open;
	}
	public boolean hasRoom(){
		return (playerCap == -1 || players.size() < playerCap);
	}
	public int getPlayerCount(){
		return players.size();
	}
	public int getMaxPlayers(){
		if(playerCap == -1 || chests.size() < playerCap){
			return chests.size();
		}else{
			return playerCap;
		}
	}
	public String getName(){
		return name;
	}
	public Location getSpawn(){
		return spawn;
	}
	public JoinResult addPlayer(Player p){
		if(!Permission.hasPermission(p, Permission.JOIN_FULL) && players.size() >= playerCap){
			return JoinResult.FAIL_FULL;
		}
		if(players.size() >= chests.size()){
			return JoinResult.FAIL_NOCHEST;
		}
		if(!open && !Permission.hasPermission(p, Permission.JOIN_CLOSED)){
			return JoinResult.FAIL_CLOSED;
		}
		//TODO Add stuff here for whitelist aswell
		
		
		players.add(p.getUniqueId());
		int index = playerChests.size();
		Chest chest = chests.get(index);
		Sign sign = signs.get(index);
		sign.setLine(1, p.getName());
		sign.update();
		playerChests.put(p.getUniqueId(), chest);
		
		return JoinResult.SUCCESS;
	}
	public void removePlayer(UUID id) {
		players.remove(id);
		Player p = plugin.getServer().getPlayer(id);
		p.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
		int index = chests.indexOf(playerChests.remove(p.getUniqueId()));
		Sign sign = signs.get(index);
		sign.setLine(1, "");
		sign.update();
	}
	

	public GameState getState(){
		return state;
	}

	public long getEndTime() {
		return endTime;
	}
	
	
	
	public void setDuration(long newDuration) {
		duration = (int) newDuration;
		
	}

	public void setEndTime(long newEndTime) {
		endTime = newEndTime;
		
	}
	
	// ==SERIALIZATION==
	
	//TODO Make dis serializable
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> abbaMap = new HashMap<String, Object>();
		abbaMap.put("Name", name);
		abbaMap.put("Spawn", spawn);
		
		abbaMap.put("Open", open);
		switch(state){
		case WAITING:
		case COUNTDOWN:
			abbaMap.put("State", "WAITING");
			break;
		case RUNNING:
		case PAUSED:
			abbaMap.put("State", "PAUSED");
			break;
		case FINISHED:
			abbaMap.put("State", "FINISHED");
			break;
		case CONCLUDED:
			abbaMap.put("State", "CONCLUDED");
			break;
		}
		
		abbaMap.put("Duration", duration);
		abbaMap.put("PlayerCap", playerCap);
		
		
		return abbaMap;
	}
	public static AbbaGame deserialize(Map<String, Object> inputMap){
		AbbaGame game = new AbbaGame((String) inputMap.get("Name"), (Location) inputMap.get("Spawn"), (int) inputMap.get("Duration"), (int) inputMap.get("PlayerCap"));
		
		switch((String) inputMap.get("State")){
		
		case "PAUSED":
			game.state = GameState.PAUSED;
			break;
		case "FINISHED":
			game.state = GameState.FINISHED;
			break;
		case "CONCLUDED":
			game.state = GameState.CONCLUDED;
			break;
		default:	// WAITING also refers back to this state
			game.state = GameState.WAITING;
		}
		
		
		
		
		return game;
	}
	public List<UUID> getPlayerIDs() {
		return players;
	}
	
	public enum JoinResult{
		SUCCESS,
		FAIL_FULL,
		FAIL_CLOSED,
		FAIL_WHITELIST,
		FAIL_NOCHEST,
		
	}
	public enum GameState {
		WAITING, 
		COUNTDOWN,
		RUNNING, 
		PAUSED, 
		FINISHED,
		CONCLUDED
		
	}
}
