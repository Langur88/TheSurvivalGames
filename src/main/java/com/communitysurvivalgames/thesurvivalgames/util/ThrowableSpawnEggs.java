package com.communitysurvivalgames.thesurvivalgames.util;


public class ThrowableSpawnEggs implements Listener {

	Map<Egg, EntityType> eggs = new HashMap<Egg, EntityType>();

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if ((player.hasPermission("tse.use")) && ((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))) {
			if (event.getItem() == null)
				return;
			ItemStack item = event.getItem();
			if ((!(item.getData() instanceof SpawnEgg)) || (item == null))
				return;
			SpawnEgg segg = (SpawnEgg) item.getData();
			Egg egg = (Egg) event.getPlayer().launchProjectile(Egg.class);
			this.eggs.put(egg, segg.getSpawnedType());
			if (item.getAmount() > 1)
				item.setAmount(item.getAmount() - 1);
			else {
				player.getInventory().remove(item);
			}
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void throwEgg(PlayerEggThrowEvent event) {
		Egg egg = event.getEgg();
		if (this.eggs.containsKey(egg)) {
			EntityType entityType = (EntityType) this.eggs.get(egg);
			Entity entity = egg.getWorld().spawnEntity(egg.getLocation(), entityType);
			if (entityType == EntityType.SHEEP) {
				((Sheep) entity).setColor(org.bukkit.DyeColor.values()[((int) (java.lang.Math.random() * org.bukkit.DyeColor.values().length))]);
				event.setHatching(false);
			}
		}
	}
}
