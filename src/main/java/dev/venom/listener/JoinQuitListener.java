package dev.venom.listener;

import dev.venom.Venom;
import dev.venom.util.AlertUtil;
import dev.venom.util.PlayerUtil;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
public final class JoinQuitListener implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Venom.INSTANCE.getPlayerDataManager().add(event.getPlayer());
        Venom.INSTANCE.getPlayerDataManager().getPlayerData(event.getPlayer()).setJoinTime(System.currentTimeMillis());

        if (event.getPlayer().hasPermission("venom.alerts")) {
            AlertUtil.toggleAlerts(Venom.INSTANCE.getPlayerDataManager().getPlayerData(event.getPlayer()));
        }

        //AlertUtil.toggleAlerts(Venom.INSTANCE.getPlayerDataManager().getPlayerData(event.getPlayer()));

        if (!PlayerUtil.getClientVersion(Venom.INSTANCE.getPlayerDataManager().getPlayerData(event.getPlayer()).getPlayer()).isOlderThanOrEquals(ClientVersion.v_1_9)) {
            boolean valid = Venom.INSTANCE.getPlayerDataManager().getPlayerData(event.getPlayer()).getPlayer().getInventory().getChestplate() != null && Venom.INSTANCE.getPlayerDataManager().getPlayerData(event.getPlayer()).getPlayer().getInventory().getChestplate().toString().contains("ELYTRA");
            if (valid) {
                Venom.INSTANCE.getPlayerDataManager().getPlayerData(event.getPlayer()).getActionProcessor().setElytra(true);
            } else {
                Venom.INSTANCE.getPlayerDataManager().getPlayerData(event.getPlayer()).getActionProcessor().setElytra(false);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Venom.INSTANCE.getPlayerDataManager().remove(event.getPlayer());
    }
}