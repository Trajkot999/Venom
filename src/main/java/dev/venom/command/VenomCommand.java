package dev.venom.command;

import dev.venom.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
public abstract class VenomCommand implements Comparable<VenomCommand> {

    protected abstract boolean handle(final CommandSender sender, final Command command, final String label, final String[] args);

    public void sendLineBreak(final CommandSender sender) {
        sender.sendMessage("");
    }

    public void sendMessage(final CommandSender sender, final String message) {
        sender.sendMessage(ColorUtil.translate(message));
    }

    public CommandInfo getCommandInfo() {
        if (this.getClass().isAnnotationPresent(CommandInfo.class)) {
            return this.getClass().getAnnotation(CommandInfo.class);
        } else {
            System.err.println("CommandInfo annotation hasn't been added to the class " + this.getClass().getSimpleName() + ".");
        }
        return null;
    }

    @Override
    public int compareTo(VenomCommand o) {
        return 0;
    }
}