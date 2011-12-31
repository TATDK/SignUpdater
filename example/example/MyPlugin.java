package example;

//Listener (must be changed)
import example.Listeners.MyPluginPluginListener;

//Imports for SignUpdater
import dk.earthgame.TAT.SignUpdater.SignUpdater;

//Imports for Bukkit
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {
    public PluginDescriptionFile info = null;
    public SignUpdater SignUpdater;
    MyPluginPluginListener pluginListener = new MyPluginPluginListener(this);
    MyPluginBlockListener blockListener = new MyPluginBlockListener(this);

    public void onEnable() {
        info = getDescription();
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvent(Type.PLUGIN_ENABLE, pluginListener, Priority.Low, this);
        pm.registerEvent(Type.PLUGIN_DISABLE, pluginListener, Priority.Low, this);
        pm.registerEvent(Type.SIGN_CHANGE, blockListener, Priority.Low, this);

        System.out.println("["+ info.getName() +"] Enabled.");
    }

    public void onDisable() {
        info = null;
    }
}
