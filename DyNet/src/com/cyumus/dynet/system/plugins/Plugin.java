package com.cyumus.dynet.system.plugins;

import com.cyumus.dynet.DyNetUtilities;

public abstract class Plugin implements PluginUtilities{
	protected DyNetUtilities controller;
	public final void setController(PluginController controller){
		this.controller = controller;
	}
	public final PluginController getController(){
		return (PluginController) this.controller;
	}
}
