package dev.venom.command.impl;

import dev.venom.Venom;
import dev.venom.command.CommandInfo;
import dev.venom.command.VenomCommand;
import dev.venom.config.Config;
import dev.venom.data.PlayerData;
import dev.venom.util.AlertUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CommandInfo(name = "alerts", purpose = "Toggles anitcheat alerts.")
public final class Alerts extends VenomCommand {

    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final PlayerData data = Venom.INSTANCE.getPlayerDataManager().getPlayerData(player);

            if (data != null) {
                if (AlertUtil.toggleAlerts(data) == AlertUtil.ToggleAlertType.ADD) {
                    sendMessage(sender, "&8[&4!&8]&8 " + "Toggled alerts on.");
                } else {
                    sendMessage(sender, "&8[&4!&8]&8 " + "Toggled alerts off.");
                }
                return true;
            }
        } else {
            sendMessage(sender, "Only players can execute this command.");
        }
        return false;
    }
}