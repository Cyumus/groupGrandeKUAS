package com.cyumus.util;

import com.digi.xbee.api.models.XBeeMessage;

public abstract class Formatter {
	/**
	 * This function formats the XBeeMessage into a pretty String.
	 * @param msg
	 * @return the pretty String.
	 */
	public static String format(XBeeMessage msg, TypeOfMessage type){
		return String.format(">> %s[%s]: %s | %s%n",  type, msg.getDevice().get64BitAddress(),
				Converter.byteToString(msg.getData()),
				msg.getDataString());
	}
	
	public static String format(String msg, TypeOfMessage type){
		return String.format(">> [%s]: %s", type, msg);
	}
}
