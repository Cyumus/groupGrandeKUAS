package com.cyumus.dynet.discovery;

import com.cyumus.dynet.DyNet;
import com.cyumus.util.Printer;
import com.cyumus.util.TypeOfMessage;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;

public class DyNetDiscover implements DyNetDiscovering{
	/**
	 * This function scans the network and adds new devices discovered to the known Remote XBee devices.
	 * It waits the DyNet main function while discovering.
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 * @throws InterruptedException 
	 */	
	public void discover(int timeout) throws TimeoutException, XBeeException, InterruptedException{
		DyNet.getSingleton().getDyNet().setDiscoveryTimeout(timeout);
		DyNet.getSingleton().getDyNet().startDiscoveryProcess();
		Printer.print("Discovering remote XBee devices...", TypeOfMessage.CONFIG);
		synchronized (DyNet.getSingleton()){ 
			DyNet.getSingleton().wait();
		}
	}
	
	/**
	 * This function is used by the DyNetListener just after the discovery process has been finished.
	 * When finished, the main functionality of DyNet is resumed.
	 * @param error
	 */
	public void discoveryFinished(String error){
		String msg = "Discovery process finished ";
		msg += error == null ? "successfully":"due to the following error: "+error;
		msg += DyNet.getSingleton().foundAtLeastOne() ? ".\n>> "+DyNet.getSingleton().getFoundDevices()+" devices found.":" but no device has been found.";
		Printer.print(msg, TypeOfMessage.CONFIG);
		synchronized(DyNet.getSingleton()){
			DyNet.getSingleton().notify();
		}
	}
	
	/**
	 * @return if the DyNet network is discovering
	 */
	public boolean isDiscovering(){
		return DyNet.getSingleton().getDyNet().isDiscoveryRunning();
	}
	
	/**
	 * Adds the device discovered to the known Remote XBee devices.
	 * @param device
	 */
	public void deviceFound(RemoteXBeeDevice device){
		Printer.print(String.format("Device discovered: %s%n", device.toString()), TypeOfMessage.CONFIG);
		DyNet.getSingleton().addRemoteDevice(device);
	}
}
