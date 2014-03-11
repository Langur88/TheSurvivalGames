package com.communitysurvivalgames.thesurvivalgames.managers;

import src.main.java.com.communitysurvivalgames.thesurvivalgames.event.KitGivenEvent;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.kits.Kit;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.kits.KitItem;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.util.IconMenu;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.util.ItemSerialization;

public class KitManager {
	private List<Kit> kits = new ArrayList<Kit>();
	private List<IconMenu> menus = new ArrayList<IconMenu>();
	private Map<String, Kit> playerKits = new HashMap<String, Kit>();

	public void loadKits() {
		readKitsFromFiles();

		if (kits.size() == 0)
			saveDefaultKits();

		for (int i = 0; i < 9; i++) {
			menus.add(new IconMenu("Select Your Kit - " + i, 54, new IconMenu.OptionClickEventHandler() {
				@Override
				public void onOptionClick(IconMenu.OptionClickEvent event) {
					if (event.getName().startsWith("Page")) {
						displayKitSelectionMenu(event.getPlayer(), (Integer.parseInt(event.getName().charAt(5) + "") - 1));
						return;
					}
					if (!(event.getPlayer().hasPermission("sg.kits.*") || event.getPlayer().hasPermission("sg.kits." + event.getName()) || event.getPlayer().isOp())) {
						event.getPlayer().sendMessage(ChatColor.RED + "Sorry, but you do not have permission to use this kit!");
						return;
					}
					event.getPlayer().sendMessage("You have chosen the " + event.getName() + " kit!");
					setPlayerKit(event.getPlayer(), getKit(event.getName()));
					event.setWillClose(true);

				}
			}, SGApi.getPlugin()));
		}
		//TODO Is this really the best way to go about this?
		int menu = 0;
		int row = 0;
		int index = 0;
		String currentC = kits.get(0).getType();

		for (Kit k : kits) {
			if (!currentC.equalsIgnoreCase(k.getType()) || index == 8) {
				currentC = k.getType();
				index = 0;
				row++;
				if (row == 5) {
					row = 0;
					menu++;
					if (menu == 9) {
						Bukkit.getLogger().severe("You can't have more that 486 kits!  (Are you insane?");
					}
				}
			}

			menus.get(menu).setOption((row * 9) + index, k.getIcon(), k.getName(), k.getIconLore());
			index++;

			//kits1.setOption(index, k.getIcon(), k.getName(), k.getIconLore());
			//index++;
		}

		for (int i = 0; i < 9; i++) {
			menus.get(i).setOption(45, new ItemStack(Material.PAPER), "Page 1", "Click to go to page 1!");
			menus.get(i).setOption(46, new ItemStack(Material.PAPER), "Page 2", "Click to go to page 2!");
			menus.get(i).setOption(47, new ItemStack(Material.PAPER), "Page 3", "Click to go to page 3!");
			menus.get(i).setOption(48, new ItemStack(Material.PAPER), "Page 4", "Click to go to page 4!");
			menus.get(i).setOption(49, new ItemStack(Material.PAPER), "Page 5", "Click to go to page 5!");
			menus.get(i).setOption(50, new ItemStack(Material.PAPER), "Page 6", "Click to go to page 6!");
			menus.get(i).setOption(51, new ItemStack(Material.PAPER), "Page 7", "Click to go to page 7!");
			menus.get(i).setOption(52, new ItemStack(Material.PAPER), "Page 8", "Click to go to page 8!");
			menus.get(i).setOption(53, new ItemStack(Material.PAPER), "Page 9", "Click to go to page 9!");
		}
	}

	public void readKitsFromFiles() {
		String[] files = SGApi.getPlugin().getDataFolder().list();

		for (String file : files) {
			if (file.startsWith("kit_")) {
				FileConfiguration kitData = YamlConfiguration.loadConfiguration(new File(SGApi.getPlugin().getDataFolder(), file));

				String kitName = kitData.getString("name");
				String type = kitData.getString("type");
				String[] iconU = kitData.getString("icon").split(":");
				ItemStack icon = null;
				if (iconU.length > 1) {
					if (iconU[0].equalsIgnoreCase("@p")) {
						icon = new ItemStack(Material.POTION);
						Potion potion = new Potion(1);
						potion.setType(PotionType.valueOf(iconU[1]));
						potion.setSplash(Boolean.valueOf(iconU[2]));
					}
				} else {
					icon = new ItemStack(Material.getMaterial(iconU[0]));
				}
				String iconLore = kitData.getString("iconLore");
				String serializedInventory = kitData.getString("items.lvl1.inventory");

				Bukkit.getServer().getLogger().info("Attempting to read inventory string of " + kitName + ". If it errors here, its a problem with this kit.");

				Inventory inventory = ItemSerialization.stringToInventory(serializedInventory); // TODO Not a temp solution, this is awesome!
				List<KitItem> list = new ArrayList<>();
				for (ItemStack itemStack : inventory) {
					KitItem ki = new KitItem();
					ki.setItem(itemStack);
					list.add(ki);
				}

				List<Integer> abilityIds = kitData.getIntegerList("ability-ids");

				kits.add(new Kit(kitName, type, list, icon, iconLore, abilityIds));
			}
		}

		Bukkit.getLogger().info("Sorted: " + kits);

		Collections.sort(kits, new Comparator<Kit>() {
			public int compare(Kit o1, Kit o2) {
				return o1.getType().compareTo(o2.getType());
			}
		});

		Bukkit.getLogger().info("Into: " + kits);
	}

	void saveDefaultKits() {
		SGApi.getPlugin().saveResource("kit_archer.yml", false);
		SGApi.getPlugin().saveResource("kit_crafter.yml", false);
		SGApi.getPlugin().saveResource("kit_enchanter.yml", false);
		SGApi.getPlugin().saveResource("kit_knight.yml", false);
		SGApi.getPlugin().saveResource("kit_notch.yml", false);
		SGApi.getPlugin().saveResource("kit_pacman.yml", false);
		SGApi.getPlugin().saveResource("kit_skeleton.yml", false);
		SGApi.getPlugin().saveResource("kit_toxicologist.yml", false);
		SGApi.getPlugin().saveResource("kit_zelda.yml", false);

		readKitsFromFiles();
	}

	public Kit getKit(String name) {
		for (Kit k : kits) {
			if (k.getName().equalsIgnoreCase(name))
				return k;
		}
		return kits.get(0);
	}

	public Kit getKit(Player p) {
		return playerKits.get(p.getName());
	}

	public List<Kit> getKits() {
		return kits;
	}

	public void displayDefaultKitSelectionMenu(Player p) {
		menus.get(0).open(p);
	}

	public void displayKitSelectionMenu(final Player p, final int i) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(SGApi.getPlugin(), new Runnable() {
			@Override
			public void run() {
				menus.get(i).open(p);

			}
		});
	}

	public void setPlayerKit(Player player, Kit kit) {
		Bukkit.getServer().getPluginManager().callEvent(new KitGivenEvent(player, kit));
		for (KitItem item : kit.getItems()) {
			playerKits.put(player.getName(), kit);
			player.getInventory().addItem(item.getItem());
		}
	}

}
