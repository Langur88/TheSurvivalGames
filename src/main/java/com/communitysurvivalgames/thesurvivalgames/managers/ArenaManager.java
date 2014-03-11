/**
 * Name: ArenaManager.java Edited: 7 December 2013
 *
 * @version 1.0.0
 */
package com.communitysurvivalgames.thesurvivalgames.managers;

import src.main.java.com.communitysurvivalgames.thesurvivalgames.configs.ArenaConfigTemplate;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.configs.ConfigTemplate;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.configs.WorldConfigTemplate;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.exception.ArenaNotFoundException;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.locale.I18N;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.multiworld.SGWorld;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.objects.MapHash;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.objects.SGArena;

public class ArenaManager {

	public final String prefix = ChatColor.DARK_AQUA + "[TheSurvivalGames]" + ChatColor.GOLD;
	public final String error = ChatColor.DARK_AQUA + "[TheSurvivalGames]" + ChatColor.RED;
	private final Map<String, SGWorld> creators = new HashMap<>();
	public final Map<String, Location> locs = new HashMap<>();
	public final Map<String, ItemStack[]> inv = new HashMap<>();
	public final Map<String, ItemStack[]> armor = new HashMap<>();
	private final List<SGArena> arenas = new ArrayList<>();

	/**
	 * The constructor for a new reference of the singleton
	 */
	public ArenaManager() {
	}

	/**
	 * Gets an arena from an integer ID
	 * 
	 * @param i The ID to get the Arena from
	 * @return The arena from which the ID represents. May be null.
	 * @throws ArenaNotFoundException
	 */
	public SGArena getArena(int i) throws ArenaNotFoundException {
		for (SGArena a : arenas) {
			if (a.getId() == i) {
				return a;
			}
		}
		throw new ArenaNotFoundException("Could not find given arena with given ID: " + i);
	}

	public SGArena getArena(Player p) throws ArenaNotFoundException {
		for (SGArena a : arenas) {
			if (a.getPlayers().contains(p.getName())) {
				return a;
			}
		}
		throw new ArenaNotFoundException("Could not find given arena with given Player: " + p.getDisplayName());
	}

	/**
	 * Adds a player to the specified arena
	 * 
	 * @param p The player to be added
	 * @param i The arena ID in which the player will be added to.
	 */
	public void addPlayer(Player p, int i) {
		SGArena a;
		try {
			a = getArena(i);
		} catch (ArenaNotFoundException e) {
			Bukkit.getLogger().severe(e.getMessage());
			return;
		}

		if (isInGame(p)) {
			p.sendMessage(error + I18N.getLocaleString("NOT_JOINABLE"));
			return;
		}

		if (a.getState() != null && !a.getState().equals(SGArena.ArenaState.WAITING_FOR_PLAYERS)) {
			// set player to spectator
			return;
		}

		p.sendMessage(prefix + "Type in /sg vote <ID> to vote for a map.");
		for (Map.Entry<MapHash, Integer> entry : a.votes.entrySet()) {
			p.sendMessage(ChatColor.GOLD.toString() + entry.getKey().getId() + ". " + ChatColor.DARK_AQUA.toString() + entry.getKey().getWorld().getDisplayName() + ": " + ChatColor.GREEN.toString() + entry.getValue());
		}

		a.getPlayers().add(p.getName());
		inv.put(p.getName(), p.getInventory().getContents());
		armor.put(p.getName(), p.getInventory().getArmorContents());

		p.getInventory().setArmorContents(null);
		p.getInventory().clear();
		p.setExp(0);

		locs.put(p.getName(), p.getLocation());
		p.teleport(a.lobby);

		// Ding!
		for (Player player : SGApi.getPlugin().getServer().getOnlinePlayers()) {
			player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
		}
	}

	/**
	 * Removes the player from an arena
	 * 
	 * @param p The player to remove from an arena
	 */
	public void removePlayer(Player p) {
		SGArena a = null;
		for (SGArena arena : arenas) {
			if (arena.getPlayers().contains(p.getName())) {
				a = arena;
			}
		}
		if (a == null || !a.getPlayers().contains(p.getName())) {
			p.sendMessage("Invalid operation!");
			return;
		}

		if (a.getSpectators().contains(p.getName()))
			a.getSpectators().remove(p.getName());
		a.getPlayers().remove(p.getName());

		p.getInventory().clear();
		p.getInventory().setArmorContents(null);

		p.getInventory().setContents(inv.get(p.getName()));
		p.getInventory().setArmorContents(armor.get(p.getName()));

		inv.remove(p.getName());
		armor.remove(p.getName());
		p.teleport(locs.get(p.getName()));
		locs.remove(p.getName());

		for (PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}

		p.setFireTicks(0);
	}

