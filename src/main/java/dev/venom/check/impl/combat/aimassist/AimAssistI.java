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
@CheckInfo(name = "AimAssist (I)", category = Category.COMBAT)
public class AimAssistI extends Check {

    public AimAssistI(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && isExempt(ExemptType.COMBAT, ExemptType.PLACING)) {
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final float lastDeltaPitch = data.getRotationProcessor().getLastDeltaPitch();

            if (deltaPitch > 1.0 && !isExempt(ExemptType.TP_DELAY_SMALL, ExemptType.JOINED)) {
                final long expandedPitch = (long) (deltaPitch * MathUtil.EXPANDER);
                final long lastExpandedPitch = (long) (lastDeltaPitch * MathUtil.EXPANDER);

                final double divisorPitch = MathUtil.getGcd(expandedPitch, lastExpandedPitch);
                final double constantPitch = divisorPitch / MathUtil.EXPANDER;

                final double pitch = data.getRotationProcessor().getPitch();
                final double moduloPitch = Math.abs(pitch % constantPitch);

                if (moduloPitch < 1.2E-5) {
                    if (buffer++ > 2) {
                        fail("moduloPitch: " + moduloPitch + ", b: " + buffer);
                    }
                } else {
                    buffer = Math.max(buffer - 0.05, 0);
                }
            }
        }
    }
}