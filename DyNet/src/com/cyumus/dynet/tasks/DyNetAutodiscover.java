package com.cyumus.dynet.tasks;

import java.util.TimerTask;

import com.cyumus.dynet.DyNet;
import com.cyumus.util.Printer;
import com.digi.xbee.api.exceptions.XBeeException;

public class DyNetAutodiscover extends TimerTask{

	@Override
	public void run() {
		try {
			DyNet.getSingleton().discover();
		} catch (XBeeException | InterruptedException e) {
			Printer.error("Error while doing the new devices autodiscovery process.");
		}		
	}

}
