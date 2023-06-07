package dev.venom.listener;

import dev.venom.Venom;
import dev.venom.check.Check;
import dev.venom.check.CheckManager;
import dev.venom.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
public final class BukkitEventListener implements Listener {

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        final PlayerData data = Venom.INSTANCE.getPlayerDataManager().getPlayerData(event.getPlayer());
        if (data != null) {
            data.getActionProcessor().handleBukkitBlockBreak();
        }
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final PlayerData data = Venom.INSTANCE.getPlayerDataManager().getPlayerData(event.getPlayer());
        if (data != null) {
            data.getActionProcessor().handleInteract(event);
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
        final PlayerData data = Venom.INSTANCE.getPlayerDataManager().getPlayerData(event.getPlayer());
        if (data != null) {
            data.getActionProcessor().fixBukkitInventoryOpen();
        }
    }
}