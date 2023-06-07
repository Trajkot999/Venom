package dev.venom.check.impl.combat.aimassist;

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
/*
  This class may contain Tecnio, GladUrBad code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/GladUrBad/Medusa/tree/f00848c2576e4812283e6dc2dc05e29e2ced866a
*/
@CheckInfo(name = "AimAssist (C)", category = Category.COMBAT)
public final class AimAssistC extends Check {

    public AimAssistC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

            final double yawAccel = data.getRotationProcessor().getJoltYaw();
            final double pitchAccel = data.getRotationProcessor().getJoltPitch();

            final boolean exempt = isExempt(ExemptType.TP, ExemptType.CINEMATIC);
            final boolean invalidYaw = yawAccel < 0.1 && Math.abs(deltaYaw) > 1.5F;
            final boolean invalidPitch = pitchAccel < 0.1 && Math.abs(deltaPitch) > 1.5F;

            final String info = String.format(
                    "dY: %.2f, dP: %.2f, yA: %.2f, pA: %.2f",
                    deltaYaw, deltaPitch, yawAccel, pitchAccel
            );

            //TODO check sens % 150? 200? 125? 8126375???
            if(data.getRotationProcessor().getSensitivity() > 150) return;

            if ((invalidPitch || invalidYaw) && !exempt && (deltaYaw > 0 && deltaPitch > 0)) {
                if (++buffer > 8) {
                    fail(info);
                }
            } else {
                buffer -= buffer > 0 ? 1 : 0;
            }
        }
    }
}
