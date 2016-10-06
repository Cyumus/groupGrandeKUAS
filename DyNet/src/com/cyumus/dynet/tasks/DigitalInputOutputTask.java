package com.cyumus.dynet.tasks;

import java.util.TimerTask;

import com.cyumus.dynet.DyNet;
import com.cyumus.util.Printer;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOValue;

public class DigitalInputOutputTask extends TimerTask{
	private boolean LOCAL;
	private RemoteXBeeDevice remoteDevice;
	private IOValue value;
	private IOLine line;
	public DigitalInputOutputTask(IOLine line){
		this.LOCAL = true;
		this.line = line;
	}
	public DigitalInputOutputTask(RemoteXBeeDevice remoteDevice, IOLine line){
		this.LOCAL = false;
		this.remoteDevice = remoteDevice;
		this.line = line;
	}
	@Override
	public void run() {
		try{
			value = this.LOCAL?
					DyNet.getSingleton().getDevice().getDIOValue(line):
						remoteDevice.getDIOValue(line);
					
		}
		catch(XBeeException e){
			Printer.error(e.getMessage());
		}
	}
	public IOValue getValue(){
		return this.value;
	}
	
	
}
