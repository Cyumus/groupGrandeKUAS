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
import com.digi.xbee.api.utils.ByteUtils;

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

	private DyNet(){
		this.remoteXBeeDevices = new HashMap<String, RemoteXBeeDevice>();
		
		this.config();
	}
	public static void main(String [] args){
		DyNet dyNet = DyNet.getSingleton();
		dyNet.discover();
		// TODO Send data to other devices
		// dyNet.sendData(sender , receiver, data);
	}
	
	public static DyNet getSingleton(){
		if (dyNet == null) dyNet = new DyNet();
		return dyNet;
	}
	
	// TODO Test this function
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
	public void sendData(XBeeDevice sender, RemoteXBeeDevice receiver, byte[] data){
		try {
			sender.sendData(receiver, data);
		} catch (XBeeException e) {
			System.out.println(">> "+e.getMessage());
		}
	}
	
	// TODO Test this function
	public void closeConnection(String client){
		this.remoteXBeeDevices.get(client).getConnectionInterface().close();
		this.remoteXBeeDevices.remove(client);
	}
	
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
	public void deviceFound(RemoteXBeeDevice device){
		this.remoteXBeeDevices.put(device.getNodeID(), device);
	}
	public boolean foundAtLeastOne(){
		return !this.remoteXBeeDevices.isEmpty();
	}
	public int getFoundDevices(){
		return this.remoteXBeeDevices.size();
	}
	
	public XBeeDevice getDevice(){
		return this.device;
	}
	public XBeeNetwork getDyNet(){
		return this.dyNetwork;
	};
	public RemoteXBeeDevice getRemoteDevice(String id){
		return this.remoteXBeeDevices.get(id);
	}
}
