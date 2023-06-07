package dev.venom.check.impl.movement.strafe;

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
@CheckInfo(name = "Strafe (A)", category = Category.MOVEMENT)
public final class StrafeA extends Check {

    public StrafeA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaX = data.getPositionProcessor().getDeltaX();
            final double deltaZ = data.getPositionProcessor().getDeltaZ();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            final double lastDeltaX = data.getPositionProcessor().getLastDeltaX();
            final double lastDeltaZ = data.getPositionProcessor().getLastDeltaZ();

            final int airTicks = data.getPositionProcessor().getClientAirTicks();

            final double blockSlipperiness = 0.91F;
            final double attributeSpeed = 0.026;

            final double predictedDeltaX = lastDeltaX * blockSlipperiness;
            final double predictedDeltaZ = lastDeltaZ * blockSlipperiness;

            final double diffX = Math.abs(deltaX - predictedDeltaX);
            final double diffZ = Math.abs(deltaZ - predictedDeltaZ);

            final boolean exempt = isExempt(ExemptType.TP_DELAY_SMALL, ExemptType.WEB, ExemptType.ELYTRA, ExemptType.PISTON, ExemptType.FLYING, ExemptType.UNDER_BLOCK, ExemptType.VEHICLE, ExemptType.BOAT, ExemptType.CLIMBABLE, ExemptType.LIQUID, ExemptType.VELOCITY);
            final boolean invalid = (diffX > attributeSpeed || diffZ > attributeSpeed) && deltaXZ > .05 && airTicks > 2;

            if(!isExempt(ExemptType.JUMP) && !isExempt(ExemptType.COMBAT)) {
                if (invalid && !exempt) {
                    if (buffer++ > 2) {
                        fail("dX: " + deltaZ + ", dZ: " + deltaZ + ", dXZ: " + deltaXZ + ", aT: " + airTicks + ", pDX: " + predictedDeltaX + ", pDZ: " + predictedDeltaZ);
                    }
                } else {
                    buffer = Math.max(buffer - 0.1, 0);
                }
            }
        }
    }
}