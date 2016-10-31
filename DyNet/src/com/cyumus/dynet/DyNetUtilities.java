package com.cyumus.dynet;

import com.cyumus.dynet.config.DyNetConfiguration;
import com.cyumus.dynet.discovery.DyNetDiscovering;
import com.cyumus.dynet.tasks.TaskController;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;

public interface DyNetUtilities {
	public void discover() throws TimeoutException, XBeeException, InterruptedException ;
	public void sendData(XBeeDevice sender, RemoteXBeeDevice receiver, byte[] data);
	public void sendDataAsync(XBeeDevice sender, RemoteXBeeDevice receiver, byte[] data);
	public void broadcast(byte[] data) throws TimeoutException, XBeeException;
	public void sendToServer(XBeeDevice sender, byte[] data);
	public void closeConnection(String client);
	public void closeConnection();
	public boolean foundAtLeastOne();
	public int getFoundDevices();
	public XBeeDevice getDevice();
	public XBeeNetwork getDyNet();
	public RemoteXBeeDevice getRemoteDevice(String id);
	public void addRemoteDevice(RemoteXBeeDevice device);
	public DyNetConfiguration getConfig();
	public DyNetDiscovering getDiscovering();
	public TaskController getTaskController();
	public void reset();
}
