package dev.venom.manager;

import dev.venom.Venom;
import dev.venom.data.PlayerData;
import dev.venom.util.Pair;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
public final class TickManager implements Runnable {

    @Getter
    private int ticks;
    private static BukkitTask task;

    public void start() {
        assert task == null : "TickProcessor has already been started!";

        task = Bukkit.getScheduler().runTaskTimer(Venom.INSTANCE.getPlugin(), this, 0L, 1L);
    }

    public void stop() {
        if (task == null) return;

        task.cancel();
        task = null;
    }

    @Override
    public void run() {
        ticks++;

        for (final PlayerData data : Venom.INSTANCE.getPlayerDataManager().getAllData()) {
            final Entity target = data.getCombatProcessor().getTarget();
            final Entity lastTarget = data.getCombatProcessor().getLastTarget();

            if (target != null && lastTarget != null) {
                if (target != lastTarget) data.getTargetLocations().clear();

                final Location location = target.getLocation();
                data.getTargetLocations().add(new Pair<>(location, data.getPositionProcessor().getTicks()));
            }
        }
    }
}