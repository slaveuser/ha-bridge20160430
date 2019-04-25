package com.bwssystems.harmony;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bwssystems.HABridge.BridgeSettingsDescriptor;
import com.bwssystems.HABridge.IpList;
import com.bwssystems.HABridge.NamedIP;

import net.whistlingfish.harmony.config.Activity;
import net.whistlingfish.harmony.config.Device;

public class HarmonyHome {
    private static final Logger log = LoggerFactory.getLogger(HarmonyHome.class);
	private Map<String, HarmonyServer> hubs;
	private Boolean isDevMode;

	public HarmonyHome(BridgeSettingsDescriptor bridgeSettings) {
		super();
        isDevMode = Boolean.parseBoolean(System.getProperty("dev.mode", "false"));
		hubs = new HashMap<String, HarmonyServer>();
		if(!bridgeSettings.isValidHarmony() && !isDevMode)
			return;
		if(isDevMode) {
			NamedIP devModeIp = new NamedIP();
			devModeIp.setIp("10.10.10.10");
			devModeIp.setName("devMode");
			List<NamedIP> theList = new ArrayList<NamedIP>();
			theList.add(devModeIp);
			IpList thedevList = new IpList();
			thedevList.setDevices(theList);
			bridgeSettings.setHarmonyAddress(thedevList);
		}
		Iterator<NamedIP> theList = bridgeSettings.getHarmonyAddress().getDevices().iterator();
		while(theList.hasNext()) {
			NamedIP aHub = theList.next();
	      	try {
	      		hubs.put(aHub.getName(), HarmonyServer.setup(bridgeSettings, isDevMode, aHub));
			} catch (Exception e) {
		        log.error("Cannot get harmony client (" + aHub.getName() + ") setup, Exiting with message: " + e.getMessage(), e);
		        return;
			}
		}
	}

	public void shutdownHarmonyHubs() {
		if(isDevMode)
			return;
		Iterator<String> keys = hubs.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			hubs.get(key).getMyHarmony().shutdown();
		}
	}

	public HarmonyHandler getHarmonyHandler(String aName) {
		HarmonyHandler aHandler = null;
		if(aName == null || aName.equals("")) {
			aName = "default";
		}

		if(hubs.get(aName) == null) {
			Set<String> keys = hubs.keySet();
			if(!keys.isEmpty()) {
				aHandler = hubs.get(keys.toArray()[0]).getMyHarmony();
			}
			else
				aHandler = null;
		}
		else
			aHandler = hubs.get(aName).getMyHarmony();
		return aHandler;
	}
	
	public List<HarmonyActivity> getActivities() {
		Iterator<String> keys = hubs.keySet().iterator();
		ArrayList<HarmonyActivity> activityList = new ArrayList<HarmonyActivity>();
		while(keys.hasNext()) {
			String key = keys.next();
			Iterator<Activity> activities = hubs.get(key).getMyHarmony().getActivities().iterator();
			while(activities.hasNext()) {
				HarmonyActivity anActivity = new HarmonyActivity();
				anActivity.setActivity(activities.next());
				anActivity.setHub(key);
				activityList.add(anActivity);
			}
		}
		return activityList;
	}
	public List<HarmonyActivity> getCurrentActivities() {
		Iterator<String> keys = hubs.keySet().iterator();
		ArrayList<HarmonyActivity> activityList = new ArrayList<HarmonyActivity>();
		while(keys.hasNext()) {
			String key = keys.next();
			Iterator<Activity> activities = hubs.get(key).getMyHarmony().getActivities().iterator();
			while(activities.hasNext()) {
				HarmonyActivity anActivity = new HarmonyActivity();
				anActivity.setActivity(activities.next());
				anActivity.setHub(key);
				activityList.add(anActivity);
			}
		}
		return activityList;
	}
	public List<HarmonyDevice> getDevices() {
		Iterator<String> keys = hubs.keySet().iterator();
		ArrayList<HarmonyDevice> deviceList = new ArrayList<HarmonyDevice>();
		while(keys.hasNext()) {
			String key = keys.next();
			Iterator<Device> devices = hubs.get(key).getMyHarmony().getDevices().iterator();
			while(devices.hasNext()) {
				HarmonyDevice aDevice = new HarmonyDevice();
				aDevice.setDevice(devices.next());
				aDevice.setHub(key);
				deviceList.add(aDevice);
			}
		}
		return deviceList;
	}
}
