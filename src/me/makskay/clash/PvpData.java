package me.makskay.clash;

import java.util.HashSet;

import org.bukkit.entity.Player;

public class PvpData {
	private int timeRemaining;
	private HashSet<String> players;
	
	
	public PvpData(Player attacker, Player attacked) {
		timeRemaining = ClashPlugin.defaultPvpTime;
		players = new HashSet<String>();
		players.add(attacker.getName());
		players.add(attacked.getName());
	}


	public HashSet<String> getPlayers() {
		return players;
	}
	

	public void registerPlayer(Player player) {
		registerPlayer(player.getName());
		resetTimeRemaining();
	}
	
	
	public void registerPlayer(String playername) {
		players.add(playername);
	}
	
	
	public void releasePlayer(String playername) {
		players.remove(playername);
	}
	
	
	public int getTimeRemaining() {
		return timeRemaining;
	}
	
	
	public void decrementTimeRemaining() {
		timeRemaining--;
	}
	
	
	public void resetTimeRemaining() {
		timeRemaining = ClashPlugin.defaultPvpTime;
	}
}
