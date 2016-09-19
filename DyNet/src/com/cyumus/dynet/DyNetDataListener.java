package com.cyumus.dynet;

import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.utils.HexUtils;

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
		String flag = xbeeMessage.isBroadcast() ? "[B]":"[M]";
		System.out.format(">> %s[%s]: %s | %s%n",  flag, xbeeMessage.getDevice().get64BitAddress(),
		HexUtils.prettyHexString(HexUtils.byteArrayToHexString(xbeeMessage.getData())),
		xbeeMessage.getData());
	}
}
