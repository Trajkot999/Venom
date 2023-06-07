package dev.venom.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@UtilityClass
public final class ColorUtil {

    public String translate(final String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}