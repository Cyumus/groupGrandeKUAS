package com.cyumus.dynet;

import java.util.HashMap;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBee;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.exceptions.InterfaceInUseException;
import com.digi.xbee.api.exceptions.InvalidConfigurationException;
import com.digi.xbee.api.exceptions.InvalidInterfaceException;
import com.digi.xbee.api.exceptions.PermissionDeniedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

public class DyNet {
	private String PORT = "COM0";
	private final int BAUD_RATE = 9600;
	
	private final String PARAM_NODE_ID = "NI";
	private final String PARAM_PAN_ID = "ID";
	private final String PARAM_DEST_ADDRESS_H = "DH";
	private final String PARAM_DEST_ADDRESS_L = "DL";
	
	private final String PARAM_VALUE_NODE_ID = "DyNet";
	
	private byte[] PARAM_VALUE_PAN_ID = new byte[]{0x11,0x11};
	
	private final int PARAM_VALUE_DEST_ADDRESS_H = 0x00;
	private final int PARAM_VALUE_DEST_ADDRESS_L = 0xFFFF;
	
	private XBeeDevice device;
	private XBeeNetwork dyNetwork;
	
	private static DyNet dyNet;
	
	private HashMap<String, RemoteXBeeDevice> remoteXBeeDevices;
	
	
	/**
	 * Main constructor of DyNet class
	 * It creates the known Remote XBee Devices table and it configures 
	 * automatically the device associated with the program.
	 */
	private DyNet(){
		this.remoteXBeeDevices = new HashMap<String, RemoteXBeeDevice>();
		
		this.config();
	}
	
	
	public static void main(String [] args){
		DyNet dyNet = DyNet.getSingleton();
		dyNet.discover();
		// dyNet.sendData(sender , receiver, data);
	}
	
	/**
	 * @return The singleton of DyNet
	 */
	public static DyNet getSingleton(){
		if (dyNet == null) dyNet = new DyNet();
		return dyNet;
	}
	
	// TODO Test this function
	/**
	 * This function tries to establish a serial connection with an XBee device in all COM ports possibles until it arrives to COM10.
	 * If no XBee device was found, it returns the COM0 as default, being as an error to this program.  
	 * @param curr Current port number.
	 * @return The COM port where the XBee was found.
	 */
	private int findPortAvailable(int curr){
		if (curr >= 10 || curr < 1) return 0;
		try {
			IConnectionInterface connection = XBee.createConnectiontionInterface("COM"+curr, BAUD_RATE);
			connection.open(); connection.close();
			return curr;
		} catch (InvalidInterfaceException | InterfaceInUseException | PermissionDeniedException | InvalidConfigurationException er){
			return findPortAvailable(curr+1);
		}
	}
	// TODO Test this function
	/**
	 * This functions sends the data from the sender to the receiver
	 * @param sender The XBee Device that sends the data to the receiver
	 * @param receiver The Remote XBee Device that receives the data sent from the sender.
	 * @param data Hexadecimal data that is sent by the sender to the receiver
	 */
	public void sendData(XBeeDevice sender, RemoteXBeeDevice receiver, byte[] data){
		try {
			sender.sendData(receiver, data);
		} catch (XBeeException e) {
			System.out.println(">> "+e.getMessage());
		}
	}
	
