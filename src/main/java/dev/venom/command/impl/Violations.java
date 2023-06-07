package dev.venom.command.impl;

import dev.venom.Venom;
import dev.venom.command.CommandInfo;
import dev.venom.command.VenomCommand;
import dev.venom.config.Config;
import dev.venom.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CommandInfo(name = "violations", syntax = "<player>", purpose = "Describes violations for the player.")
public final class Violations extends VenomCommand {
    @Override
    protected boolean handle(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            final Player player = Bukkit.getPlayer(args[1]);

            if (player != null) {
                final PlayerData playerData = Venom.INSTANCE.getPlayerDataManager().getPlayerData(player);

                if (playerData != null) {
                    sendLineBreak(sender);
                    sendMessage(sender, "&8[&4!&8]&4 " + "Violations for " + playerData.getPlayer().getName() + "&8:");
                    sendLineBreak(sender);
                    sendMessage(sender, "&8[&4!&8]&4 " + "Total violations &8» &7" + playerData.getTotalViolations());
                    sendMessage(sender, "&8[&4!&8]&4 " + "Combat violations &8» &7" + playerData.getCombatViolations());
                    sendMessage(sender, "&8[&4!&8]&4 " + "Movement violations &8» &7" + playerData.getMovementViolations());
                    sendMessage(sender, "&8[&4!&8]&4 " + "Player violations &8» &7" + playerData.getPlayerViolations());
                    sendLineBreak(sender);
                    return true;
                }
            }
        }
        return false;
    }
}