package dk.earthgame.TAT.SignUpdater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.block.Sign;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

/**
 * Plugin used by other plugins for updating signs
 * @author TAT
 */
public class SignUpdater extends JavaPlugin {
	protected final Logger log = Logger.getLogger("Minecraft");
	Configuration config;
	PluginDescriptionFile pdfFile;
	SignUpdater signUpdater = this;
	boolean output;
	//If the updater is running
	boolean running;
	//The runner/worker
	Runner runner = new Runner();
	//Update containers
	HashMap<Sign, String[]> Highest = new HashMap<Sign, String[]>();
	HashMap<Sign, String[]> High    = new HashMap<Sign, String[]>();
	HashMap<Sign, String[]> Normal  = new HashMap<Sign, String[]>();
	HashMap<Sign, String[]> Low     = new HashMap<Sign, String[]>();
	HashMap<Sign, String[]> Lowest  = new HashMap<Sign, String[]>();
	
	@Override
	public void onEnable() {
		pdfFile = getDescription();
		CreateConfig();
		LoadConfig();
		
		if (output) {
			log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		}
	}

	@Override
	public void onDisable() {
		if (output) {
			log.info(pdfFile.getName() + " is disabled!");
		}
	}
	
	/**
	 * Add sign to the updater
	 * @param priority Priority for the update (Highest first)
	 * @param sign Sign that needs to be updated
	 * @param lines New lines on sign (Require all 4 lines)
	 */
	public void AddSignUpdate(UpdaterPriority priority, Sign sign, String[] lines) {
		switch (priority) {
			case HIGHEST:
				Highest.put(sign, lines);
				break;
			case HIGH:
				High.put(sign, lines);
				break;
			case NORMAL:
				Normal.put(sign, lines);
				break;
			case LOW:
				Low.put(sign, lines);
				break;
			case LOWEST:
				Lowest.put(sign, lines);
				break;
			default:
				if (output)
					log.info(pdfFile.getName() + " - Unknown UpdaterPriority");
		}
		if (!running) {
			running = true;
			getServer().getScheduler().scheduleSyncDelayedTask(this, runner, 2);
		}
	}
	
	/**
	 * Get the next sign in queue
	 * @return Sign and new lines if found (else null)
	 */
	Entry<Sign, String[]> getNext() {
		if (Highest.size() > 0) {
			return Highest.entrySet().iterator().next();
		}
		if (High.size() > 0) {
			return High.entrySet().iterator().next();
		}
		if (Normal.size() > 0) {
			return Normal.entrySet().iterator().next();
		}
		if (Low.size() > 0) {
			return Low.entrySet().iterator().next();
		}
		if (Lowest.size() > 0) {
			return Lowest.entrySet().iterator().next();
		}
		return null;
	}
	
	/**
	 * Get priority of sign
	 * @param s Sign
	 * @return Priority of sign if found (else null)
	 */
	UpdaterPriority getPriority(Sign s) {
		if (Highest.containsKey(s))
			return UpdaterPriority.HIGHEST;
		if (High.containsKey(s))
			return UpdaterPriority.HIGH;
		if (Normal.containsKey(s))
			return UpdaterPriority.NORMAL;
		if (Low.containsKey(s))
			return UpdaterPriority.LOW;
		if (Lowest.containsKey(s))
			return UpdaterPriority.LOWEST;
		return null;
	}
	
	/**
	 * Remove sign from queue
	 * @param s Sign
	 */
	void removeSign(Sign s) {
		if (Highest.containsKey(s)) {
			Highest.remove(s);
		} else if (High.containsKey(s)) {
			High.remove(s);
		} else if (Normal.containsKey(s)) {
			Normal.remove(s);
		} else if (Low.containsKey(s)) {
			Low.remove(s);
		} else if (Lowest.containsKey(s)) {
			Lowest.remove(s);
		}
	}
	
	/**
	 * Sign updating process
	 * @author TAT
	 */
	class Runner implements Runnable {
		@Override
        public void run() {
            Sign s = getNext().getKey();
            String[] lines = getNext().getValue();
            
            s.setLine(0, lines[0]);
            s.setLine(1, lines[1]);
            s.setLine(2, lines[2]);
            s.setLine(3, lines[3]);
            s.update();
            
            //Remove the sign from the updatelist
            removeSign(s);
            
            if (getNext() != null) {
                signUpdater.getServer().getScheduler().scheduleSyncDelayedTask(signUpdater, runner, 2);
            } else {
            	running = false;
            }

        }
	}
	
	/**
	 * Load configuration
	 */
	void LoadConfig() {
		config = new Configuration(new File(getDataFolder(), "config.yml"));
		config.load();
		
		output = config.getBoolean("Output", false);
		
		if (output)
			log.info(pdfFile.getName() + " - Config loaded");
	}
	
	/**
	 * Create configuration if not found
	 */
	void CreateConfig() {
		getDataFolder().mkdir();
		String name = "config.yml";
		File actual = new File(getDataFolder(), name);
		if (!actual.exists()) {
			InputStream input = SignUpdater.class.getResourceAsStream("/Config/" + name);
			if (input != null) {
				FileOutputStream output = null;

				try {
					output = new FileOutputStream(actual);
					byte[] buf = new byte[8192];
					int length = 0;
					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}
				} catch (IOException e) {
					e.printStackTrace();
					log.warning(pdfFile.getName() + " - Error creating config file!");
				} finally {
					try {
						if (input != null)
							input.close();
					} catch (IOException e) {}

					try {
						if (output != null)
							output.close();
					} catch (IOException e) {}
				}
			}
		}
	}
}
