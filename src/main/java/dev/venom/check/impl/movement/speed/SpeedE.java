package dev.venom.check.impl.movement.speed;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;

/*
  This class may contain Nik (Alice) code.
  All credits are given to the author.
  Find more about this check here: https://youtu.be/-SiqszHE9rQ
*/
@CheckInfo(name = "Speed (E)", category = Category.MOVEMENT)
public final class SpeedE extends Check {

    public SpeedE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation() && !isExempt(ExemptType.TP_DELAY_SMALL, ExemptType.JOINED, ExemptType.CLIMBABLE, ExemptType.BOAT, ExemptType.VEHICLE)) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double accel = Math.abs(deltaXZ - lastDeltaXZ);

            final double squaredAccel = accel * 100;

            if (deltaYaw > 1.5F && deltaXZ > .15D && squaredAccel < 1.0E-5) {
                if(buffer++ > 2) {
                    fail("dY:" + deltaYaw + ", dXZ: " + deltaXZ + ", accel: " + squaredAccel);
                }
            } else buffer = Math.max(buffer - 0.01, 0);
        }
    }
}