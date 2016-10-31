package com.cyumus.dynet.system.plugins;

import com.cyumus.dynet.DyNet;
import com.cyumus.dynet.DyNetUtilities;
import com.cyumus.dynet.config.DyNetConfiguration;
import com.cyumus.dynet.discovery.DyNetDiscovering;
import com.cyumus.dynet.tasks.TaskController;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;

/**
 * This class is a very simple controller to make an extra layer of flexibility to the code.
 */
public class PluginController implements DyNetUtilities{
	@Override
	public void discover() throws TimeoutException, XBeeException, InterruptedException {
		DyNet.getSingleton().discover();
	}

	@Override
	public void sendData(XBeeDevice sender, RemoteXBeeDevice receiver, byte[] data) {
		DyNet.getSingleton().sendData(sender, receiver, data);		
	}

	@Override
	public void sendDataAsync(XBeeDevice sender, RemoteXBeeDevice receiver, byte[] data) {
		DyNet.getSingleton().sendDataAsync(sender, receiver, data);		
	}

	@Override
	public void broadcast(byte[] data) throws TimeoutException, XBeeException {
		DyNet.getSingleton().broadcast(data);
	}

	@Override
	public void sendToServer(XBeeDevice sender, byte[] data) {
		DyNet.getSingleton().sendToServer(sender, data);	
	}

	@Override
	public void closeConnection(String client) {
		DyNet.getSingleton().closeConnection(client);
	}

	@Override
	public void closeConnection() {
		DyNet.getSingleton().closeConnection();		
	}

	@Override
	public boolean foundAtLeastOne() {
		return DyNet.getSingleton().foundAtLeastOne();
	}

	@Override
	public int getFoundDevices() {
		return DyNet.getSingleton().getFoundDevices();
	}

	@Override
	public XBeeDevice getDevice() {
		return DyNet.getSingleton().getDevice();
	}

	@Override
	public XBeeNetwork getDyNet() {
		return DyNet.getSingleton().getDyNet();
	}

	@Override
	public RemoteXBeeDevice getRemoteDevice(String id) {
		return DyNet.getSingleton().getRemoteDevice(id);
	}

	@Override
	public void addRemoteDevice(RemoteXBeeDevice device) {
		DyNet.getSingleton().addRemoteDevice(device);
	}

	@Override
	public DyNetConfiguration getConfig() {
		return DyNet.getSingleton().getConfig();
	}

	@Override
	public DyNetDiscovering getDiscovering() {
		return DyNet.getSingleton().getDiscovering();
	}

	@Override
	public TaskController getTaskController() {
		return DyNet.getSingleton().getTaskController();
	}

	@Override
	public void reset() {
		DyNet.getSingleton().reset();
	}

}
