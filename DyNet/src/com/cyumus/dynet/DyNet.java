package com.cyumus.dynet;

import java.util.HashMap;
import java.util.Timer;

import com.cyumus.dynet.config.DyNetConfiguration;
import com.cyumus.dynet.discovery.DyNetDiscover;
import com.cyumus.dynet.discovery.DyNetDiscovering;
import com.cyumus.dynet.system.plugins.PluginSystem;
import com.cyumus.dynet.tasks.AnalogToDigitalConverterTask;
import com.cyumus.dynet.tasks.DyNetAutodiscover;
import com.cyumus.dynet.tasks.TaskController;
import com.cyumus.util.Printer;
import com.cyumus.util.TypeOfMessage;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOMode;
import com.digi.xbee.api.models.XBee64BitAddress;

public class DyNet {		
	private DyNetConfiguration config;
	
	private DyNetDiscovering discover;
	
	private static DyNet dyNet;
	
	private HashMap<String, RemoteXBeeDevice> remoteXBeeDevices;
	
	private TaskController taskcontroller;
	
	private PluginSystem plugins;
	
	
	/**
	 * Main constructor of DyNet class
	 * It creates the known Remote XBee Devices table and it configures 
	 * automatically the device associated with the program.
	 */
	private DyNet(){
		this.remoteXBeeDevices = new HashMap<String, RemoteXBeeDevice>();
		this.plugins = new PluginSystem();/*
		// Creates the configuration of the DyNet.
		this.config = new DyNetConfiguration();
		
		this.discover = new DyNetDiscover();*/
		
		
	}
	
	public static void main(String [] args){
		DyNet dyNet = DyNet.getSingleton();
		/*byte[] data = {0x1, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0x87, 0x65, 0x43, 0x21};
		try{
			dyNet.discover();
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			dyNet.closeConnection();
			System.exit(1);
		}
		
		System.out.println(">> Trying to send data...");
		dyNet.sendData(dyNet.getDevice(), dyNet.getRemoteDevice("0013A20041040854 -  Receiver"), data);
		System.out.println(">> Trying to broadcast...");
		try {
			dyNet.broadcast(data);
		} catch (XBeeException e) {
			e.printStackTrace();
		}
		
		System.out.println(">> Trying to send data to server...");
		dyNet.sendToServer(dyNet.getDevice(), data);
		System.out.println(">> Trying to close remote connection access...");
		dyNet.closeConnection("0013A20041040854 -  Receiver");*/
		/*System.out.println(">> Trying to close serial connection...");
		dyNet.closeConnection();*/
		
	}
	
	/**
	 * @return The singleton of DyNet
	 */
	public static DyNet getSingleton(){
		if (dyNet == null) dyNet = new DyNet();
		return dyNet;
	}

	/**
	 * This function starts the discovering process.
	 * @throws TimeoutException
	 * @throws XBeeException
	 * @throws InterruptedException
	 */
	public void discover() throws TimeoutException, XBeeException, InterruptedException{
		this.discover.discover((Integer) DyNet.getSingleton().getConfig().get("DEFAULT_DISCOVERY_TIMEOUT"));
	}
	
	/**
	 * This function sends the data from the sender to the receiver
	 * @param sender The XBee Device that sends the data to the receiver
	 * @param receiver The Remote XBee Device that receives the data sent from the sender.
	 * @param data Hexadecimal data that is sent by the sender to the receiver
	 */
	public void sendData(XBeeDevice sender, RemoteXBeeDevice receiver, byte[] data){
		try {
			sender.sendData(receiver, data);
		} catch (XBeeException e) {
			Printer.error(e.getMessage());
		}
	}
	
	/**
	 * This function sends the data in asynchronous mode from the sender to the receiver
	 * @param sender The XBee Device that sends the data to the receiver
	 * @param receiver The Remote XBee Device that receives the data sent from the sender
	 * @param data Hexadecimal data that is sent by the sender to the receiver
	 */
	public void sendDataAsync(XBeeDevice sender, RemoteXBeeDevice receiver, byte[] data){
		try{
			sender.sendDataAsync(receiver, data);
		} catch(XBeeException e){
			Printer.error(e.getMessage());
		}
	}
	
	/**
	 * This function sends the data to the 0xFFFF address, so it arrives to everybody.
	 * @param sender The XBee Device that sends the data to everybody
	 * @param data Hexadecimal data that is sent to everybody.
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	public void broadcast(byte[] data) throws TimeoutException, XBeeException{
		this.getDevice().sendBroadcastData(data);
	}
	
	// TODO Test this function
	/**
	 * This function sends the data to the 0x0000 address, so it arrives to the coordinator.
	 * @param sender The XBee Device that sends the data to the server.
	 * @param data Hexadecimal data that is sent to the server
	 */
	public void sendToServer(XBeeDevice sender, byte[] data){
		this.sendData(sender, new RemoteXBeeDevice(sender, new XBee64BitAddress("0000")), data);
	}
	
	
	/**
	 * This function gets the connection interface of the client and closes it.
	 * Then, it removes it from the known Remote XBee Devices.
	 * @param client The identification of the Remote XBee Device.
	 */
	public void closeConnection(String client){
		this.remoteXBeeDevices.get(client).getConnectionInterface().close();
		this.remoteXBeeDevices.remove(client);
	}
	
	/**
	 * This function gets the connection interface of the XBee device and closes it.
	 * Then, it closes the serial connection with the device.
	 */
	public void closeConnection(){
		this.getDevice().getConnectionInterface().close();
		this.getDevice().close();
	}
	
	/**
	 * @return if the device has found at least one XBee Device.
	 */
	public boolean foundAtLeastOne(){
		return !this.remoteXBeeDevices.isEmpty();
	}
	
	/**
	 * @return How many XBee Devices has found.
	 */
	public int getFoundDevices(){
		return this.remoteXBeeDevices.size();
	}
	
	/**
	 * @return The XBee device that this program is connected with.
	 */
	public XBeeDevice getDevice(){
		return (XBeeDevice) this.config.get("DEVICE");
	}
	/**
	 * @return The Network where the XBee device is connected to.
	 */
	public XBeeNetwork getDyNet(){
		return (XBeeNetwork) this.config.get("NETWORK");
	};
	/**
	 * @param id The identification of the remote XBee device.
	 * @return The known Remote XBee Device, if found.
	 */
	public RemoteXBeeDevice getRemoteDevice(String id){
		return this.remoteXBeeDevices.get(id);
	}
	
	/**
	 * This function adds a new device to the known devices.
	 * @param device
	 */
	public void addRemoteDevice(RemoteXBeeDevice device){
		this.remoteXBeeDevices.put(device.toString(), device);
	}
	
	/**
	 * Gets the configuration of DyNet
	 * @return
	 */
	public DyNetConfiguration getConfig(){
		return this.config;
	}
	
	/**
	 * Gets the Discovering methodology of DyNet
	 * @return
	 */
	public DyNetDiscovering getDiscovering(){
		return this.discover;
	}
	
	/**
	 * Gets the Task Controller
	 * @return
	 */
	public TaskController getTaskController(){
		return this.taskcontroller;
	}
	
	/**
	 * This function resets all DyNet
	 */
	public void reset(){
		try {
			((XBeeDevice)this.config.get("DEVICE")).reset();
		} catch (XBeeException e) {
			Printer.error(e.getMessage());
		} finally{
			dyNet = new DyNet();
			Printer.print("DyNet has been reseted.", TypeOfMessage.IMPORTANT);
		}
	}
}
