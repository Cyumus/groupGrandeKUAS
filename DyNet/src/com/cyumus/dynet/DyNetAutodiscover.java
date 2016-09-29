package com.cyumus.dynet;

import java.util.TimerTask;

import com.digi.xbee.api.exceptions.XBeeException;

public class DyNetAutodiscover extends TimerTask{

	@Override
	public void run() {
		try {
			DyNet.getSingleton().discover();
		} catch (XBeeException | InterruptedException e) {
			DyNet.getSingleton().error("Error while doing the new devices autodiscovery process.");
		}		
	}

}
