package example;

//Listener (must be changed)
import example.Listeners.MyPluginPluginListener;

//Imports for SignUpdater
import dk.earthgame.TAT.SignUpdater.SignUpdater;

//Imports for Bukkit
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {
    public PluginDescriptionFile info = null;
    public SignUpdater SignUpdater;
    MyPluginPluginListener pluginListener = new MyPluginPluginListener(this);

    public void onEnable() {
        info = getDescription();
        PluginManager pm = getServer().getPluginManager();

        //Register when a plugin getting enabled and disabled
        pm.registerEvent(Type.PLUGIN_ENABLE, pluginListener, Priority.Low, this);
        pm.registerEvent(Type.PLUGIN_DISABLE, pluginListener, Priority.Low, this);
        
        //In case that required plugins is already enabled
        if (!pm.getPlugin("SignUpdater").isEnabled()) {
        	Plugin test = pm.getPlugin("SignUpdater");
			if (test != null) {
				SignUpdater = (SignUpdater)test;
				System.out.println("Established connection with SignUpdater!");
			}
        }

        System.out.println("["+ info.getName() +"] Enabled.");
    }

    public void onDisable() {
        info = null;
    }
}
