package me.lennartVH01;

import org.bukkit.Location;

public class AbbaGame {
	public boolean open = false;
	public String name;
	public Location spawn;
	public AbbaGame(Location spawn, String name){
		this.spawn = spawn;
		this.name = name;
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
}
