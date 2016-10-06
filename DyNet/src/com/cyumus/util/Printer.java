package com.cyumus.util;

import com.digi.xbee.api.models.XBeeMessage;

public abstract class Printer {
	/**
	 * Prints the message received by the data listener.
	 * @param msg a message containing the data
	 */
	public static void print(XBeeMessage msg, TypeOfMessage type){
		System.out.println(Formatter.format(msg, type));
	}
	public static void print(String msg, TypeOfMessage type){
		System.out.println(Formatter.format(msg,type));
	}
	/**
	 * This function prints an error message
	 * @param error The error message
	 */
	public static void error(String error){
		print(error, TypeOfMessage.ERROR);
	}
}
