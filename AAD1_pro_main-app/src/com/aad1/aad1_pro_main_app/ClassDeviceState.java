package com.aad1.aad1_pro_main_app;

public class ClassDeviceState {
	private String deviceName = "";
	private boolean deviceState = false;
	private String deviceIP = "";
	
	public boolean getDeviceState() {
		return deviceState;
	}
	public void setDeviceState(boolean deviceState) {
		this.deviceState = deviceState;
	}
	public String getDeviceIP() {
		return deviceIP;
	}
	public void setDeviceIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
}
