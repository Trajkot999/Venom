package dev.venom.command.impl;

import dev.venom.Venom;
import dev.venom.command.CommandInfo;
import dev.venom.command.VenomCommand;
import dev.venom.config.Config;
import dev.venom.data.PlayerData;
import dev.venom.util.ColorUtil;
import dev.venom.util.PlayerUtil;
import io.github.retrooper.packetevents.PacketEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CommandInfo(name = "info", syntax = "<player>", purpose = "Returns information about the players client.")
public final class Info extends VenomCommand {
    @Override
    protected boolean handle(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            final Player player = Bukkit.getPlayer(args[1]);

            if (player != null) {
                final PlayerData playerData = Venom.INSTANCE.getPlayerDataManager().getPlayerData(player);

                if (playerData != null) {
                    sendLineBreak(sender);
                    sendMessage(sender, "&8[&4!&8]&4 " + "All information about " + playerData.getPlayer().getName() + ":");
                    sendLineBreak(sender);
                    sendMessage(sender, "&8[&4!&8]&4 " + "Ping &8» &7" + PacketEvents.get().getPlayerUtils().getPing(playerData.getPlayer()) + "ms");
                    sendMessage(sender, "&8[&4!&8]&4 " + "Version &8» &7" + PlayerUtil.getPlayerVersion(playerData.getPlayer()));
                    sendMessage(sender, "&8[&4!&8]&4 " + "Checks amount &8» &7" + playerData.getChecks().size());
                    sendMessage(sender, "&8[&4!&8]&4 " + "Sensitivity &8» &7" + playerData.getRotationProcessor().getSensitivity() + "%");
                    final String clientBrand = playerData.getClientBrand() == null ? "&cError resolving client brand." : playerData.getClientBrand();
                    sendMessage(sender, ColorUtil.translate("&8[&4!&8]&4 " + "Client brand &8» &7" + clientBrand));
                    sendLineBreak(sender);
                    return true;
                }
            }
        }
        return false;
    }
}