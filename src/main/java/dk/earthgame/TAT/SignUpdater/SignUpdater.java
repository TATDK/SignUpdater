package dk.earthgame.TAT.SignUpdater;

import org.bukkit.block.Sign;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * Plugin used by other plugins for updating signs
 *
 * @author TAT
 */
public class SignUpdater extends JavaPlugin {
    protected final Logger log = Logger.getLogger("Minecraft");
    PluginDescriptionFile pdfFile;
    public SignUpdater instance = null;
    boolean output;
    //If the updater is running
    boolean running;
    //The runner/worker
    Runner runner = new Runner();
    //Update containers
    HashMap<Sign, String[]> Highest = new HashMap<Sign, String[]>();
    HashMap<Sign, String[]> High = new HashMap<Sign, String[]>();
    HashMap<Sign, String[]> Normal = new HashMap<Sign, String[]>();
    HashMap<Sign, String[]> Low = new HashMap<Sign, String[]>();
    HashMap<Sign, String[]> Lowest = new HashMap<Sign, String[]>();

    @Override
    public void onEnable() {
        if (instance == null)
            instance = this;
        pdfFile = getDescription();
        this.saveDefaultConfig();

        output = this.getConfig().getBoolean("Output", false);

        if (output)
            log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    @Override
    public void onDisable() {
        if (output)
            log.info(pdfFile.getName() + " is disabled!");
    }

    /**
     * Add sign to the updater
     *
     * @param priority Priority for the update (Highest first)
     * @param sign     Sign that needs to be updated
     * @param lines    New lines on sign (Require all 4 lines)
     */
    public void AddSignUpdate(UpdaterPriority priority, Sign sign, String[] lines) {
        if (lines.length < 4) {
            if (output)
                log.info(pdfFile.getName() + " - Not enough lines");
            return;
        }
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
     *
     * @return Sign and new lines if found (else null)
     */
    Entry<Sign, String[]> getNext() {
        if (Highest.size() > 0)
            return Highest.entrySet().iterator().next();
        if (High.size() > 0)
            return High.entrySet().iterator().next();
        if (Normal.size() > 0)
            return Normal.entrySet().iterator().next();
        if (Low.size() > 0)
            return Low.entrySet().iterator().next();
        if (Lowest.size() > 0)
            return Lowest.entrySet().iterator().next();
        return null;
    }

    /**
     * Get priority of sign
     *
     * @param sign Sign
     * @return Priority of sign if found (else null)
     */
    UpdaterPriority getPriority(Sign sign) {
        if (Highest.containsKey(sign))
            return UpdaterPriority.HIGHEST;
        if (High.containsKey(sign))
            return UpdaterPriority.HIGH;
        if (Normal.containsKey(sign))
            return UpdaterPriority.NORMAL;
        if (Low.containsKey(sign))
            return UpdaterPriority.LOW;
        if (Lowest.containsKey(sign))
            return UpdaterPriority.LOWEST;
        return null;
    }

    /**
     * Remove sign from queue
     *
     * @param s Sign
     */
    void removeSign(Sign s) {
        if (Highest.containsKey(s))
            Highest.remove(s);
        else if (High.containsKey(s))
            High.remove(s);
        else if (Normal.containsKey(s))
            Normal.remove(s);
        else if (Low.containsKey(s))
            Low.remove(s);
        else if (Lowest.containsKey(s))
            Lowest.remove(s);
    }

    /**
     * Sign updating process
     *
     * @author TAT
     */
    class Runner implements Runnable {
        @Override
        public void run() {
            Entry<Sign, String[]> next = getNext();
            Sign s = next.getKey();
            String[] lines = next.getValue();

            s.setLine(0, lines[0]);
            s.setLine(1, lines[1]);
            s.setLine(2, lines[2]);
            s.setLine(3, lines[3]);
            s.update();

            //Remove the sign from the updatelist
            removeSign(s);

            if (getNext() != null)
                instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, runner, 2);
            else
                running = false;
        }
    }
}
