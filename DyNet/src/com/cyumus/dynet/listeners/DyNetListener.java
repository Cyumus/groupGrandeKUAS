package com.cyumus.dynet.listeners;

import com.cyumus.dynet.DyNet;
import com.cyumus.util.Printer;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.listeners.IDiscoveryListener;

public class DyNetListener implements IDiscoveryListener{
	/**
	 * Notifies that a remote device was discovered in the network.
	 * 
	 * @param discoveredDevice The discovered remote device.
	 * 
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 */
	@Override
	public void deviceDiscovered(RemoteXBeeDevice device){
		DyNet.getSingleton().getDiscovering().deviceFound(device);
	}
	
	/**
	 * Notifies that an error occurred during the discovery process.
	 * 
	 * <p>This method is only called when an error occurs but does not cause 
	 * the process to finish.</p>
	 * 
	 * @param error The error message.
	 */
	@Override
	public void discoveryError(String error){
		Printer.error(">> There was an error discovering devices: "+error);
	}

	/**
	 * Notifies that the discovery process has finished.
	 * 
	 * @param error The error message, or {@code null} if the process finished 
	 *              successfully.
	 */
	@Override
	public void discoveryFinished(String error){
		DyNet.getSingleton().getDiscovering().discoveryFinished(error);
	}
}
