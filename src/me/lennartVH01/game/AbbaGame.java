package me.lennartVH01.game;

import java.util.List;





import net.minecraft.server.v1_9_R2.IChatBaseComponent;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface AbbaGame{
	
	
	public boolean join(Player p);
	public void leave(Player p);
	public void leaveAll();
	
	//SETTERS GETTERS
	//public void setState(GameState state);
	public GameState getState();
	public void setPlayerCap(int playerCap);
	public int getPlayerCap();
	public List<Player> getPlayers();
	
	public void setOpen(boolean open);
	public boolean isOpen();
	public boolean hasRoom();
	public int getPlayerCount();
	public String getName();
	public Location getSpawn();
	public int getDuration();
	public void setTimeLeft(int newTime);
	public int getTimeLeft();
	
	public void broadcast(String message);
	public void broadcast(IChatBaseComponent message);
	public void tpAll(Location loc);
	
	public boolean start(CommandSender sender);
	public boolean pause(CommandSender sender);
	
	public void destroy();
	
	
	public enum GameState {
		WAITING, 
		COUNTDOWN,
		RUNNING, 
		PAUSED, 
		FINISHED
		
	}
	
}
