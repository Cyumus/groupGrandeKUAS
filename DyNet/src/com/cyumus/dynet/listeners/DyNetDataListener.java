package com.cyumus.dynet.listeners;

import com.cyumus.util.Printer;
import com.cyumus.util.TypeOfMessage;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;

public class DyNetDataListener implements IDataReceiveListener{
	/**
	 * Called when data is received from a remote node of the network.
	 * 
	 * @param xbeeMessage An {@code XBeeMessage} object containing the data,
	 *                    the {@code RemoteXBeeDevice} that sent the data and 
	 *                    a flag indicating whether the data was sent via 
	 *                    broadcast or not.
	 */
	@Override
	public void dataReceived(XBeeMessage xbeeMessage) {
		TypeOfMessage type = xbeeMessage.isBroadcast()?
				TypeOfMessage.BROADCAST : TypeOfMessage.SIMPLE;
		Printer.print(xbeeMessage, type);
	}
}
