package example.Listeners;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import dk.earthgame.TAT.SignUpdater.SignUpdater;

import example.MyPlugin;

public class MyPluginPluginListener extends ServerListener {
    // Change "MyPlugin" to the name of your plugin name.
    private MyPlugin plugin;
    
    public MyPluginPluginListener(MyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
    	//SignUpdater
		if (plugin.SignUpdater == null && event.getPlugin().getDescription().getName().equalsIgnoreCase("SignUpdater")) {
			Plugin test = plugin.getServer().getPluginManager().getPlugin("SignUpdater");
			if (test != null) {
				plugin.SignUpdater = (SignUpdater)test;
				System.out.println("Established connection with SignUpdater!");
			}
		}
    }
    
    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        // Check to see if the plugin thats being disabled is the one we are using
        if (plugin.SignUpdater != null && event.getPlugin().getDescription().getName().equalsIgnoreCase("SignUpdater")) {
        	plugin.SignUpdater = null;
			System.out.println("Established connection with SignUpdater!");
        }
    }
}
