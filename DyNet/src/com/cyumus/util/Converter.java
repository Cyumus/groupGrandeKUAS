package com.cyumus.util;

import com.digi.xbee.api.utils.HexUtils;

public abstract class Converter {
	/**
	 * Transforms the hexadecimal data into a String
	 * @param data -> hexadecimal data that listener receives.
	 * @return -> a string of characters
	 */
	public static String byteToString(byte [] data){
		return HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data));
	}
}
