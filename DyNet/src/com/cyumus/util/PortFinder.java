package com.cyumus.util;

import com.cyumus.dynet.DyNet;
import com.cyumus.dynet.exceptions.NoPortFoundException;
import com.digi.xbee.api.XBee;
import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.exceptions.InterfaceInUseException;
import com.digi.xbee.api.exceptions.InvalidConfigurationException;
import com.digi.xbee.api.exceptions.InvalidInterfaceException;
import com.digi.xbee.api.exceptions.PermissionDeniedException;

public abstract class PortFinder {
	/**
	 * This function tries to establish a serial connection with an XBee device in all COM ports possibles until it arrives to COM10.
	 * If no XBee device was found, it returns the COM0 as default, being as an error to this program.  
	 * @param curr Current port number.
	 * @return The COM port where the XBee was found.
	 */
	public static int findPortAvailable(int curr){
		if (curr >= 10 || curr < 1) throw new NoPortFoundException("No port available found.");
		try {
			IConnectionInterface connection = XBee.createConnectiontionInterface("COM"+curr, (Integer) DyNet.getSingleton().getConfig().get("BAUD_RATE"));
			connection.open(); connection.close();
			return curr;
		} catch (InvalidInterfaceException | InterfaceInUseException | PermissionDeniedException | InvalidConfigurationException er){
			return findPortAvailable(curr+1);
		}
	}
}
