package dev.venom.check.impl.packet.badpackets;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "BadPackets (I)", category = Category.PACKET)
public final class BadPacketsI extends Check {

    private float lastYaw = 0.0f, lastPitch = 0.0f;

    public BadPacketsI(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && PlayerUtil.getClientVersion(data.getPlayer()).isOlderThan(ClientVersion.v_1_12_2)) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());

            if (!wrapper.isLook() || data.getPlayer().isInsideVehicle() || data.getActionProcessor().isInsideVehicle()) return;

            final float yaw = wrapper.getYaw();
            final float pitch = wrapper.getPitch();

            final boolean exempt = isExempt(ExemptType.TP_DELAY_SMALL, ExemptType.RESPAWN, ExemptType.GHOST_BLOCK_TICKS_SMALL, ExemptType.BOAT, ExemptType.VEHICLE);

            if (yaw == lastYaw && pitch == lastPitch && !exempt) {
                fail("y: " + yaw + ", p: " + pitch);
            }

            lastYaw = yaw;
            lastPitch = pitch;
        }
    }
}
