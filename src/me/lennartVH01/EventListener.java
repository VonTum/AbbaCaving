package me.lennartVH01;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener{
	public Main plugin;
	public void initialize(Main plugin) {
		this.plugin = plugin;
		
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		AbbaTools.leave(e.getPlayer().getUniqueId());
	}
	
	
	
}
