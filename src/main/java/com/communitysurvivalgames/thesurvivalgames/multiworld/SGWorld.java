/**
 * Name: SGWorld.java Created: 16 December 2013
 *
 * @version 1.0.0
 */
package com.communitysurvivalgames.thesurvivalgames.multiworld;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SGWorld {

    public List<Location> locs = new ArrayList<>(0);
    private final String name;
    private final WorldCreator wc;

    private final String displayName;
    private Location center = null;

    public SGWorld(String name, String map) {
        this.name = name;
        displayName = map;

        wc = new WorldCreator(name);
        wc.environment(World.Environment.NORMAL);
        wc.type(WorldType.NORMAL);
    }

    public void init() {
        for (Location l : locs) {
            for (Location loc : locs) {
                if (Math.abs(l.getBlockX()) - Math.abs(loc.getBlockX()) <= 2) {
                    int radius = (int) (loc.distance(l) / 2);
                    center = loc.subtract(radius, loc.getY(), loc.getZ());
                }
            }
        }
    }

    public World getWorld() {
        return name != null ? Bukkit.getServer().getWorld(name) : null;
    }

    public World create() {
        if (Bukkit.getServer().getWorld(name) != null) {
            return Bukkit.getServer().getWorld(name);
        }

        return wc.createWorld();
    }

    public void remove() {
        World world = Bukkit.getServer().getWorld(name);

        for (Player p : world.getPlayers()) {
            p.kickPlayer("You suck ");
            //TODO teleport player somewhere safe
        }
        for (Entity e : world.getEntities()) {
            e.remove();
        }
        for (Chunk c : world.getLoadedChunks()) {
            c.unload(false, false);
            world.unloadChunk(c);
        }
        Bukkit.getServer().unloadWorld(world, false);
        deleteFiles(world.getWorldFolder());
    }

    private void deleteFiles(File path) {
        if (path.exists()) {
            File files[] = path.listFiles();
            for (File file : files != null ? files : new File[0]) {
                if (file.isDirectory()) {
                    deleteFiles(file);
                } else {
                    file.delete();
                }
            }
        }
        path.delete();
    }

    /**
     * Adds the next spawn into the list of spawns
     *
     * @param loc The location of the spawn
     */
    public void nextSpawn(Location loc) {
        locs.add(loc);
    }

}
