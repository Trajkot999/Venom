package dev.venom.check.impl.packet.badpackets;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.steervehicle.WrappedPacketInSteerVehicle;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "BadPackets (D)", category = Category.PACKET)
public final class BadPacketsD extends Check {

    public BadPacketsD(final PlayerData data) {
        super(data);
    }


    @Override
    public void handle(final Packet packet) {
        if (packet.isSteerVehicle()) {
            final WrappedPacketInSteerVehicle wrapper = new WrappedPacketInSteerVehicle(packet.getRawPacket());

            if (data.getPlayer().getVehicle() == null) {
                if (!data.getPositionProcessor().isNearVehicle()) {
                    fail("spoofed vehicle LLL");
                }
            }

            final float forward = Math.abs(wrapper.getForwardValue());
            final float sideways = Math.abs(wrapper.getSideValue());

            if (sideways > 0.98F || forward > 0.98F) {
                fail("s: " + sideways + ", f: " + forward);
            }
        }
    }
}
