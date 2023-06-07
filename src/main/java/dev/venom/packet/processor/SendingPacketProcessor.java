package dev.venom.packet.processor;

import dev.venom.data.PlayerData;
import dev.venom.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.out.entityvelocity.WrappedPacketOutEntityVelocity;
import io.github.retrooper.packetevents.packetwrappers.play.out.explosion.WrappedPacketOutExplosion;
import io.github.retrooper.packetevents.packetwrappers.play.out.position.WrappedPacketOutPosition;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
public final class SendingPacketProcessor  {

    public void handle(final PlayerData data, final Packet packet) {
        if (packet.isVelocity()) {
            final WrappedPacketOutEntityVelocity wrapper = new WrappedPacketOutEntityVelocity(packet.getRawPacket());
            if (wrapper.getEntity() == data.getPlayer()) {
                data.getVelocityProcessor().handle(wrapper.getVelocityX(), wrapper.getVelocityY(), wrapper.getVelocityZ());
            }
        }
        if (packet.isExplosion()) {
            final WrappedPacketOutExplosion wrapper = new WrappedPacketOutExplosion(packet.getRawPacket());
            data.getVelocityProcessor().handle(wrapper.getPlayerMotionX(), wrapper.getPlayerMotionY(), wrapper.getPlayerMotionZ());
        }

        if (packet.isOutPosition()) {
            final WrappedPacketOutPosition wrapper = new WrappedPacketOutPosition(packet.getRawPacket());

            data.getPositionProcessor().handleTeleport(wrapper);
        }
        if (!data.getPlayer().hasPermission("venom.bypass") || data.getPlayer().isOp()) {
            data.getChecks().forEach(check -> check.handle(packet));
        }
    }
}