package dev.venom.check.impl.movement.fly;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import org.bukkit.Bukkit;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Fly (A)", category = Category.MOVEMENT)
public final class FlyA extends Check {

    public FlyA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double velocityY = data.getVelocityProcessor().getVelocityY();

            final int clientAirTicks = data.getPositionProcessor().getClientAirTicks();

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double predicted = (lastDeltaY - 0.08) * 0.9800000190734863;

            double fixedPredicted = Math.abs(predicted) < 0.005 ? 0.0 : predicted;
            if (isExempt(ExemptType.VELOCITY_ON_TICK, ExemptType.VELOCITY)) fixedPredicted = velocityY;

            final double difference = Math.abs(deltaY - fixedPredicted);

            final boolean exempt = isExempt(ExemptType.PISTON, ExemptType.VEHICLE, ExemptType.TP,
                    ExemptType.LIQUID, ExemptType.BOAT, ExemptType.FLYING, ExemptType.WEB, ExemptType.JOINED,
                    ExemptType.SLIME_ON_TICK, ExemptType.CLIMBABLE, ExemptType.CHUNK, ExemptType.VOID, ExemptType.UNDER_BLOCK,
                    ExemptType.VELOCITY_ON_TICK, ExemptType.VELOCITY, ExemptType.GHOST_BLOCK_TICKS_SMALL);
            final boolean invalid = difference > 1E-8 && (clientAirTicks > 1 && data.getPositionProcessor().getSinceTeleportTicks() > 2
                    || data.getPositionProcessor().getServerAirTicks() > 2 && !isExempt(ExemptType.GHOST_BLOCK));

            if (invalid && !exempt) {
                if (increaseBuffer() > 3) {
                    fail(String.format("pred: %.4f delta: %.4f vel: %s", fixedPredicted, deltaY, isExempt(ExemptType.VELOCITY_ON_TICK, ExemptType.VELOCITY)));
                }
            } else {
                decreaseBufferBy(0.15);
            }
        }
    }
}
