package dev.venom.check.impl.packet.badpackets;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "BadPackets (H)", category = Category.PACKET)
public final class BadPacketsH extends Check {

    private int count = 0;
    private WrappedPacketInEntityAction.PlayerAction lastAction;

    public BadPacketsH(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isEntityAction()) {
            final WrappedPacketInEntityAction wrapper = new WrappedPacketInEntityAction(packet.getRawPacket());

            final boolean invalid = ++count > 1 && wrapper.getAction() == lastAction && wrapper.getAction().getActionValue() != (byte) 2 && wrapper.getAction().getActionValue() != (byte) 8;

            if (invalid)
                fail("c: " + count + ", aV: " + wrapper.getAction().getActionValue());

            lastAction = wrapper.getAction();
        }
    }
}
