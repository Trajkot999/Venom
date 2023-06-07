package dev.venom.check.impl.movement.strafe;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.MathUtil;

/*
  This class may contain DerRedstoner code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/DerRedstoner/CheatGuard
*/
@CheckInfo(name = "Strafe (B)", category = Category.MOVEMENT)
public final class StrafeB extends Check {

    public StrafeB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            boolean isExempt = isExempt(ExemptType.TP, ExemptType.GHOST_BLOCK, ExemptType.ELYTRA, ExemptType.PISTON, ExemptType.VELOCITY, ExemptType.FLYING, ExemptType.VEHICLE, ExemptType.BOAT);

            if (isExempt) return;

            double deltaX = data.getPositionProcessor().getDeltaX();
            double deltaZ = data.getPositionProcessor().getDeltaZ();
            double lastDeltaX = data.getPositionProcessor().getLastDeltaX();
            double lastDeltaZ = data.getPositionProcessor().getLastDeltaZ();

            boolean xChange = MathUtil.isOpposite(deltaX, lastDeltaX) && Math.abs(deltaX - lastDeltaX) > 0.23;
            boolean zChange = MathUtil.isOpposite(deltaZ, lastDeltaZ) && Math.abs(deltaZ - lastDeltaZ) > 0.23;

            if (!data.getPositionProcessor().isClientGround() && Math.abs(data.getPositionProcessor().getDeltaY()) < 0.41999998688697815D && data.getPositionProcessor().getServerAirTicks() > 0 && (xChange || zChange)) {
                if(buffer++ > 2) {
                    fail("dY: " + data.getPositionProcessor().getDeltaY() + ", xC: " + xChange + ", zC: " + zChange);
                }
            }
        }
    }
}