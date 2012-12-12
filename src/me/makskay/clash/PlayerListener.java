package me.makskay.clash;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class PlayerListener implements Listener {
	private PlayerManager playerManager;
	private PvpManager    pvpManager;
	
	
	public PlayerListener(ClashPlugin plugin) {
		playerManager = plugin.getPlayerManager();
		pvpManager    = plugin.getPvpManager();
	}
	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (ClashPlugin.managedWorlds.contains(player.getWorld().getName())) {
			playerManager.registerPlayer(player);
		}
	}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (ClashPlugin.managedWorlds.contains(player.getWorld().getName())) {
			playerManager.releasePlayer(player);
			if (ClashPlugin.punishPvpLoggers && pvpManager.hasDataFor(player)) {
				player.setHealth(0);
				event.setQuitMessage(player.getName() + " logged out during PvP");
			}
		}
	}
	
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if (ClashPlugin.managedWorlds.contains(player.getWorld().getName())) {
			playerManager.registerPlayer(player);
		} else {
			playerManager.releasePlayer(player);
		}
	}
	
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		if (!ClashPlugin.managedWorlds.contains(player.getWorld().getName()) || event.getCause().equals(TeleportCause.ENDER_PEARL)) {
			return;
		} else if (pvpManager.hasDataFor(player)) {
			event.setCancelled(true);
		}
	}
}
