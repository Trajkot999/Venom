package dev.venom.listener;

import dev.venom.Venom;
import dev.venom.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
public final class ClientBrandListener implements PluginMessageListener, Listener {

    @Override
    public void onPluginMessageReceived(final String channel, final Player player, final byte[] msg) {
        try {
            final PlayerData data = Venom.INSTANCE.getPlayerDataManager().getPlayerData(player);
            if (data == null) return;

            String client = new String(msg, "UTF-8").substring(1);

            data.setClientBrand(client);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        addChannel(player, "MC|BRAND");
    }

    private void addChannel(final Player player, final String channel) {
        try {
            player.getClass().getMethod("addChannel", String.class).invoke(player, channel);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                 | SecurityException e) {
            e.printStackTrace();
        }
    }
}