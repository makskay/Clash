package me.makskay.clash;

public class PlayerData {
	private int protectTime;
	
	public PlayerData() {
		protectTime = ClashPlugin.defaultProtectTime;
	}
	
	public boolean isProtected() {
		return protectTime > 0;
	}
	
	public void decrementProtectTime() {
		if (isProtected()) {
			protectTime--;
		}
	}
	
	public void setProtectTime(int newProtectTime) {
		protectTime = newProtectTime;
	}
}
