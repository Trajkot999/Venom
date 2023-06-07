package dev.venom.check.impl.player.scaffold;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.packet.Packet;
import dev.venom.util.PlayerUtil;
import dev.venom.util.ServerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import io.github.retrooper.packetevents.utils.player.Direction;
import io.github.retrooper.packetevents.utils.server.ServerVersion;

/*
  This class may contain Islandscout code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/HawkAnticheat/Hawk
*/
@CheckInfo(name = "Scaffold (C)", category = Category.PLAYER)
public final class ScaffoldC extends Check {

    public ScaffoldC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            if (ServerUtil.getServerVersion().isNewerThanOrEquals(ServerVersion.v_1_9) || PlayerUtil.getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_9)) return;
            WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());
            if (wrapper.getDirection() == Direction.INVALID) fail("Invalid direction");
            float x = wrapper.getCursorPosition().isPresent() ? wrapper.getCursorPosition().get().getX() : 0;
            float y = wrapper.getCursorPosition().isPresent() ? wrapper.getCursorPosition().get().getY() : 0;
            float z = wrapper.getCursorPosition().isPresent() ? wrapper.getCursorPosition().get().getZ() : 0;
            for (float value : new float[] { x, y, z }) {
                if (value > 1 || value < 0) fail("value=" + value);
            }
        }
    }
}