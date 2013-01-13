package me.makskay.clash;

import com.massivecraft.factions.P;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.plugin.Plugin;

public class PvpListener implements Listener {
	private HashSet<EntityDamageByEntityEvent> managedEvents;
	private PlayerManager playerManager;
	private PvpManager pvpManager;
	private P factions;
	 
	public PvpListener(ClashPlugin plugin) {
		managedEvents = new HashSet<EntityDamageByEntityEvent>();
		playerManager = plugin.getPlayerManager();
		pvpManager    = plugin.getPvpManager();
		
      // Faction support
      Plugin plF = Bukkit.getPluginManager().getPlugin("Factions");
      if (plF == null || !(plF instanceof P)) {
         factions = null;
      } else {
         factions = (P) plF;
      }
	}
	
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!ClashPlugin.managedWorlds.contains(event.getEntity().getWorld().getName())) {
			return;
		}
		
		//event.setCancelled(true); // "hide" the event from other protection plugins - why? this breaks mcmmo, stat counters, etc
		managedEvents.add(event); // register this event as hidden so we know to handle it down the line
	}
	
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = false)
	public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
		if (!managedEvents.contains(event)) { 
			return; // if this event wasn't registered as hidden earlier, we don't care about it now
		}
		
		Entity entDamager = event.getDamager(), entDamaged = event.getEntity();
		if (entDamaged == null || entDamager == null || !(entDamaged instanceof Player)) {
			event.setCancelled(false);
			return;
		}
		
		Player plrDamager = null, plrDamaged = (Player) entDamaged;
		
		if (entDamager instanceof Player) {
			plrDamager = (Player) entDamager;
		} else if (entDamager instanceof Projectile) {
			Entity entShooter = ((Projectile) entDamager).getShooter();
			if (!(entShooter instanceof Player)) {
				event.setCancelled(false);
				return;
			}
			plrDamager = (Player) entShooter;
		} else {
			event.setCancelled(false);
			return;
		}
		
		// Check for Faction protection (ie: ally/neutral/enemy)
		if (factions != null) {
         if (!factions.entityListener.canDamagerHurtDamagee(event)) {
            // You can't damage an ally, Factions already alerts the user this, no need to repeat
            return;
         }
      }
		
		PlayerData datDamager = playerManager.getData(plrDamager);
		PlayerData datDamaged = playerManager.getData(plrDamaged);
		if (datDamager == null || datDamaged == null) {
		   return;
		}
		if (datDamaged.isProtected()) {
			plrDamager.sendMessage(ChatColor.RED + "You can't damage a protected player.");
			return;
		}
		
		datDamager.setProtectTime(0);
		pvpManager.registerConflict(plrDamager, plrDamaged);
		event.setCancelled(false);
	}
	
	
	@EventHandler
	public void onPotionSplash(PotionSplashEvent event) {
		LivingEntity entAttacker = event.getEntity().getShooter();
		if (!(entAttacker instanceof Player)) {
			return;
		}
		
		Player plrAttacker = (Player) entAttacker;
		HashSet<Player> affectedPlayers = new HashSet<Player>();
		
		for (LivingEntity entity : event.getAffectedEntities()) {
			if (entity instanceof Player) {
				affectedPlayers.add((Player) entity);
			}
		}
		
		for (Player plrDamaged : affectedPlayers) {
			PlayerData datDamaged = playerManager.getData(plrDamaged);
			if (datDamaged.isProtected()) {
				event.setCancelled(true);
				return;
			}
		}
		
		for (Player plrDamaged : affectedPlayers) {
			pvpManager.registerConflict(plrAttacker, plrDamaged);
		}
		
		playerManager.getData(plrAttacker).setProtectTime(0);
	}
	
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) {
			return;
		}
		
		Player player = (Player) entity;
		PvpData data  = pvpManager.getPvpDataFor(player);
		if (data != null) { // if a PvPing player is damaged
			data.resetTimeRemaining(); // extend the conflict they're participating in
		}
	}
	
	/**
	 * Removes the player who died from the PvP manager to prevent spawn camping
	 * 
	 * @param event
	 */
	@EventHandler (priority = EventPriority.MONITOR)
   public void onEntityDeath(EntityDeathEvent event) {
	   Entity entity = event.getEntity();
	   if (!(entity instanceof Player)) {
         return;
      }
      
	   pvpManager.releasePlayer((Player) entity);
	}
} 
