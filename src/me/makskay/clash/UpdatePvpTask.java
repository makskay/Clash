package me.makskay.clash;

public class UpdatePvpTask implements Runnable {
	private PvpManager pvpManager;
	
	public UpdatePvpTask(ClashPlugin plugin) {
		pvpManager = plugin.getPvpManager();
	}


	public void run() {
		for (PvpData pvpData : pvpManager.getManagedConflicts()) {
			pvpData.decrementTimeRemaining();
			if (pvpData.getTimeRemaining() < 1) {
				pvpManager.releasePvpData(pvpData);
			}
		}
	}
}
