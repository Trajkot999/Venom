package dev.venom.check.impl.combat.aimassist;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.MathUtil;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
/*
  This class may contain Tecnio, GladUrBad code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/GladUrBad/Medusa/tree/f00848c2576e4812283e6dc2dc05e29e2ced866a
*/
@CheckInfo(name = "AimAssist (D)", category = Category.COMBAT)
public final class AimAssistD extends Check {

    public AimAssistD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && isExempt(ExemptType.COMBAT)) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

            final float lastDeltaYaw = data.getRotationProcessor().getLastDeltaYaw();
            final float lastDeltaPitch = data.getRotationProcessor().getLastDeltaPitch();

            final double divisorYaw = MathUtil.getGcd((long) (deltaYaw * MathUtil.EXPANDER), (long) (lastDeltaYaw * MathUtil.EXPANDER));
            final double divisorPitch = MathUtil.getGcd((long) (deltaPitch * MathUtil.EXPANDER), (long) (lastDeltaPitch * MathUtil.EXPANDER));

            final double constantYaw = divisorYaw / MathUtil.EXPANDER;
            final double constantPitch = divisorPitch / MathUtil.EXPANDER;

            final double currentX = deltaYaw / constantYaw;
            final double currentY = deltaPitch / constantPitch;

            final double previousX = lastDeltaYaw / constantYaw;
            final double previousY = lastDeltaPitch / constantPitch;

            if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 20.f && deltaPitch < 20.f) {
                final double moduloX = currentX % previousX;
                final double moduloY = currentY % previousY;

                final double floorModuloX = Math.abs(Math.floor(moduloX) - moduloX);
                final double floorModuloY = Math.abs(Math.floor(moduloY) - moduloY);

                final boolean invalidX = moduloX > 90.d && floorModuloX > 0.1;
                final boolean invalidY = moduloY > 90.d && floorModuloY > 0.1;

                if (data.getRotationProcessor().isCinematic()) return;

                if (invalidX && invalidY) {
                    if (buffer++ > 6) {
                        fail("dY: "+ deltaYaw + ", dP: " + deltaPitch + " moduloX: " + moduloX + ", moduloY: " + moduloY + ", b: " + buffer);
                    }
                } else {
                    buffer = Math.max(buffer - 0.25, 0);
                }
            }
        }
    }
}
