package com.cyumus.dynet.system.plugins;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;
import java.util.HashMap;

import com.cyumus.dynet.DyNet;
import com.cyumus.dynet.system.security.PluginPolicy;
import com.cyumus.util.Formatter;
import com.cyumus.util.Printer;
import com.cyumus.util.TypeOfMessage;

public class PluginSystem {
	private HashMap<String, Plugin> plugins;
	public PluginSystem(){
		Policy.setPolicy(new PluginPolicy());
		System.setSecurityManager(new SecurityManager());
		
		this.plugins = new HashMap<String, Plugin>();
		
		this.loadPlugins();
	}
	
	private void loadPlugins(){
		File pluginFolder = new File("plugins/");
		File [] pluginFiles = pluginFolder.listFiles();
		Printer.print("Loading all plugins from the plugin directory.", TypeOfMessage.CONFIG);
		for (File pluginFile:pluginFiles){
			try{
				URL[] url={pluginFile.toURI().toURL()};
				ClassLoader loader = URLClassLoader.newInstance(url);
				Class<?> pluginClass = loader.loadClass("plugin.Main");
				Plugin plugin = (Plugin) pluginClass.newInstance();
				String fancyName = Formatter.getFancyName(pluginFile.getName(), 4);
				plugin.setController((PluginController) DyNet.getSingleton().getConfig().get("PLUGIN_CONTROLLER"));
				plugin.load();
				Printer.print(fancyName+" has been loaded.", TypeOfMessage.CONFIG);
				plugins.put(fancyName, plugin);
			}
			catch(Exception e){
				Printer.error(e.getMessage());
			}
		}
		Printer.print("All plugins had been loaded successfully.", TypeOfMessage.CONFIG);
	}
}