	// TODO Test this function.
	/**
	 * This funciton sends the data to the 0xFFFF address, so it arrives to everybody.
	 * @param sender The XBee Device that sends the data to everybody
	 * @param data Hexadecimal data that is sent to everybody.
	 */
	public void broadcast(XBeeDevice sender, byte[] data){
		this.sendData(sender, new RemoteXBeeDevice(sender, new XBee64BitAddress("FFFF")), data);
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
	
	// TODO Test this function
	/**
	 * This function gets the connection interface of the client and closes it.
	 * Then, it removes it from the known Remote XBee Devices.
	 * @param client The identification of the Remote XBee Device.
	 */
	public void closeConnection(String client){
		this.remoteXBeeDevices.get(client).getConnectionInterface().close();
		this.remoteXBeeDevices.remove(client);
	}
	// TODO Test this function
	/**
	 * This function gets the connection interface of the XBee device and closes it.
	 * Then, it closes the serial connection with the device.
	 */
	public void closeConnection(){
		this.device.getConnectionInterface().close();
		this.device.close();
	}
	
	/**
	 * Transforms the hexadecimal data into a String
	 * @param data -> hexadecimal data that listener receives.
	 * @return -> a string of characters
	 */
	public String byteToString(byte[] data){
		return HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data));
	}
	
	/**
	 * This function configures the XBee device.
	 * First, it finds in which port it is connected.
	 * Then, it opens the serial connection with the device.
	 * When the connection is opened, it starts to set the main parameters.
	 * At last, it checks if the configuration was set correctly.
	 */
	private void config(){
		try{
			this.PORT = "COM"+this.findPortAvailable(1);
			if (this.PORT.equals("COM0")) {System.out.println(">> No port available found."); System.exit(1);}
			System.out.println(">> Port available found: "+this.PORT);
			
			this.device = new XBeeDevice(this.PORT, this.BAUD_RATE);
			
			this.device.open();
			
			this.device.setParameter(this.PARAM_NODE_ID, this.PARAM_VALUE_NODE_ID.getBytes());
			this.device.setParameter(this.PARAM_PAN_ID, this.PARAM_VALUE_PAN_ID);
			this.device.setParameter(this.PARAM_DEST_ADDRESS_H, ByteUtils.intToByteArray(this.PARAM_VALUE_DEST_ADDRESS_H));
			this.device.setParameter(this.PARAM_DEST_ADDRESS_L, ByteUtils.intToByteArray(this.PARAM_VALUE_DEST_ADDRESS_L));
			
			this.checkConfig();
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			device.close();
			System.exit(1);
		}
	}
	
	/**
	 * This function checks if the configuration was set correctly.
	 * @throws Exception
	 */
	private void checkConfig() throws Exception{
		byte [] ni = this.device.getParameter(this.PARAM_NODE_ID),
				id = this.device.getParameter(this.PARAM_PAN_ID),
				dh = this.device.getParameter(this.PARAM_DEST_ADDRESS_H),
				dl = this.device.getParameter(this.PARAM_DEST_ADDRESS_L);
		if (ni.equals(this.PARAM_NODE_ID)){throw new Exception("NI parameter was not set correctly.");}
		if (ByteUtils.byteArrayToLong(id) != ByteUtils.byteArrayToLong(this.PARAM_VALUE_PAN_ID)){throw new Exception("ID parameter was not set correctly");}
		if (ByteUtils.byteArrayToInt(dh) != this.PARAM_VALUE_DEST_ADDRESS_H){throw new Exception("DH parameter was not set correctly.");}
		if (ByteUtils.byteArrayToInt(dl) != this.PARAM_VALUE_DEST_ADDRESS_L){throw new Exception("DL parameter was not set correctly.");}
		System.out.println(">> All parameters were set correctly!");
	}
	/**
	 * This function scans the network and adds new devices discovered to the known Remote XBee devices.
	 */
	public void discover(){
		try{
			this.dyNetwork = device.getNetwork();
			this.dyNetwork.setDiscoveryTimeout(15000);
			this.dyNetwork.addDiscoveryListener(new DyNetListener());
			this.dyNetwork.startDiscoveryProcess();
			System.out.println(">> Discovering remote XBee devices...");
		}
		catch (XBeeException e){
			e.printStackTrace();
			device.close();
			System.exit(1);
		}
	}
	
	/**
	 * Adds the device discovered to the known Remote XBee devices.
	 * @param device
	 */
	public void deviceFound(RemoteXBeeDevice device){
		this.remoteXBeeDevices.put(device.getNodeID(), device);
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
		return this.device;
	}
	/**
	 * @return The Network where the XBee device is connected to.
	 */
	public XBeeNetwork getDyNet(){
		return this.dyNetwork;
	};
	/**
	 * @param id The identification of the remote XBee device.
	 * @return The known Remote XBee Device, if found.
	 */
	public RemoteXBeeDevice getRemoteDevice(String id){
		return this.remoteXBeeDevices.get(id);
	}
}
