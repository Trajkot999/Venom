package dev.venom.util;

import dev.venom.Venom;
import dev.venom.check.Check;
import dev.venom.data.PlayerData;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@UtilityClass
public final class PunishUtil {

    public void punish(final Check check, final PlayerData data) {
        if (!check.getPunishCommand().isEmpty() && data.getPlayer().isOnline()) {
            Bukkit.getScheduler().runTask(Venom.INSTANCE.getPlugin(), () ->
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), ColorUtil.translate(check.getPunishCommand()
                            .replaceAll("%player%", data.getPlayer().getName())
                            .replaceAll("%check%", check.getName())
                            .replaceAll("%type%", String.valueOf(check.getType())))));
        }
    }
}