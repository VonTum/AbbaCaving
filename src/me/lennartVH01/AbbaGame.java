package me.lennartVH01;



import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.HumanEntity;
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
	
	AbbaChestPlayerList chestList;
	
	
	public Map<UUID, List<ItemStack>> leftovers = new HashMap<UUID, List<ItemStack>>();
	public List<ItemStack> collectedItems = new ArrayList<ItemStack>();
	
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
		
		chestList = new AbbaChestPlayerList(playerCap);
		
		for(ValueItemPair pair:AbbaTools.itemPairs){
			ItemStack stack = pair.getItemStack().clone();
			stack.setAmount(0);
			collectedItems.add(stack);
		}
	}
	
	
	public void destroy(){
		for(AbbaChest chestSignPair:chestList.getChests()){
			Sign s = chestSignPair.getSign();
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
		for(AbbaChest abbaChest:chestList.getOccupiedChests()){
			UUID id = abbaChest.getOwnerId();
			
			Chest chest = abbaChest.getChest();
			Sign sign = abbaChest.getSign();
			
			Player p = plugin.getServer().getPlayer(id);
			
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
		for(int i = 0; i < Math.min(LeaderBoardWeights.size(), players.size()); i++){
			totalWeight += LeaderBoardWeights.get(i);
		}
		if(players.size() > LeaderBoardWeights.size()){
			totalWeight += (players.size() - LeaderBoardWeights.size()) * AllPlayerWeight;
		}
		
		for(int i = 0; i < Math.min(LeaderBoardWeights.size(), players.size()); i++){
			Inventory chestInv = chestList.get(endStats.get(i).arg1).getChest().getInventory();
			
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
		int playerChestSize = chestList.getOccupiedChests().size();
		for(int i = LeaderBoardWeights.size(); i < playerChestSize; i++){
			Inventory chestInv = chestList.get(endStats.get(i).arg1).getChest().getInventory();
			List<ItemStack> leftoverList = new ArrayList<ItemStack>();
			leftovers.put(endStats.get(i).arg1, leftoverList);
			for(ItemStack stack: collectedItems){
				int newAmount = (int) (stack.getAmount() / (playerChestSize - LeaderBoardWeights.size()));
				int left = stack.getAmount() % (playerChestSize - LeaderBoardWeights.size());
				if(i - LeaderBoardWeights.size() < left){
					newAmount++;
				}
				ItemStack newStack = stack.clone();
				newStack.setAmount(newAmount);
				
				leftoverList.addAll(chestInv.addItem(newStack).values());
			}
		}
	}
	
	
	public boolean addChest(Chest chest, Sign sign){
		if(chestList.contains(chest)){
			return false;
		}
		chestList.addEmptyChest(new AbbaChest(chest, sign));
		sign.setLine(0, "§9[" + name + "]");
		sign.update();
		
		return true;
	}
	
	
	public void setOpen(boolean open){
		this.open = open;
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
		if(playerCap == -1 || chestList.size() < playerCap){
			return chestList.size();
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
	public void onChestOpen(Inventory inv, HumanEntity player) {
		
	}
	
	
	
	
	public JoinResult addPlayer(Player p){
		if(!Permission.hasPermission(p, Permission.JOIN_FULL) && players.size() >= playerCap){
			return JoinResult.FAIL_FULL;
		}
		if(players.size() >= chestList.size()){
			return JoinResult.FAIL_NOCHEST;
		}
		if(!open && !Permission.hasPermission(p, Permission.JOIN_CLOSED)){
			return JoinResult.FAIL_CLOSED;
		}
		//TODO Add stuff here for whitelist aswell
		
		ItemStack[] contraband = AbbaTools.getContraband(p.getInventory());
		if(contraband.length >= 1){
			p.sendMessage("You cannot join the game with any of the following items!");
			for(ItemStack stack:contraband){
				p.sendMessage(" - " + stack.getType().toString().toLowerCase());
			}
		}
		AbbaChest claimedChest = chestList.claimChest(p.getUniqueId());
		players.add(p.getUniqueId());
		
		
		claimedChest.getSign().setLine(1, p.getName());
		claimedChest.getSign().update();
		
		
		return JoinResult.SUCCESS;
	}
	public void removePlayer(UUID id) {
		players.remove(id);
		plugin.getServer().getPlayer(id).setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
		AbbaChest aChest = chestList.freeChest(id);
		
		aChest.getSign().setLine(1, "");
		aChest.getSign().update();
	}
	

	public GameState getState(){
		return state;
	}

	public long getEndTime() {
		return endTime;
	}
	
	
	
	public void setDuration(int duration) {
		this.duration = duration;
		
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
		
		List<String> playerChestList = new ArrayList<String>();
		for(AbbaChest aChest:chestList.getOccupiedChests()){
			playerChestList.add(String.format("%d;%d;%d;%s", aChest.getChest().getX(), aChest.getChest().getY(), aChest.getChest().getZ(), aChest.getOwnerId().toString()));
		}
		for(AbbaChest aChest:chestList.getFreeChests()){
			playerChestList.add(String.format("%d;%d;%d", aChest.getChest().getX(), aChest.getChest().getY(), aChest.getChest().getZ()));
		}
		abbaMap.put("Chests", playerChestList);
		//TODO SERIALIZE PLAYERS
		
		
		
		return abbaMap;
	}
	
	@SuppressWarnings("unchecked")
	public static AbbaGame deserialize(Map<String, Object> inputMap){
		AbbaGame game = new AbbaGame((String) inputMap.get("Name"), (Location) inputMap.get("Spawn"), (int) inputMap.get("Duration"), (int) inputMap.get("PlayerCap"));
		
		game.setOpen((boolean) inputMap.get("Open"));
		game.setDuration((int) inputMap.get("Duration"));
		game.setPlayerCap((int) inputMap.get("PlayerCap"));
		
		for(String input:(List<String>) inputMap.get("Chests")){
			String[] args = input.split(";");
			Block sign = game.spawn.getWorld().getBlockAt(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			
			AbbaChest aChest = game.new AbbaChest((Chest) BlockUtils.getAttachedBlock(sign), (Sign) sign);
			if(args.length >= 4){
				UUID id = UUID.fromString(args[3]);
				game.chestList.addEmptyChest(aChest);
				game.chestList.claimChest(id);
				game.players.add(id);
				
			}
		}
		
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
	public void setPlayerCap(int playerCap) {
		this.playerCap = playerCap;
	}
	public int getPlayerCap(){
		return playerCap;
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
	
	
	
	public class AbbaChest{
		private Chest chest;
		private Sign sign;
		private UUID ownerId;
		public AbbaChest(Chest chest, Sign sign){
			this.chest = chest;
			this.sign = sign;
		}
		
		
		
		
		public Chest getChest(){return chest;}
		public void setChest(Chest chest){this.chest = chest;}
		public Sign getSign(){return sign;}
		public void setSign(Sign sign){this.sign = sign;}
		public UUID getOwnerId(){return ownerId;}
		public void setOwnerId(UUID ownerId){this.ownerId = ownerId;}
	}
	
	public class AbbaChestPlayerList{
		private int playerLength = 0;
		private List<AbbaChest> chests;
		//creates An ArrayList with two "sublists, each building toward eachother"
		public AbbaChestPlayerList(int initialSize){
			chests = new ArrayList<AbbaChest>(initialSize);
		}
		
		
		public int size() {
			return chests.size();
		}


		public AbbaChest claimChest(UUID id){
			AbbaChest chest = chests.get(playerLength);
			chest.setOwnerId(id);
			playerLength++;
			return chest;
		}
		public AbbaChest freeChest(UUID id){
			for(int i = 0; i < playerLength; i++){
				AbbaChest chest = chests.get(i);
				if(chest.getOwnerId().equals(id)){
					chest.setOwnerId(null);
					playerLength--;
					Collections.swap(chests, i, playerLength);
					return chest;
				}
			}
			return null;
		}
		public void remove(AbbaChest chest){
			chests.remove(chest);
		}
		public void addEmptyChest(AbbaChest c){
			chests.add(c);
		}
		public List<AbbaChest> getOccupiedChests(){
			return chests.subList(0, playerLength);
		}
		public List<AbbaChest> getFreeChests(){
			return chests.subList(playerLength, chests.size());
		}
		public List<AbbaChest> getChests(){
			return chests;
		}
		public AbbaChest get(int i){
			return chests.get(i);
		}
		public AbbaChest get(UUID id){
			for(int i = 0; i < playerLength; i++){
				if(chests.get(i).getOwnerId().equals(id)){
					return chests.get(i);
				}
			}
			return null;
		}
		public boolean contains(AbbaChest abbaChest){
			for(AbbaChest aChest:chests){
				if(aChest.equals(abbaChest)){
					return true;
				}
			}
			return false;
		}
		public boolean contains(Chest chest){
			for(AbbaChest aChest:chests){
				if(chest.equals(aChest.getChest())){
					return true;
				}
			}
			return false;
		}
		public boolean contains(Sign sign){
			for(AbbaChest aChest:chests){
				if(sign.equals(aChest.getSign())){
					return true;
				}
			}
			return false;
		}
	}
}
