/**
 * Name: MoveListener.java
 * Created: 29 December 2013
 *
 * @version 1.0.0
 */
package com.communitysurvivalgames.thesurvivalgames.listeners;

import src.main.java.com.communitysurvivalgames.thesurvivalgames.exception.ArenaNotFoundException;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.locale.I18N;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.managers.SGApi;
import src.main.java.com.communitysurvivalgames.thesurvivalgames.objects.SGArena;

public class MoveListener implements Listener {

    private static final List<String> list = new ArrayList<>();

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Location q = e.getFrom();
        Location w = e.getTo();

        if (list.contains(e.getPlayer().getName()) && (q.getBlockX() != w.getBlockX() || q.getBlockZ() != w.getBlockZ())) {
            e.setTo(e.getFrom());
        }

        try {
            if (SGApi.getArenaManager().isInGame(e.getPlayer()) && SGApi.getArenaManager().getArena(e.getPlayer()).getState() == SGArena.ArenaState.DEATHMATCH
                    && (Math.abs(e.getPlayer().getLocation().distanceSquared(
                        SGApi.getMultiWorldManager().worldForName(
                            SGApi.getArenaManager().getArena(e.getPlayer()).getArenaWorld().getName()).getCenter())) >= 0.5)) {
                e.setTo(e.getFrom());
                e.getPlayer().sendMessage(SGApi.getArenaManager().prefix + I18N.getLocaleString("NOT_HAPPY"));
            }
        } catch (ArenaNotFoundException ignored) {
        }
    }

    public static List<String> getPlayers() {
        return list;
    }

}
