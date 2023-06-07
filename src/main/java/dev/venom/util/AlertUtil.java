package dev.venom.util;

import dev.venom.Venom;
import dev.venom.check.Check;
import dev.venom.check.api.VenomSendAlertEvent;
import dev.venom.config.Config;
import dev.venom.data.PlayerData;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@Getter
@UtilityClass
public final class AlertUtil {

    private final Set<PlayerData> alerts = new HashSet<>();

    public ToggleAlertType toggleAlerts(final PlayerData data) {
        if (alerts.contains(data)) {
            alerts.remove(data);
            return ToggleAlertType.REMOVE;
        } else {
            alerts.add(data);
            return ToggleAlertType.ADD;
        }
    }

    public void handleAlert(final Check check, final PlayerData data, final String info) {
        final TextComponent alertMessage = new TextComponent(ColorUtil.translate(Config.ALERT_FORMAT)
                .replaceAll("%player%", data.getPlayer().getName())
                .replaceAll("%uuid%", data.getPlayer().getUniqueId().toString())
                .replaceAll("%check%", check.getName())
                .replaceAll("%ping%", Integer.toString(PlayerUtil.getPing(data.getPlayer())))
                .replaceAll("%type%", Character.toString(check.getType()))
                .replaceAll("%dev%", check.getCheckInfo().experimental() ? "(Dev)" : "")
                .replaceAll("%vl%", Integer.toString(check.getVl()))
                .replaceAll("%maxvl%", Integer.toString(check.getMaxVl()))
                .replaceAll("%ping%", Integer.toString(PlayerUtil.getPing(data.getPlayer())))
                .replaceAll("%tps%", new DecimalFormat("##.##").format(ServerUtil.getTPS())));

        alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + data.getPlayer().getName()));
        alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ColorUtil.translate(
                "&c" + "Information &8» &7" + info +
                        "\n" + "&c" + "Ping &8» &7" + PlayerUtil.getPing(data.getPlayer()) + "ms" +
                        "\n" + "&c" + "TPS &8» &7" + new DecimalFormat("##.##").format(ServerUtil.getTPS()) +
                        "\n" + "&8&o" + "Click to teleport to " + data.getPlayer().getName() + ".")).create()));

        final VenomSendAlertEvent event = new VenomSendAlertEvent(alertMessage, data.getPlayer(), check, info, check.category);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        switch (Venom.INSTANCE.getPlugin().getConfig().getString("violations.visibility")) {
            case "ADMIN":
                alerts.forEach(data1 -> data1.getPlayer().spigot().sendMessage(alertMessage));
                break;
            case "SELF":
                data.getPlayer().spigot().sendMessage(alertMessage);
                break;
            case "ADMIN-SELF":
                data.getPlayer().spigot().sendMessage(alertMessage);
                alerts.forEach(data1 ->
                {
                    if(!data1.getPlayer().equals(data.getPlayer())) {
                        data1.getPlayer().spigot().sendMessage(alertMessage);
                    }
                });
                break;
        }
    }

    public enum ToggleAlertType {
        ADD, REMOVE
    }
}