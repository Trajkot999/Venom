package dev.venom.check.impl.movement.speed;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.PlayerUtil;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Speed (F)", category = Category.MOVEMENT)
public final class SpeedF extends Check {

    public SpeedF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            final int iceTicks = data.getPositionProcessor().getSinceIceTicks();
            final int slimeTicks = data.getPositionProcessor().getSinceSlimeTicks();
            final int collidedVTicks = data.getPositionProcessor().getSinceBlockNearHeadTicks();

            final boolean takingVelocity = data.getVelocityProcessor().isTakingVelocity();
            final double velocityXZ = data.getVelocityProcessor().getVelocityXZ();

            double limit = PlayerUtil.getBaseSpeed(data.getPlayer(), 0.34F);

            if (iceTicks < 40 || slimeTicks < 40) limit += 0.34;
            if (collidedVTicks < 40) limit += 0.91;
            if (takingVelocity) limit += (velocityXZ + 0.15);

            final boolean exempt = isExempt(ExemptType.VEHICLE, ExemptType.PISTON, ExemptType.FLYING, ExemptType.TP);
            final boolean invalid = deltaXZ > limit && data.getPositionProcessor().getServerAirTicks() > 2;

            if (invalid && !exempt) {
                if (increaseBuffer() > 8) {
                    fail("dXZ: " + deltaXZ + ", vXZ: " + velocityXZ + ", l: " + limit + " b: " + buffer);
                    multiplyBuffer(0.75);
                }
            } else {
                multiplyBuffer(0.75);
            }
        }
    }
}