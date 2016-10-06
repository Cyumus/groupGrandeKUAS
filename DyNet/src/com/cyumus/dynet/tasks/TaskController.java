package com.cyumus.dynet.tasks;

import java.util.Timer;

import com.cyumus.dynet.DyNet;
import com.cyumus.util.Printer;
import com.cyumus.util.TypeOfMessage;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOMode;

public class TaskController {
	private Timer timer;
	public TaskController(){
		// Creates the timer for the scheduled tasks.
		this.timer = new Timer();
	}
	// TODO Test this function
	/**
	 * This function starts an autodiscover process, in order to update the usertable, adding new devices and removing inactive or lost ones.
	 * @param delay the time between autodiscovering processes
	 */
	public void startAutoDiscovery(long delay){
		Printer.print("Starting autodiscovering process...", TypeOfMessage.CONFIG);
		this.timer.schedule(new DyNetAutodiscover(), delay);
	}
	
	// TODO Test this function
	/**
	 * This function starts an Analog-To-Digital scheduled task, reading all input data from local.
	 * @throws TimeoutException
	 * @throws XBeeException
	 */
	public void startLocalADCTask() throws TimeoutException, XBeeException{
		DyNet.getSingleton().getDevice().setIOConfiguration(IOLine.DIO1_AD1, IOMode.ADC);
		this.timer.schedule(new AnalogToDigitalConverterTask(IOLine.DIO1_AD1), 0);
	}
	
	// TODO Test this function
	/**
	 * This function starts a Remote Analog-To-Digital scheduled task, reading all input data from the given remote XBee device.
	 * @param remoteDevice from where the data comes.
	 * @throws TimeoutException
	 * @throws XBeeException
	 */
	public void startRemoteADCTask(RemoteXBeeDevice remoteDevice) throws TimeoutException, XBeeException{
		remoteDevice.setIOConfiguration(IOLine.DIO1_AD1, IOMode.ADC);
		this.timer.schedule(new AnalogToDigitalConverterTask(remoteDevice, IOLine.DIO1_AD1), 0);
	}
	
	/**
	 * This function starts a Local Digital Input-Output scheduled task, reading all input digital data from local.
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	public void startLocalDIOTask() throws TimeoutException, XBeeException{
		DyNet.getSingleton().getDevice().setIOConfiguration(IOLine.DIO1_AD1, IOMode.DIGITAL_IN);
		this.timer.schedule(new DigitalInputOutputTask(IOLine.DIO1_AD1), 0);
	}
	
	/**
	 * This function starts a remote Digital Input-Output scheduled task, reading all input digital data from remote XBee device.
	 * @param remoteDevice
	 * @throws TimeoutException
	 * @throws XBeeException
	 */
	public void startRemoteDIOTask(RemoteXBeeDevice remoteDevice) throws TimeoutException, XBeeException{
		remoteDevice.setIOConfiguration(IOLine.DIO1_AD1, IOMode.DIGITAL_IN);
		this.timer.schedule(new DigitalInputOutputTask(remoteDevice, IOLine.DIO1_AD1), 0);
	}
	
	// TODO Test this function
	/**
	 * This function stops all scheduled tasks in the timer.
	 */
	public void stop(){
		this.timer.cancel();
	}
}
