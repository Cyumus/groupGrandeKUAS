package com.cyumus.dynet;

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
		DyNet.getSingleton().deviceFound(device);
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
		DyNet.getSingleton().error(error);
	}

	/**
	 * Notifies that the discovery process has finished.
	 * 
	 * @param error The error message, or {@code null} if the process finished 
	 *              successfully.
	 */
	@Override
	public void discoveryFinished(String error){
		String msg = ">> Discovery process finished ";
		msg += error == null ? "successfully":"due to the following error: "+error;
		msg += DyNet.getSingleton().foundAtLeastOne() ? ".\n>> "+DyNet.getSingleton().getFoundDevices()+" devices found.":" but no device has been found.";
		DyNet.getSingleton().print(msg);
	}
}
