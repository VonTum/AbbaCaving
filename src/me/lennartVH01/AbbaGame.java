package me.lennartVH01;



import org.bukkit.Location;


public class AbbaGame {
	
	public boolean open;
	public String name;
	public Location spawn;
	public int duration;
	
	public AbbaGame(String name, Location spawn, int duration){
		this.name = name;
		this.spawn = spawn;
		this.duration = duration;
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
	public String getName(){
		return name;
	}
}
