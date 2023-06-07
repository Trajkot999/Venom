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
@CheckInfo(name = "Speed (D)", category = Category.MOVEMENT)
public final class SpeedD extends Check {

    public SpeedD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double limit = (PlayerUtil.getBaseSpeed(data.getPlayer()) + 0.1) + (isExempt(ExemptType.VELOCITY) ? data.getVelocityProcessor().getVelocityXZ() + 0.15 : 0.0);

            final double acceleration = deltaXZ - lastDeltaXZ;

            final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.VEHICLE, ExemptType.BOAT,
                    ExemptType.UNDER_BLOCK, ExemptType.TP, ExemptType.PISTON, ExemptType.CLIMBABLE,
                    ExemptType.VEHICLE, ExemptType.SLIME, ExemptType.JOINED);
            final boolean invalid = acceleration > limit;

            if (invalid && !exempt) fail();
        }
    }
}