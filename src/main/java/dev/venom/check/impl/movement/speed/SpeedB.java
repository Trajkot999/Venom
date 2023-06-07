package dev.venom.check.impl.movement.speed;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.data.processor.PositionProcessor;
import dev.venom.data.processor.VelocityProcessor;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Speed (B)", category = Category.MOVEMENT)
public final class SpeedB extends Check {

    public SpeedB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final int airTicks = data.getPositionProcessor().getClientAirTicks();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double predicted = (lastDeltaXZ * 0.91F) + 0.026F;
            final double difference = deltaXZ - predicted;

            final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.VEHICLE, ExemptType.BOAT,
                    ExemptType.UNDER_BLOCK, ExemptType.TP, ExemptType.LIQUID, ExemptType.PISTON,
                    ExemptType.CLIMBABLE, ExemptType.VELOCITY_ON_TICK);
            final boolean invalid = difference > 1E-5 && predicted > 0.075 && airTicks > 2;

            if (invalid && !exempt) {
                if (buffer++ > 5) {
                    fail("dXZ: " + deltaXZ + ", diff: " + difference + ", pred: " + predicted + ", b: " + buffer);
                }
            } else {
                decreaseBufferBy(0.5);
            }
        }
    }
}