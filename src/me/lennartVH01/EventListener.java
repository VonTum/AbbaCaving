package me.lennartVH01;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener{
	public Main plugin;
	public void initialize(Main plugin) {
		this.plugin = plugin;
		
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		AbbaGame game = plugin.playerMap.remove(e.getPlayer().getUniqueId());
		if(game != null){
			game.leave(e.getPlayer());
		}
	}
	
	//Don't use doublechests, they kinda break stuff
	@EventHandler
	public void onChestRemove(BlockBreakEvent e){
		
	}
}
