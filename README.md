# Clash
Clash is a PvP protection and API plugin for Bukkit-based Minecraft servers.

## Pull Requests
Pull requests are welcomed, but will only be accepted if they conform to [Oracle's Java code conventions](http://www.oracle.com/technetwork/java/codeconv-138413.html). "Self-documenting" and appropriately-commented code that makes clear the role of every method and variable is also appreciated.

## API
Clash provides a public-facing API that other plugins can use to determine and set whether a player is currently engaged in PvP, who else is involved in the same PvP situation, and how long a player is to be protected, among other things. To get started, import Clash.jar into your project and add it as a dependency or soft-dependency in your plugin.yml:

    depend: [Clash]      # your plugin will always load after Clash, and will only load if Clash is present on the server
    softdepend: [Clash]  # your plugin will load after Clash if Clash is present, but your plugin can run without Clash

Next, obtain the PlayerManager and/or PvpManager objects that handle data:

    PlayerManager playerManager = ClashPlugin.instance.getPlayerManager();
    PvpManager pvpManager       = ClashPlugin.instance.getPvpManager();

Now you can start fiddling with Clash's data. Here's how to determine whether a particular player is currently engaged in PvP:

    Player player /* = whatever player you want to find out about */;
    if (pvpManager.hasDataFor(player)) {
    	// the player is currently PvPing
    }
    else {
    	// the player is not currently engaged in PvP
    }

How about a more complicated example? Let's say you've developed another land protection plugin, and want to use Clash to make areas protected by your plugin safe from PvP. You can set a particular player to be protected as follows:
    
    Player player /* = whatever player you want to find out about */;
    PlayerData playerData = playerManager.getData(player);
    playerData.setProtectTime(ClashPlugin.defaultProtectTime); // time in seconds, assuming 20 ticks per second

What if you've developed a plugin that adds magic wands that can cast combat spells to the game, and want to restrict use of combat magic within Clash's safe zones? Again, it's pretty easy to mark two players as engaged in PvP:

    Player attacker /* = the attacking player */;
    Player target   /* = the player who was attacked */;
    pvpManager.registerConflict(attacker, target);

Note that Clash handles protection for you: if the target player you passed is supposed to be protected, they won't be registered as engaged in PvP, and pvpManager.registerConflict() will return *false* (which you can use to determine whether or not your plugin should inflict damage on the target player).