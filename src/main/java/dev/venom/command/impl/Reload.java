package dev.venom.command.impl;

import dev.venom.Venom;
import dev.venom.command.CommandInfo;
import dev.venom.command.VenomCommand;
import dev.venom.config.Config;
import dev.venom.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CommandInfo(name = "reload", purpose = "Reload config.")
public final class Reload extends VenomCommand {

    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            if (args.length == 1) {
                final PlayerData player = Venom.INSTANCE.getPlayerDataManager().getPlayerData((Player) sender);
                if (player != null) {
                    Venom.INSTANCE.getPlugin().reloadConfig();
                    Config.updateConfig();
                    Venom.INSTANCE.getPlugin().reloadConfig();
                    Venom.INSTANCE.getPlugin().saveConfig();
                    Venom.INSTANCE.getPlugin().saveDefaultConfig();
                    sendMessage(sender, "&8[&4!&8]&8 Reloaded config.");
                    return true;
                }
            }
        }
        return false;
    }
}