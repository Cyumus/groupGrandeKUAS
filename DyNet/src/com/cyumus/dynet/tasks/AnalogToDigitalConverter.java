package com.cyumus.dynet.tasks;

import java.util.TimerTask;

import com.cyumus.dynet.DyNet;
import com.cyumus.util.Printer;
import com.cyumus.util.TypeOfMessage;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;

public class AnalogToDigitalConverter extends TimerTask{
	public static final IOLine IOLINE_IN = IOLine.DIO1_AD1;
	private boolean LOCAL;
	private RemoteXBeeDevice remoteDevice;
	public AnalogToDigitalConverter(){
		this.LOCAL = true;
	}
	public AnalogToDigitalConverter(RemoteXBeeDevice device){
		this.LOCAL = false;
		this.remoteDevice = device;
	}
	@Override
	public void run() {
		try{
			DyNet dyNet = DyNet.getSingleton();
			int value = this.LOCAL? 
					dyNet.getDevice().getADCValue(IOLINE_IN) :
						this.remoteDevice.getADCValue(IOLINE_IN);
			Printer.print(Integer.toString(value), TypeOfMessage.INPUT);
		}
		catch(XBeeException e){
			Printer.error(e.getMessage());
		}
	}

}
