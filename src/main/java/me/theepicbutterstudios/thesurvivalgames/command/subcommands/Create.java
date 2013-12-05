/**
 * Name: Create.java
 * Created: 25 November 2013
 * Edited: 4 December 2013
 *
 * @version 1.0.0
 */

package me.theepicbutterstudios.thesurvivalgames.command.subcommands;

import me.theepicbutterstudios.thesurvivalgames.SGArena;
import me.theepicbutterstudios.thesurvivalgames.command.SubCommand;
import me.theepicbutterstudios.thesurvivalgames.managers.ArenaManager;

import org.bukkit.entity.Player;

public class Create implements SubCommand {

    /**
     * The create command. DO NOT CALL DIRECTLY. Only use in CommandHandler
     *
     * @param cmd The command that was executed
     * @param p The player that executed the command
     * @param args The arguments after the command
     */

    @Override
    public void execute(String cmd, Player p, String[] args) {
        if(cmd.equalsIgnoreCase("create")) {
            SGArena a = ArenaManager.getManager().createArena(p);
            p.sendMessage(ArenaManager.getManager().prefix + "Creating arena #" + a.getId());
            
            return; 
        }
    }
}
