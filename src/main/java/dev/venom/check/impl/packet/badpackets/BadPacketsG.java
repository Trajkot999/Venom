package dev.venom.check.impl.packet.badpackets;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.keepalive.WrappedPacketInKeepAlive;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "BadPackets (G)", category = Category.PACKET)
public final class BadPacketsG extends Check {

    private int lastId = -1;

    public BadPacketsG(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isKeepAlive()) {
            final WrappedPacketInKeepAlive wrapper = new WrappedPacketInKeepAlive(packet.getRawPacket());

            if (wrapper.getId() == lastId) {
                fail("id: " + wrapper.getId());
            }

            lastId = (int) wrapper.getId();
        }
    }
}
