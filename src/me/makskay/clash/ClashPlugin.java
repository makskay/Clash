package me.makskay.clash;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ClashPlugin extends JavaPlugin {
	private PlayerManager playerManager;
	private PvpManager pvpManager;
	static int defaultProtectTime, defaultPvpTime;
	static boolean claimsProtectEveryone, punishPvpLoggers;
	static List<String> managedWorlds;
	public static ClashPlugin instance;
	
	public void onEnable() {
		instance = this;
		
		//FileConfiguration config = YamlConfiguration.loadConfiguration(new File("config.yml"));
		FileConfiguration config = getConfig();
		
		managedWorlds = config.getStringList("ManagedWorlds");
		if (managedWorlds == null || managedWorlds.size() == 0) {
			managedWorlds = new ArrayList<String>();
			
			// For public release, just add every worldname on the server
			for (World world : Bukkit.getWorlds()) {
				managedWorlds.add(world.getName());
			}
			
			// For testing
			/*managedWorlds.add("survival");
			managedWorlds.add("survival_nether");
			managedWorlds.add("survival_the_end");
			managedWorlds.add("world");*/
		}
		
		defaultProtectTime = config.getInt("DefaultProtectTime", 15);
		defaultPvpTime     = config.getInt("DefaultPvpTime", 15);
		
		// TODO Remyserver defaults -- for public release, flip them both
		claimsProtectEveryone = config.getBoolean("ClaimsProtectEveryone", true /*false*/);
		punishPvpLoggers      = config.getBoolean("PunishPvpLoggers", false /*true*/);
		
		playerManager = new PlayerManager(this);
		pvpManager    = new PvpManager(this);
		
		Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PvpListener(this), this);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new UpdatePlayersTask(this), 20L, 20L);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new UpdatePvpTask(this), 20L, 20L);
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
	
	public PvpManager getPvpManager() {
		return pvpManager;
	}
}
