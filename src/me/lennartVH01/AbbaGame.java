package me.lennartVH01;



import java.util.List;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;


public class AbbaGame {
	
	public boolean open;
	public String name;
	public Location spawn;
	public int duration;
	public int playerCap;
	public List<Player> players;
	
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
		this.open = false;
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

	public void leave(Player p) {
		players.remove(p);
	}

	
}
