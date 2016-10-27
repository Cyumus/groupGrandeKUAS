package com.cyumus.dynet.system.plugins;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;
import java.util.HashMap;

import com.cyumus.dynet.system.security.PluginPolicy;
import com.cyumus.util.Printer;
import com.cyumus.util.TypeOfMessage;

public class PluginSystem {
	private HashMap<String, Plugin> plugins;
	public PluginSystem(){
		Policy.setPolicy(new PluginPolicy());
		System.setSecurityManager(new SecurityManager());
		
		this.plugins = new HashMap<String, Plugin>();
		
		try {this.loadPlugins();}
		catch(MalformedURLException | InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e){Printer.error("Plugin format error: "+e.getMessage());}
	}
	
	private void loadPlugins() throws MalformedURLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		File pluginFolder = new File("plugins/");
		File [] pluginFiles = pluginFolder.listFiles();
		Printer.print("Loading all plugins from the plugin directory", TypeOfMessage.CONFIG);
		for (File pluginFile:pluginFiles){
			URL [] url = {pluginFile.toURI().toURL()};
			ClassLoader loader = URLClassLoader.newInstance(url);
			Class<?> pluginClass = loader.loadClass("plugin.Main");
			Plugin plugin = (Plugin) pluginClass.newInstance();
			plugin.load();
			plugins.put(pluginFile.getName().substring(0, pluginFile.getName().length()-4), plugin);
		}
	}
}
