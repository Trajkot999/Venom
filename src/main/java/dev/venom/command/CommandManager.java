package dev.venom.command;

import dev.venom.command.impl.*;
import dev.venom.config.Config;
import dev.venom.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.List;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
public final class CommandManager implements CommandExecutor {

    private final List<VenomCommand> commands = new ArrayList<>();

    public CommandManager() {
        commands.add(new Alerts());
        commands.add(new Info());
        commands.add(new Reload());
        commands.add(new Violations());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] args) {
        if (commandSender.hasPermission("venom.cmd")) {
            if (args.length > 0) {
                for (final VenomCommand venomCommand : commands) {
                    final String commandName = venomCommand.getCommandInfo().name();
                    if (commandName.equals(args[0])) {
                        if (commandSender.hasPermission("venom." + commandName)) {
                            if (!venomCommand.handle(commandSender, command, string, args)) {
                                commandSender.sendMessage(ColorUtil.translate("&8[&4!&8]&4 " + "Usage: /venom " +
                                        venomCommand.getCommandInfo().name() + " " +
                                        venomCommand.getCommandInfo().syntax()));
                            }
                        } else {
                            return false;
                        }
                        return true;
                    }
                }
            } else {
                commandSender.sendMessage("");
                commandSender.sendMessage(ColorUtil.translate("&8[&4!&8]&4 " + "Venom Anti-Cheat Commands:\n" + " \n"));
                for (final VenomCommand venomCommand : commands) {
                    commandSender.sendMessage(ColorUtil.translate( "&8[&4!&8]&7 " + "/venom " +
                            venomCommand.getCommandInfo().name() + " " +
                            venomCommand.getCommandInfo().syntax()));
                }
                commandSender.sendMessage("");
                return true;
            }
        }
        return false;
    }
}