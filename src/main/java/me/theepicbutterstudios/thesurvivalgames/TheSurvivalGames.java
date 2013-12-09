/**
 * Name: TheSurvivalGames.java
 * Created: 19 November 2013
 * Edited: 9 December 2013
 *
 * @version 1.0.0
 */

package me.theepicbutterstudios.thesurvivalgames;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import me.theepicbutterstudios.thesurvivalgames.command.CommandHandler;
import me.theepicbutterstudios.thesurvivalgames.command.PartyCommandHandler;
import me.theepicbutterstudios.thesurvivalgames.command.subcommands.CreateCommand;
import me.theepicbutterstudios.thesurvivalgames.command.subcommands.HelpCommand;
import me.theepicbutterstudios.thesurvivalgames.command.subcommands.JoinCommand;
import me.theepicbutterstudios.thesurvivalgames.command.subcommands.party.ChatCommand;
import me.theepicbutterstudios.thesurvivalgames.command.subcommands.party.DeclineCommand;
import me.theepicbutterstudios.thesurvivalgames.command.subcommands.party.InviteCommand;
import me.theepicbutterstudios.thesurvivalgames.command.subcommands.party.LeaveCommand;
import me.theepicbutterstudios.thesurvivalgames.command.subcommands.party.ListCommand;
import me.theepicbutterstudios.thesurvivalgames.command.subcommands.party.PromoteCommand;
import me.theepicbutterstudios.thesurvivalgames.listeners.EntityDamageListener;
import me.theepicbutterstudios.thesurvivalgames.listeners.ItemListener;
import me.theepicbutterstudios.thesurvivalgames.listeners.SetupListener;
import me.theepicbutterstudios.thesurvivalgames.managers.ArenaManager;
import me.theepicbutterstudios.thesurvivalgames.runnables.Scoreboard;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TheSurvivalGames extends JavaPlugin {

	public void onEnable() {
		registerAll();
		setupDatabase();
		ArenaManager am = new ArenaManager(this);
		am.loadGames();

		getLogger().info("has been enabled!");
		getLogger().info("is a community project, join at http://dev.bukkit.org/bukkit-plugins/the-survival-games/");
		saveDefaultConfig();
	}

	public void onDisable() {
		getLogger().info("was disabled.");
	}

	public void registerAll() {
		// register all commands and listeners
		getCommand("sg").setExecutor(new CommandHandler());
		getCommand("party").setExecutor(new PartyCommandHandler());
		
		CommandHandler.register("help", new HelpCommand());
		CommandHandler.register("create", new CreateCommand());
		CommandHandler.register("join", new JoinCommand());
		
		PartyCommandHandler.register("chat", new ChatCommand());
		PartyCommandHandler.register("decline", new DeclineCommand());
		PartyCommandHandler.register("help", new HelpCommand());
		PartyCommandHandler.register("invite", new InviteCommand());
		PartyCommandHandler.register("join", new JoinCommand());
		PartyCommandHandler.register("leave", new LeaveCommand());
		PartyCommandHandler.register("list", new ListCommand());
		PartyCommandHandler.register("promote", new PromoteCommand());

		PluginManager pm = getServer().getPluginManager();

		pm.registerEvents(new ItemListener(this), this);
		pm.registerEvents(new SetupListener(), this);
		pm.registerEvents(new EntityDamageListener(), this);
		
		Scoreboard.registerScoreboard(this);
	}
	
    private void setupDatabase() {
        try {
            getDatabase().find(PlayerData.class).findRowCount();
        } catch (PersistenceException ex) {
            System.out.println("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
    }
 
    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(PlayerData.class);
        return list;
    }

}
