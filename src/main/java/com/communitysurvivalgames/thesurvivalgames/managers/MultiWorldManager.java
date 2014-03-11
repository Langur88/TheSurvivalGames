/**
 * Name: MultiworldMain.java Created: 13 December 2013
 *
 * @version 1.0.0
 */
package com.communitysurvivalgames.thesurvivalgames.managers;

import src.main.java.com.communitysurvivalgames.thesurvivalgames.multiworld.SGWorld;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.util.UnTAR;

public class MultiWorldManager {
    
    List<SGWorld> worlds = new ArrayList<SGWorld>();

    public MultiWorldManager() {
    }

    SGWorld createWorld(String name, String display) {
        SGWorld world = new SGWorld(name, display);
        world.create();
        worlds.add(world);
        return world;
    }

    public void deleteWorld(String name) {
        SGWorld w = worldForName(name);
        if(worlds.contains(w)) {
            worlds.remove(w);
            w.remove();
        }
    }

    public World copyFromInternet(final Player sender, final String worldName, final String display) {// TODO:
                                                                                // Translate
        String url = "http://communitysurvivalgames.com/worlds/" + worldName + ".zip";
        /*
         * if (!FileUtil.exists(url)) {
         * sender.sendMessage("That arena dosen't seem to be in our database!  :("
         * ); sender.sendMessage(
         * "Look for worlds we do have at: http://communitysurvivalgames.com/worlds/"
         * ); return null; }
         */
        try {
            FileUtils.copyURLToFile(new URL(url), new File(SGApi.getPlugin().getDataFolder().getAbsolutePath(), "SG_ARENA_TMP.tar"));
        } catch (MalformedURLException e) {
           sender.sendMessage("Bad world name! Are you using special characters?");
            return null;
        } catch (IOException e) {
            sender.sendMessage("World downloading failed, try again later or something");
            return null;
        }
        try {
            UnTAR.unTar(new File(SGApi.getPlugin().getDataFolder(), "SG_ARENA_TMP.tar"), new File(Bukkit.getServer().getWorldContainer().getAbsolutePath(), worldName));
      } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArchiveException e) {
            e.printStackTrace();
        }
        if (!checkIfIsWorld(new File(Bukkit.getServer().getWorldContainer().getAbsolutePath(), worldName))) {
            sender.sendMessage("The downloaded world was not a world at all!");
            return null;
        }
        createWorld(worldName, display);

        return Bukkit.getWorld(worldName);
    }

    public World importWorldFromFolder(final Player sender, final String worldName, String display) {
        if (!checkIfIsWorld(new File(Bukkit.getServer().getWorldContainer().getAbsolutePath(), worldName))) {
            sender.sendMessage("That's not a world :/");
            return null;
        }
        createWorld(worldName, display);

        return Bukkit.getWorld(worldName);
    }

    public World createRandomWorld(final String worldName) {
        // TODO
        return Bukkit.getWorld(worldName);
    }
    
    public List<SGWorld> getWorlds() {
        return worlds;
    }

    public SGWorld worldForName(String name) {
        for(SGWorld world : getWorlds()) {
            if(world.getWorld().getName().equalsIgnoreCase(name)) {
                return world;
            }
        }
        return null;
    }

    private static boolean checkIfIsWorld(File worldFolder) {
        if (worldFolder.isDirectory()) {
            File[] files = worldFolder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String name) {
                    return name.equalsIgnoreCase("level.dat");
                }
            });
            if (files != null && files.length > 0) {
                return true;
            }
        }
        return false;
    }
}
