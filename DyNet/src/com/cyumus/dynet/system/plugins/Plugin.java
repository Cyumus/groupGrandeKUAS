package com.cyumus.dynet.system.plugins;

import javax.swing.JComponent;

public interface Plugin {
	public void load();
	public void run();
	public void unload();
	public JComponent draw();
}
