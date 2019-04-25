package com.bwssystems.hue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bwssystems.HABridge.BridgeSettingsDescriptor;
import com.bwssystems.HABridge.NamedIP;
import com.bwssystems.HABridge.api.hue.DeviceResponse;
import com.bwssystems.HABridge.api.hue.HueApiResponse;

public class HueHome {
    private static final Logger log = LoggerFactory.getLogger(HueHome.class);
	private Map<String, HueInfo> hues;
	private String theHUERegisteredUser;
	
	public HueHome(BridgeSettingsDescriptor bridgeSettings) {
		hues = new HashMap<String, HueInfo>();
		if(!bridgeSettings.isValidHue())
			return;
		Iterator<NamedIP> theList = bridgeSettings.getHueaddress().getDevices().iterator();
		while(theList.hasNext()) {
			NamedIP aHue = theList.next();
      		hues.put(aHue.getName(), new HueInfo(aHue, this));
		}
		theHUERegisteredUser = null;
	}

	public List<HueDevice> getDevices() {
		log.debug("consolidating devices for hues");
		Iterator<String> keys = hues.keySet().iterator();
		ArrayList<HueDevice> deviceList = new ArrayList<HueDevice>();
		while(keys.hasNext()) {
			String key = keys.next();
			HueApiResponse theResponse = hues.get(key).getHueApiResponse();
			if(theResponse != null) {
				Map<String, DeviceResponse> theDevices = theResponse.getLights();
				if(theDevices != null) {
					Iterator<String> deviceKeys = theDevices.keySet().iterator();
					while(deviceKeys.hasNext()) {
						String theDeviceKey = deviceKeys.next();
						HueDevice aNewHueDevice = new HueDevice();
						aNewHueDevice.setDevice(theDevices.get(theDeviceKey));
						aNewHueDevice.setHuedeviceid(theDeviceKey);
						aNewHueDevice.setHueaddress(hues.get(key).getHueAddress().getIp());
						aNewHueDevice.setHuename(key);
						deviceList.add(aNewHueDevice);
					}
				}
				else {
					deviceList = null;
					break;
				}
			}
			else
				log.warn("Cannot get lights for Hue with name: " + key);
		}
		return deviceList;
	}

	public String getTheHUERegisteredUser() {
		return theHUERegisteredUser;
	}

	public void setTheHUERegisteredUser(String theHUERegisteredUser) {
		this.theHUERegisteredUser = theHUERegisteredUser;
	}
}
