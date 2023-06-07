package dev.venom.check.impl.movement.jesus;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Jesus (B)", category = Category.MOVEMENT)
public class JesusB extends Check {
    public JesusB(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final boolean inLiquid = data.getPositionProcessor().isFullySubmergedInLiquidStat();

            final double multiplier = data.getPositionProcessor().isFullySubmergedInLiquidStat() ? 0.8 : 0.5;

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double acceleration = deltaY - lastDeltaY;

            final double predicted = lastDeltaY * multiplier - 0.02F;
            final double difference = Math.abs(deltaY - predicted);

            final boolean exempt = isExempt(ExemptType.TP, ExemptType.VEHICLE, ExemptType.FLYING, ExemptType.PISTON,
                    ExemptType.CLIMBABLE, ExemptType.VELOCITY, ExemptType.WEB, ExemptType.SLIME, ExemptType.BOAT, ExemptType.CHUNK);
            final boolean invalid = difference > 0.075 && deltaY < -0.075 && acceleration <= 0.0 && inLiquid;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail(difference);
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }
}