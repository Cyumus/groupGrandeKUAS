package com.cyumus.dynet.discovery;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;

public interface DyNetDiscovering {
	public void discover(int timeout) throws TimeoutException, XBeeException, InterruptedException;
	public void discoveryFinished(String error);
	public boolean isDiscovering();
	public void deviceFound(RemoteXBeeDevice device);
}
