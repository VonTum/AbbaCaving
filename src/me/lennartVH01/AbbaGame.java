package me.lennartVH01;



import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.TrapDoor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;


public class AbbaGame {
	private Main plugin;
	public int taskId;
	public boolean open = false;
	public GameState state = GameState.WAITING;
	public String name;
	public Location spawn;
	public long endTime;
	public int duration;
	public int countDownTime;
	public int playerCap;
	public List<Player> players;
	public Map<UUID, CalculatedScore> endStats = new HashMap<UUID, CalculatedScore>();
	public Map<UUID, Inventory> playerChests = new HashMap<UUID, Inventory>();
	private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	private Objective abbaObjective = scoreboard.registerNewObjective("AbbaStats", "dummy");
	private Score timer = abbaObjective.getScore("Time Remaining");
	
	public AbbaGame(Main plugin, String name, Location spawn, int duration, int playerCap, int countDownTime){
		this.plugin = plugin;
		this.name = name;
		this.spawn = spawn;
		this.duration = duration;
		this.playerCap = playerCap;
		this.countDownTime = countDownTime;
		if(playerCap == -1){
			players = new ArrayList<Player>();
		}else{
			players = new ArrayList<Player>(playerCap);
		}
		abbaObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public void start() {
		// TODO Add stuff like tp people to cave if neccecary
		endTime = System.currentTimeMillis() + 1000 * countDownTime;
		state = GameState.COUNTDOWN;
		
		for(Player p:players){
			p.sendMessage("§cGame starting!");
		}
		
		startClock(20);
	}
	
	
	private void startClock(long delay){
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				switch(state){
				case COUNTDOWN:
					if(endTime - System.currentTimeMillis() <= 0){
						state = GameState.RUNNING;
						endTime = System.currentTimeMillis() + duration * 1000;
						timer.setScore(duration);
						
						for(Player p:players){
							p.sendMessage("§cGOGOGO!");
							p.setScoreboard(scoreboard);
						}
						//do stuff when countdown is finished here
						
					}else{
						String message = "§c" + (endTime - System.currentTimeMillis())/1000;
						for(Player p:players){
							p.sendMessage(message);
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
						
						for(Player p:players){
							p.sendMessage("Game ended!");
							p.teleport(spawn);
							
							
							Score score = abbaObjective.getScore(p.getName());
							CalculatedScore stat = AbbaTools.calcScore(p.getInventory());
							score.setScore(stat.getTotal());
							endStats.put(p.getUniqueId(), stat);
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
	
	public void open(){
		open = true;
	}
	public void close(){
		open = false;
	}
	public void claimChest(Player p, Sign sign){
		if(playerChests.containsKey(p.getUniqueId())){
			p.sendMessage("§cYou already have a chest!");
			return;
		}
		Block blockBelowSign = spawn.getWorld().getBlockAt(sign.getX(), sign.getY() - 1, sign.getZ());
		if(blockBelowSign instanceof InventoryHolder){
			InventoryHolder chest = (InventoryHolder) blockBelowSign;
			
			
			
			if(playerChests.containsValue(chest.getInventory())){
				p.sendMessage("§cThis chest is already claimed!");
				return;
			}else{
				playerChests.put(p.getUniqueId(), chest.getInventory());
				sign.setLine(2, p.getName());
				
			}
			
		}
		
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
		return playerCap;
	}
	public String getName(){
		return name;
	}
	public Location getSpawn(){
		return spawn;
	}

	public void addPlayer(Player p) {
		players.add(p);
		
	}
	public void leave(Player p) {
		players.remove(p);
		playerChests.remove(p.getUniqueId());
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

	public Map<UUID, Inventory> getPlayerChests() {
		return playerChests;
		
	}
	
	
	
}
