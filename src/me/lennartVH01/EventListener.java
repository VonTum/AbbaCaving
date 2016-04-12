package me.lennartVH01;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
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
	
	@EventHandler
	public void onInventoryEdit(InventoryInteractEvent e){
		UUID p = e.getView().getPlayer().getUniqueId();
		if(!plugin.playerMap.get(p).getPlayerChests().get(p).equals(e.getInventory())){
			((Cancellable) e).setCancelled(true);
		}
	}
	public void onChestOpen(InventoryOpenEvent e){
		
	}
}
