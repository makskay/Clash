package me.makskay.clash;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class UpdatePlayersTask implements Runnable {
	private PlayerManager playerManager;
	private PvpManager pvpManager;
	private WorldGuardPlugin worldGuard;
	private GriefPrevention griefPrevention;
	
	public UpdatePlayersTask(ClashPlugin plugin) {
		playerManager = plugin.getPlayerManager();
		pvpManager    = plugin.getPvpManager();
		
		// WorldGuard support
		Plugin plWg = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if (plWg == null || !(plWg instanceof WorldGuardPlugin)) {
			worldGuard = null;
		} else {
			worldGuard = (WorldGuardPlugin) plWg;
		}
		
		// Grief Prevention support
		Plugin plGp = Bukkit.getPluginManager().getPlugin("GriefPrevention");
		if (plGp == null || !(plGp instanceof GriefPrevention)) {
			griefPrevention = null;
		} else {
			griefPrevention = (GriefPrevention) plGp;
		}
	}

	@Override
	public void run() {
		for (Player player : playerManager.getManagedPlayers()) {
			if (pvpManager.hasDataFor(player)) {
				return; // player shouldn't be protected if in PVP
			}
			
			executeGriefPrevention(player);
			executeWorldGuardProtection(player);
			
			if (playerManager.getData(player).getProtectTime() == 1) {
			   player.sendMessage(ChatColor.RED + "You are no longer protected from PVP.");
			}
			playerManager.getData(player).decrementProtectTime();
		}
	}
	
	/**
	 * Executes protection that WorldGuard sets
	 * 
	 * @param player - The player to verify WorldGuard protection on
	 */
	private void executeWorldGuardProtection(Player player) {
	   if (worldGuard != null) {
         RegionManager regionManager = worldGuard.getRegionManager(player.getWorld());
         ApplicableRegionSet set = regionManager.getApplicableRegions(player.getLocation());
         if (!set.allows(DefaultFlag.PVP)) {
            //ClashPlugin.instance.getLogger().info("");
            if (!playerManager.getData(player).isProtected()) {
               player.sendMessage(ChatColor.GREEN + "You are now protected from PVP.");
            }
            playerManager.getData(player).setProtectTime(ClashPlugin.defaultProtectTime);
            return;
         }
      }
	}
	
	/**
    * Executes protection that GriefPrevention sets
    * 
    * @param player - The player to verify GriefPrevention protection on
    */
   private void executeGriefPrevention(Player player) {
      if (griefPrevention != null) {
         Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, null);
         if (claim != null && (ClashPlugin.claimsProtectEveryone || claim.allowBuild(player) == null)) {
            if (!playerManager.getData(player).isProtected()) {
               player.sendMessage(ChatColor.GREEN + "You are now protected from PVP.");
            }
            playerManager.getData(player).setProtectTime(ClashPlugin.defaultProtectTime);
            return;
         }
      }
   }

}
