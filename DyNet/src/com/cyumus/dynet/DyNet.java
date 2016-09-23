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
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

public class DyNet {
	private String PORT = "COM0";
	private final int BAUD_RATE = 9600;
	
	public static int DEFAULT_DISCOVERY_TIMEOUT = 15000;
	
	private final String PARAM_NODE_ID = "NI";
	private final String PARAM_PAN_ID = "ID";
	private final String PARAM_DEST_ADDRESS_H = "DH";
	private final String PARAM_DEST_ADDRESS_L = "DL";
	
	private final String PARAM_VALUE_NODE_ID = "DyNet";
	
	private byte[] PARAM_VALUE_PAN_ID = new byte[]{0x1};
	
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
		byte[] data = {0x1, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0x87, 0x65, 0x43, 0x21};
		try{
			dyNet.discover();
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			dyNet.closeConnection();
			System.exit(1);
		}
		try {
			synchronized (dyNet){ 
				dyNet.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(">> Trying to send data...");
		dyNet.sendData(dyNet.getDevice(), dyNet.getRemoteDevice("0013A20041040854 -  Receiver"), data);
		System.out.println(">> Trying to broadcast...");
		try {
			dyNet.broadcast(data);
		} catch (XBeeException e) {
			e.printStackTrace();
		}
		/*System.out.println(">> Trying to close remote connection access...");
		dyNet.closeConnection("0013A20041040854 -  Receiver");*/
		System.out.println(">> Trying to close serial connection...");
		dyNet.closeConnection();
	}
	
	/**
	 * @return The singleton of DyNet
	 */
	public static DyNet getSingleton(){
		if (dyNet == null) dyNet = new DyNet();
		return dyNet;
	}
	
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
	
	/**
	 * This function sends the data to the 0xFFFF address, so it arrives to everybody.
	 * @param sender The XBee Device that sends the data to everybody
	 * @param data Hexadecimal data that is sent to everybody.
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	public void broadcast(byte[] data) throws TimeoutException, XBeeException{
		this.device.sendBroadcastData(data);
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
		this.device.getConnectionInterface().close();
		this.device.close();
	}
	// TODO Create 'util' class to store all these 'util' functions.
	/**
	 * Transforms the hexadecimal data into a String
	 * @param data -> hexadecimal data that listener receives.
	 * @return -> a string of characters
	 */
	private String byteToString(byte[] data){
		return HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data));
	}
	
	/**
	 * This function configures the XBee device.
	 * First, it finds in which port it is connected.
	 * Then, it opens the serial connection with the device.
	 * When the connection is opened, it starts to set the main parameters.
	 * After that, it creates the listeners as the DyNetListener, to scan the network; and the DyNetDataListener, to receive data.
	 * At last, it checks if the configuration was set correctly.
	 */
	private void config(){
		try{
			// Finds the port where the XBee device is plugged in.
			this.PORT = "COM"+this.findPortAvailable(1);
			if (this.PORT.equals("COM0")) {System.out.println(">> No port available found."); System.exit(1);}
			System.out.println(">> Port available found: "+this.PORT);
			
			// Connects to the XBee Device using the port found before.
			this.device = new XBeeDevice(this.PORT, this.BAUD_RATE);
			
			// Establishes a serial connection with the device.
			this.device.open();
			
			// Sets the main parameters to the XBee device.
			this.device.setParameter(this.PARAM_NODE_ID, this.PARAM_VALUE_NODE_ID.getBytes());
			this.device.setParameter(this.PARAM_PAN_ID, this.PARAM_VALUE_PAN_ID);
			this.device.setParameter(this.PARAM_DEST_ADDRESS_H, ByteUtils.intToByteArray(this.PARAM_VALUE_DEST_ADDRESS_H));
			this.device.setParameter(this.PARAM_DEST_ADDRESS_L, ByteUtils.intToByteArray(this.PARAM_VALUE_DEST_ADDRESS_L));
			
			// Creating the network listener
			this.dyNetwork = device.getNetwork();
			this.dyNetwork.setDiscoveryTimeout(DyNet.DEFAULT_DISCOVERY_TIMEOUT);
			this.dyNetwork.addDiscoveryListener(new DyNetListener());
			
			// Creating the data listener
			this.device.addDataListener(new DyNetDataListener());
			
			// Checking that the configuration has been set correctly.
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
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	public void discover() throws TimeoutException, XBeeException{
		this.discover(DyNet.DEFAULT_DISCOVERY_TIMEOUT);
	}
	
	public void discover(int timeout) throws TimeoutException, XBeeException{
		this.dyNetwork.setDiscoveryTimeout(timeout);
		this.dyNetwork.startDiscoveryProcess();
		System.out.println(">> Discovering remote XBee devices...");
	}
	
	public void discoveryFinished(String error){
		String msg = ">> Discovery process finished ";
		msg += error == null ? "successfully":"due to the following error: "+error;
		msg += DyNet.getSingleton().foundAtLeastOne() ? ".\n>> "+DyNet.getSingleton().getFoundDevices()+" devices found.":" but no device has been found.";
		System.out.println(msg);
		synchronized(dyNet){
			dyNet.notify();
		}
	}
	
	public boolean isDiscovering(){
		return this.dyNetwork.isDiscoveryRunning();
	}
	
	/**
	 * Adds the device discovered to the known Remote XBee devices.
	 * @param device
	 */
	public void deviceFound(RemoteXBeeDevice device){
		System.out.format(">> Device discovered: %s%n", device.toString());
		this.remoteXBeeDevices.put(device.toString(), device);
	}
	
	// TODO Create a 'Messaging' class to store all these communicating functions.
	/**
	 * Prints the message received by the data listener.
	 * @param msg a message containing the data
	 */
	public void print(XBeeMessage msg){
		System.out.println(this.format(msg));
	}
	
	/**
	 * This function formats the XBeeMessage into a pretty String.
	 * @param msg
	 * @return the pretty String.
	 */
	public String format(XBeeMessage msg){
		String flag = msg.isBroadcast() ? "[B]":"[M]";
		return String.format(">> %s[%s]: %s | %s%n",  flag, msg.getDevice().get64BitAddress(),
				this.byteToString(msg.getData()),
				msg.getDataString());
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
	/**
	 * This function returns the port where the XBee device of the coordinator is plugged in.
	 * @return The port of the Coordinator's XBee device, if any. 
	 */
	public String getPort(){
		return this.PORT;
	}
	
	/**
	 * This function prints an error message
	 * @param error The error message
	 */
	public void error(String error){
		System.out.println(">> There was an error discovering devices: "+error);
	}
}
