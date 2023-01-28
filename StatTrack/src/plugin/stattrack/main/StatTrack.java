package plugin.stattrack.main;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import my.plugin.main.commands.OpenCommand;

public class StatTrack extends JavaPlugin implements Listener {
	
	public static StatTrack plugin;

    @Override
    public void onEnable() {
    	plugin = this;
    	getConfig().options().copyDefaults();
    	saveDefaultConfig();
    	new OpenCommand(this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new StatTrackHandler(), this);
    }
}
