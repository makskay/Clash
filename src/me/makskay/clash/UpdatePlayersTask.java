package me.makskay.clash;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
		
		Plugin plWg = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if (plWg == null || !(plWg instanceof WorldGuardPlugin)) {
			worldGuard = null;
		} else {
			worldGuard = (WorldGuardPlugin) plWg;
		}
		
		Plugin plGp = Bukkit.getPluginManager().getPlugin("GriefPrevention");
		if (plGp == null || !(plGp instanceof GriefPrevention)) {
			griefPrevention = null;
		} else {
			griefPrevention = (GriefPrevention) plGp;
		}
	}

	
	public void run() {
		for (Player player : playerManager.getManagedPlayers()) {
			if (pvpManager.hasDataFor(player)) {
				return; // player shouldn't be protected if in PVP
			}
			
			PlayerData data = playerManager.getData(player);
			Location loc    = player.getLocation();
			
			if (griefPrevention != null) {
				Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, true, null);
				if (claim != null && (ClashPlugin.claimsProtectEveryone || claim.allowBuild(player) == null)) {
					data.setProtectTime(ClashPlugin.defaultProtectTime);
					return;
				}
			}
			
			if (worldGuard != null) {
				RegionManager regionManager = worldGuard.getRegionManager(player.getWorld());
				ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
				if (!set.allows(DefaultFlag.PVP)) {
					data.setProtectTime(ClashPlugin.defaultProtectTime);
					return;
				}
			}
			
			data.decrementProtectTime();
		}
	}

}
