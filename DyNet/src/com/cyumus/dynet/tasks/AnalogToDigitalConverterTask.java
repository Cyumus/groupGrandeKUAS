package com.cyumus.dynet.tasks;

import java.util.TimerTask;

import com.cyumus.dynet.DyNet;
import com.cyumus.util.Printer;
import com.cyumus.util.TypeOfMessage;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;

public class AnalogToDigitalConverterTask extends TimerTask{
	private boolean LOCAL;
	private RemoteXBeeDevice remoteDevice;
	private IOLine line;
	public AnalogToDigitalConverterTask(IOLine line){
		this.LOCAL = true;
		this.line = line;
	}
	public AnalogToDigitalConverterTask(RemoteXBeeDevice device, IOLine line){
		this.LOCAL = false;
		this.remoteDevice = device;
		this.line = line;
	}
	@Override
	public void run() {
		try{
			DyNet dyNet = DyNet.getSingleton();
			int value = this.LOCAL? 
					dyNet.getDevice().getADCValue(line) :
						this.remoteDevice.getADCValue(line);
			Printer.print(Integer.toString(value), TypeOfMessage.INPUT);
		}
		catch(XBeeException e){
			Printer.error(e.getMessage());
		}
	}

}
