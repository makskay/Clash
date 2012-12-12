package me.makskay.clash;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.entity.Player;

public class PlayerManager {
	private HashMap<Player, PlayerData> dataPerPlayer;
	
	Set<Player> getManagedPlayers() {
		return dataPerPlayer.keySet();
	}
	
	
	public PlayerManager(ClashPlugin plugin) {
		dataPerPlayer = new HashMap<Player, PlayerData>();
	}
	
	
	void registerPlayer(Player player) {
		dataPerPlayer.put(player, new PlayerData());
	}
	
	
	void releasePlayer(Player player) {
		dataPerPlayer.remove(player);
	}
	
	/**
	 * 
	 * @param player The Player about whom data should be obtained.
	 * @return A PlayerData object if any exists; null if Clash doesn't have any data about this Player.
	 */
	public PlayerData getData(Player player) {
		return dataPerPlayer.get(player);
	}
}
