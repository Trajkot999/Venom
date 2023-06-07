package dev.venom.util;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import lombok.experimental.UtilityClass;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@UtilityClass
public final class ServerUtil {

    public double getTPS() {
        return Math.min(20.0, PacketEvents.get().getServerUtils().getTPS());
    }

    public ServerVersion getServerVersion() {
        return PacketEvents.get().getServerUtils().getVersion();
    }

    public boolean isLowerThan1_8() {
        return getServerVersion().isOlderThan(ServerVersion.v_1_8);
    }
}