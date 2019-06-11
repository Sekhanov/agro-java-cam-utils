package ru.skhanov.ipcam;

import be.teletask.onvif.OnvifManager;
import be.teletask.onvif.models.OnvifDevice;

public class AppOnvif {
	
	public static void main(String[] args) {
		onvifDevice();
	}
	
	public static void onvifDevice() {
		OnvifManager onvifManager = new OnvifManager();
		OnvifDevice onvifDevice = new OnvifDevice("192.168.100.170", "admin", "admin");
		onvifManager.getServices(onvifDevice, (d, s) -> {System.out.println(s.getStreamURIPath());});
		onvifManager.getDeviceInformation(onvifDevice, (d, i) -> {System.out.println(i.toString());});
		onvifManager.getMediaProfiles(onvifDevice, (d, mp) -> {
			onvifManager.getMediaStreamURI(onvifDevice, mp.get(0), (dev, omp, uri) -> {System.out.println(uri);});
		});
	}
}
