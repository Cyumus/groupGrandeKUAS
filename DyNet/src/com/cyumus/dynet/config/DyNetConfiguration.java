package com.cyumus.dynet.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.cyumus.dynet.listeners.DyNetDataListener;
import com.cyumus.dynet.listeners.DyNetListener;
import com.cyumus.dynet.listeners.DyNetPacketListener;
import com.cyumus.dynet.listeners.DyNetStatusListener;
import com.cyumus.dynet.system.plugins.PluginController;
import com.cyumus.util.PortFinder;
import com.cyumus.util.Printer;
import com.cyumus.util.TypeOfMessage;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.utils.HexUtils;

public class DyNetConfiguration {
	private Properties config;

	// TODO Test this function with the file reading instead of the setParameter mess.
	/**
	 * This class configures the XBee device.
	 * First, it finds in which port it is connected.
	 * Then, it opens the serial connection with the device.
	 * When the connection is opened, it starts to set the main parameters.
	 * After that, it creates the listeners as the DyNetListener, to scan the network; and the DyNetDataListener, to receive data.
	 * As an extra, it sets the plugin controller as wanted.
	 * At last, it checks if the configuration was set correctly.
	 */
	public DyNetConfiguration(){
		this.config = new Properties();
		try{
			// Sets the default values
			this.config.put("PORT", "COM0");
			this.config.put("BAUD_RATE", 9600);
			this.config.put("DEFAULT_DISCOVERY_TIMEOUT", 15000);
			
			// Finds the port where the XBee device is plugged in.
			try{
				PortFinder.setPort((Integer)this.config.get("BAUD_RATE"));
				this.config.put("PORT", "COM"+PortFinder.findPortAvailable(1));
				Printer.print("Port available found: "+((String) this.config.get("PORT")), TypeOfMessage.CONFIG);
			}
			catch(Exception e){
				Printer.error("No port available found.");
				System.exit(1);
			}
			
			// Connects to the XBee Device using the port found before.
			XBeeDevice device = new XBeeDevice((String) this.config.get("PORT"), (Integer) this.config.get("BAUD_RATE"));
			
			// Establishes a serial connection with the device.
			((XBeeDevice)this.config.get("DEVICE")).open();
			
			// Reads the configuration file.
			this.load();
			// Sets all the parameters to the device.
			this.set();
			
			// Creating the network listener
			XBeeNetwork dyNetwork = device.getNetwork();
			dyNetwork.setDiscoveryTimeout((Integer) this.config.get("DEFAULT_DISCOVERY_TIMEOUT"));
			dyNetwork.addDiscoveryListener(new DyNetListener());
			
			// Creating the data listener
			device.addDataListener(new DyNetDataListener());
			
			// Creating the modem status listener.
			device.addModemStatusListener(new DyNetStatusListener());
			
			// Creating the packet listener.
			device.addPacketListener(new DyNetPacketListener());
			
			this.config.put("DEVICE", device);
			this.config.put("NETWORK", dyNetwork);
			
			// TODO ControllerFactory
			this.config.put("PLUGIN_CONTROLLER", new PluginController());
			
			Printer.print("All parameters were set correctly", TypeOfMessage.CONFIG);
		}
		catch(Exception e){
			Printer.error(e.getMessage());
			((XBeeDevice)this.config.get("DEVICE")).close();
			System.exit(1);
		}
	}
	
	/**
	 * This function reads all configuration file settings.
	 */
	public void load(){
		try{
			config.load(new FileInputStream("config.properties"));
		}
		catch(FileNotFoundException e){
			Printer.error("Configuration file not found or corrupted.");
		}
		catch(IOException er){
			Printer.error("Something with the configuration file was wrong. Maybe it's corrupted.");
		}
		
	}
	
	// TODO Test this function
	/**
	 * This function sets all the parameters to the device using the properties object.
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	public void set(){
		this.config.forEach(
			(k,v) -> {
				try{
					((XBeeDevice) this.config.get("DEVICE")).setParameter((String) k, HexUtils.hexStringToByteArray((String) v));
				}
				catch(XBeeException e){
					Printer.error("Something went wrong when setting the device properties.");
				}
			}
		);
	}
	
	/**
	 * This function saves the Properties object to the config file. 
	 */
	public void save(){
		try{
			this.config.store(new FileOutputStream("config.properties"), "Saved new configuration");
		}
		catch(FileNotFoundException e){
			Printer.error("Configuration file not found or corrupted.");
		}
		catch(IOException er){
			Printer.error("Something with the configuration file was wrong. Maybe it's corrupted.");
		}
	}
	
	/**
	 * This function returns the given configuration parameter
	 * @param key
	 * @return
	 */
	public Object get(String key){
		return this.config.get(key);
	}
}