	/**
	 * Creates a lobby
	 */
	public SGArena createLobby(Player p) {
		SGArena a = new SGArena();
		
		int s = arenas.size();
		s += 1;

		a.createArena(s);

		a.lobby = p.getLocation();

		a.setState(SGArena.ArenaState.WAITING_FOR_PLAYERS);

		arenas.add(a);

		SGApi.getTimeManager(a).countdownLobby(1);

		return a;
	}

	/**
	 * Creates a new arena
	 * 
	 * @param creator The creator attributed with making the arena
	 */
	public void createWorld(final Player creator, final String worldName, final String display) {
		creator.getInventory().addItem(new ItemStack(Material.BLAZE_ROD));

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SGApi.getPlugin(), new Runnable() {
			@Override
			public void run() {
				// todo this is only a temp solution to create a new map
				SGWorld world = SGApi.getMultiWorldManager().createWorld(worldName, display);
				creators.put(creator.getName(), world);
			}
		});

	}

	public void createWorldFromDownload(final Player creator, final String worldName, final String displayName) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SGApi.getPlugin(), new Runnable() {
			@Override
			public void run() {
				SGApi.getMultiWorldManager().copyFromInternet(creator, worldName, displayName);
			}
		});

	}

	public void createWorldFromImport(final Player creator, final String worldName, final String displayName) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SGApi.getPlugin(), new Runnable() {
			@Override
			public void run() {
				SGApi.getMultiWorldManager().importWorldFromFolder(creator, worldName, displayName);
			}
		});

	}

	/**
	 * Removes an arena from memory
	 * 
	 * @param i The ID of the arena to be removed
	 */
	public void removeArena(int i) {
		SGArena a;
		try {
			a = getArena(i);
		} catch (ArenaNotFoundException e) {
			Bukkit.getLogger().severe(e.getMessage());
			return;
		}
		arenas.remove(a);
		new File(SGApi.getPlugin().getDataFolder().getAbsolutePath() + "/arenas/" + i + ".yml").delete();
	}

	/**
	 * Gets whether the player is playing
	 * 
	 * @param p The player that will be scanned
	 * @return Whether the player is in a game
	 */
	public boolean isInGame(Player p) {
		for (SGArena a : arenas) {
			if (a.getPlayers().contains(p.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Loads the game into memory after a shutdown or a relaod
	 */
	public void loadGames() {
		File arenas = new File(SGApi.getPlugin().getDataFolder().getAbsolutePath() + "/arenas/");
		File maps = new File(SGApi.getPlugin().getDataFolder().getAbsolutePath() + "/maps/");

		if (SGApi.getPlugin().getPluginConfig().isBungeecordMode()) {
			if (arenas.listFiles().length > 1) {
				Bukkit.getLogger().severe("You cannot have mutiple arenas on Bngeecord mode");
				Bukkit.getPluginManager().disablePlugin(SGApi.getPlugin());
			}
		}

		if (maps.listFiles().length == 0)
			return;
		for (File file : maps.listFiles()) {
			ConfigTemplate<SGWorld> configTemplate = new WorldConfigTemplate(file);
			SGWorld world = configTemplate.deserialize();
			Bukkit.getLogger().info("Loaded map! " + world.toString());
			SGApi.getMultiWorldManager().getWorlds().add(world);
		}

		if (arenas.listFiles().length == 0)
			return;
		for (File file : arenas.listFiles()) {
			ConfigTemplate<SGArena> configTemplate = new ArenaConfigTemplate(file);
			SGArena arena = configTemplate.deserialize();
			Bukkit.getLogger().info("Loaded arena! " + arena.toString());
			this.arenas.add(arena);

			arena.setState(SGArena.ArenaState.WAITING_FOR_PLAYERS);
			SGApi.getTimeManager(arena).countdownLobby(5);
		}
	}

	/**
	 * Gets the HashMap that contains the creators
	 * 
	 * @return The HashMap of creators
	 */
	public Map<String, SGWorld> getCreators() {
		return creators;
	}

	/**
	 * Get the arenas
	 * 
	 * @return the ArrayList of arenas
	 */
	public List<SGArena> getArenas() {
		return arenas;
	}

	/**
	 * Serializes a location to a string
	 * 
	 * @param l The location to serialize
	 * @return The serialized location
	 */
	public String serializeLoc(Location l) {
		return l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
	}

	public String serializeBlock(Block b) {
		return b.getType() + ":" + serializeLoc(b.getLocation());
	}

	/**
	 * Gets a location from a string
	 * 
	 * @param s The string to deserialize
	 * @return The location represented from the string
	 */
	public Location deserializeLoc(String s) {
		String[] st = s.split(",");
		return new Location(Bukkit.getWorld(st[0]), Integer.parseInt(st[1]), Integer.parseInt(st[2]), Integer.parseInt(st[3]));
	}

	public Block deserializeBlock(String st) {
		String[] s = st.split(":");
		return deserializeLoc(serializeLoc(new Location(Bukkit.getServer().getWorld(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3]), Integer.parseInt(s[4])))).getBlock();
	}
}
