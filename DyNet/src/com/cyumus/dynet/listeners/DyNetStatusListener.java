package com.cyumus.dynet.listeners;

import com.cyumus.util.Printer;
import com.cyumus.util.TypeOfMessage;
import com.digi.xbee.api.listeners.IModemStatusReceiveListener;
import com.digi.xbee.api.models.ModemStatusEvent;

public class DyNetStatusListener implements IModemStatusReceiveListener{

	@Override
	public void modemStatusEventReceived(ModemStatusEvent modemStatusEvent) {
		Printer.print(modemStatusEvent.toString(), TypeOfMessage.SERVER);
	}
}
