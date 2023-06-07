package dev.venom;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import org.bukkit.plugin.java.JavaPlugin;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the author.
  Find more about this class on https://github.com/Tecnio/AntiHaxerman/blob/master/src/main/java/me/tecnio/antihaxerman/
*/
public final class VenomPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        PacketEvents.create(this).getSettings()
                .checkForUpdates(false)
                .fallbackServerVersion(ServerVersion.v_1_8_8);

        PacketEvents.get().load();
    }

    @Override
    public void onEnable() {
        PacketEvents.get().init();
        Venom.INSTANCE.start(this);
    }

    @Override
    public void onDisable() {
        PacketEvents.get().terminate();
        Venom.INSTANCE.stop(this);
    }
}