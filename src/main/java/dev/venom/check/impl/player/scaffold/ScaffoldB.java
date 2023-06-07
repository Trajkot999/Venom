package dev.venom.check.impl.player.scaffold;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.packet.Packet;
import dev.venom.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import io.github.retrooper.packetevents.utils.player.Direction;

/*
  This class may contain Tecnio, GladUrBad code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/GladUrBad/Medusa/tree/f00848c2576e4812283e6dc2dc05e29e2ced866a
*/
@CheckInfo(name = "Scaffold (B)", category = Category.PLAYER)
public final class ScaffoldB extends Check {

    public ScaffoldB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());

            if (PlayerUtil.getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_9)) return;

            if (!(wrapper.getBlockPosition().getX() == 1 && wrapper.getBlockPosition().getY() == 1 && wrapper.getBlockPosition().getZ() == 1)) {
                if (data.getPlayer().getItemInHand().getType().isBlock()) {
                    if (wrapper.getDirection() == Direction.DOWN) {
                        if (wrapper.getBlockPosition().getY() < data.getPositionProcessor().getY())
                            fail("bY: " + wrapper.getBlockPosition().getY() + ", pY: " + data.getPositionProcessor().getY());
                    }
                }
            }
        }
    }
}
