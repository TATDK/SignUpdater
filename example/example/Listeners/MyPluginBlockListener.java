package example.Listeners;

import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;

import dk.earthgame.TAT.SignUpdater.UpdaterPriority;


public class MyPluginBlockListener extends BlockListener {
	// Change "MyPlugin" to the name of your plugin name.
    private MyPlugin plugin;
    
    public MyPluginPluginListener(MyPlugin plugin) {
        this.plugin = plugin;
    }
    
	@Override
	public void onSignChange(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("[MP]")) {
			String[] lines = {"[MP]", "Example of", "SignUpdater", ""};
            plugin.signupdater.AddSignUpdate(UpdaterPriority.NORMAL, (Sign)event.getBlock().getState(), lines);
		}
	}
}
