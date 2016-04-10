package me.lennartVH01;



import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;


public class AbbaGame {
	
	public boolean open = false;
	public boolean running = false;
	public boolean ended = true;
	public String name;
	public Location spawn;
	public long endTime;
	public int duration;
	public int playerCap;
	public List<Player> players;
	public List<Chest> chests;
	public Map<UUID, CalculatedScore> endStats = new HashMap<UUID, CalculatedScore>();
	public Scoreboard sb;
	
	public AbbaGame(String name, Location spawn, int duration, int playerCap){
		this.name = name;
		this.spawn = spawn;
		this.duration = duration;
		this.playerCap = playerCap;
		if(playerCap == -1){
			players = new ArrayList<Player>();
		}else{
			players = new ArrayList<Player>(playerCap);
		}
	}
	
	public void open(){
		open = true;
	}
	public void close(){
		open = false;
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
	public void addChest(Chest c){
		chests.add(c);
	}
	public List<Chest> getChests(){
		return chests;
	}
	public void leave(Player p) {
		players.remove(p);
	}
	public void onSecond(){
		
	}

	public boolean isRunning() {
		return running;
	}

	public long getEndTime() {
		return endTime;
	}

	public void start() {
		// TODO Add stuff like tp people to cave if neccecary
		endTime = System.currentTimeMillis() + 1000 * duration;
		running = true;
		
	}

	public void finishGame() {
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective abbaObjective = sb.registerNewObjective("Abba Scores", "dummy");
		abbaObjective.setDisplaySlot(DisplaySlot.SIDEBAR);;
		for(Player p:players){
			p.sendMessage("Game ended!");
			p.teleport(spawn);
			
			
			Score score = abbaObjective.getScore(p.getName());
			CalculatedScore stat = AbbaTools.calcScore(p.getInventory());
			score.setScore(stat.getTotal());
			endStats.put(p.getUniqueId(), stat);
		}
		for(Player p:players){
			p.setScoreboard(sb);
		}
		running = false;
		ended = true;
	}

	public void setDuration(long newDuration) {
		duration = (int) newDuration;
		
	}

	public void setEndTime(long newEndTime) {
		endTime = newEndTime;
		
	}
	
}
