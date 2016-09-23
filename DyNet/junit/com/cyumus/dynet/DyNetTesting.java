package com.cyumus.dynet;
import com.digi.xbee.api.RemoteXBeeDevice;

import junit.framework.TestCase;

public class DyNetTesting extends TestCase {
	private static DyNet dyNet;
	
	public void setUp(){
		 dyNet = DyNet.getSingleton();
	}

	public void testCloseConnectionString() {
		RemoteXBeeDevice kicked = dyNet.getRemoteDevice("Name");
		dyNet.closeConnection("Name");
		assertEquals(false, kicked.getConnectionInterface().isOpen());
		assertEquals(null, "Name");
	}

	public void testCloseConnection() {
		dyNet.closeConnection();
		assertEquals(false, dyNet.getDevice().isOpen());
	}

	public void testDiscover() {
		assertEquals(true, dyNet.foundAtLeastOne());
	}

}
