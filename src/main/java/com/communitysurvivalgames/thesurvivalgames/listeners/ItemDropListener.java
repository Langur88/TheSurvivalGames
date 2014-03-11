package com.communitysurvivalgames.thesurvivalgames.listeners;

import src.main.java.com.communitysurvivalgames.thesurvivalgames.managers.EnchantmentManager;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.managers.SGApi;

public class ItemDropListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onItemDrop(PlayerDropItemEvent event) {
		SGApi.getEnchantmentManager();
		if (event.getItemDrop().getItemStack().containsEnchantment(EnchantmentManager.undroppable)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "You cannot drop that item!");
		}
	}
}
