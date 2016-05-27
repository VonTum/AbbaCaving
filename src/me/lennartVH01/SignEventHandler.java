package me.lennartVH01;

import me.lennartVH01.game.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class SignEventHandler implements Listener{
	@EventHandler
	public static void onPlayerQuit(PlayerQuitEvent e){
		GameManager.leave(e.getPlayer());
	}
}
