package me.makskay.clash;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PvpManager {
	private HashSet<PvpData> data;
	private PlayerManager playerManager;
	
	
	public PvpManager(ClashPlugin plugin) {
		data = new HashSet<PvpData>();
		playerManager = plugin.getPlayerManager();
	}
	
	
	PvpData getPvpDataFor(Player player) {
		for (PvpData pvp : data) {
			if (pvp.getPlayers().contains(player.getName())) {
				return pvp;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Called whenever one Player causes damage, directly or indirectly, to another.
	 * @param attacker The Player who caused the damage.
	 * @param attacked The Player who was damaged.
	 * @return True if the damaged player wasn't protected and the combat registration was completed successfully,
	 * 		   false if he was protected and the combat registration didn't go through.
	 */
	public boolean registerConflict(Player attacker, Player attacked) {
		PlayerData attackedData = playerManager.getData(attacked);
		if (attackedData != null && attackedData.isProtected()) {
			attacker.sendMessage(ChatColor.RED + "You can't damage a protected player.");
			return false;
		}
		
		PvpData datAttacker = getPvpDataFor(attacker);
		PvpData datAttacked = getPvpDataFor(attacked);
		
		if (datAttacker != null) { // if the attacker has data
			if (datAttacked != null) { // and the attacked also has data
				for (String playername : datAttacked.getPlayers()) {
					datAttacker.registerPlayer(playername); // add all members of the attacked's data to the attacker's
				}
			
				releasePvpData(datAttacked); // release the attacked's data
				return true; // the data are merged
			}
			
			else { // if the attacker has data but the attacked doesn't
				datAttacker.registerPlayer(attacked.getName()); // add the attacked to the attacker's data
				if (ClashPlugin.punishPvpLoggers) {
					attacked.sendMessage(ChatColor.DARK_RED + "You're in PvP combat. Don't log out until combat ends!");
				}
				return true;
			}
		}
		
		else if (datAttacked != null) { // if the attacked has data but the attacker doesn't
			datAttacked.registerPlayer(attacker.getName()); // add the attacker to the attacked's data
			if (ClashPlugin.punishPvpLoggers) {
				attacker.sendMessage(ChatColor.DARK_RED + "You're in PvP combat. Don't log out until combat ends!");
			}
			return true;
		}
		
		// if neither have data
		PvpData pvpData = new PvpData(attacker, attacked); // create a new data object
		if (ClashPlugin.punishPvpLoggers) {
			attacker.sendMessage(ChatColor.DARK_RED + "You're in PvP combat. Don't log out until combat ends!");
			attacked.sendMessage(ChatColor.DARK_RED + "You're in PvP combat. Don't log out until combat ends!");
		}
		data.add(pvpData);
		return true;
	}
	
	
	public boolean hasDataFor(Player player) {
		return getPvpDataFor(player) != null;
	}
	
	
	void releasePlayer(Player player) {
		releasePlayer(player.getName());
	}
	

	void releasePlayer(String playername) {
		for (PvpData pvp : data) {
			pvp.releasePlayer(playername);
			
			if (ClashPlugin.punishPvpLoggers) {
				try {
					Bukkit.getPlayer(playername).sendMessage(ChatColor.GREEN + "Combat ended. It's safe to log off.");
				} catch (NullPointerException ex) {
					// do nothing
				}
			}
				
			if (pvp.getPlayers().size() == 0) {
				releasePvpData(pvp);
			}
		}
	}
	
	
	void releasePvpData(PvpData pvpData) {
		data.remove(pvpData);
	}
	
	
	HashSet<PvpData> getManagedConflicts() {
		return data;
	}
}
