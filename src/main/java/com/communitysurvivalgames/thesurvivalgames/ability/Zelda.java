package com.communitysurvivalgames.thesurvivalgames.ability;

import src.main.java.com.communitysurvivalgames.thesurvivalgames.event.GameStartEvent;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.event.PlayerKilledEvent;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.util.FireworkEffectPlayer;

public class Zelda extends SGAbility implements Listener {
	public Zelda() {
		super(1);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onGameStart(GameStartEvent event) {
		for (String p : event.getArena().getPlayers()) {
			if (hasAbility(p)) {
				Player player = Bukkit.getPlayer(p);
				player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 99999, 5, false));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (this.hasAbility(player)) {
			if (player.getItemInHand().getType() == Material.SPECKLED_MELON && player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("Zelda Heart")) {
				ItemStack item = player.getItemInHand();
				item.setAmount(1);
				player.getInventory().remove(item);
				if (player.getHealth() >= 14) {
					player.setHealth(20);
				}

				if (player.getHealth() < 14)
					player.setHealth(player.getHealth() + 6);
				FireworkEffect fEffect = FireworkEffect.builder().flicker(false).withColor(Color.YELLOW).withFade(Color.GREEN).with(Type.BALL).trail(true).build();
				try {
					FireworkEffectPlayer.getFireworkEffectPlayer().playFirework(event.getPlayer().getWorld(), event.getPlayer().getLocation(), fEffect);
				} catch (Exception e) {
					//If the firework dosen't work... to bad 
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onKill(PlayerKilledEvent event) {
		ItemStack zeldaHeart = new ItemStack(Material.SPECKLED_MELON);
		ItemMeta meta = zeldaHeart.getItemMeta();
		meta.setDisplayName("Zelda Heart");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&3Heal when right clicked - hearts from the dead"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&1Zelda Kit - LvL1"));
		meta.setLore(lore);
		zeldaHeart.setItemMeta(meta);
		event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), zeldaHeart);
	}
}
