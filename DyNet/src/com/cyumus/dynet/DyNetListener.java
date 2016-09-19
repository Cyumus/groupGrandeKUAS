package com.cyumus.dynet;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.listeners.IDiscoveryListener;

public class DyNetListener implements IDiscoveryListener{
	@Override
	public void deviceDiscovered(RemoteXBeeDevice device){
		System.out.format(">> Device discovered: %s%n", device.toString());
		DyNet.getSingleton().deviceFound(device);
	}
	@Override
	public void discoveryError(String error){
		System.out.println(">> There was an error discovering devices: "+error);
	}
	@Override
	public void discoveryFinished(String error){
		String msg = ">> Discovery process finished ";
		msg += error == null ? "successfully":"due to the following error: "+error;
		msg += DyNet.getSingleton().foundAtLeastOne() ? ".\n>> "+DyNet.getSingleton().getFoundDevices()+" devices found.":" but no device has been found.";
		System.out.println(msg);
		System.exit(0);	
	}
}
