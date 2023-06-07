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
@CheckInfo(name = "AimAssist (E)", category = Category.COMBAT)
public final class AimAssistE extends Check {

    public AimAssistE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation() && !data.getRotationProcessor().isCinematic() && isExempt(ExemptType.COMBAT)) {

            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

            final float lastDeltaPitch = data.getRotationProcessor().getLastDeltaPitch();

            final long gcd = MathUtil.getGcd((long) (deltaPitch * MathUtil.EXPANDER), (long) (lastDeltaPitch * MathUtil.EXPANDER));
            final boolean invalid = gcd < 131072L && deltaPitch > 0.5F && deltaPitch < 20.0F;

            if (invalid) {
                if (buffer++ > 5) {
                    fail("gcd: " + gcd + ", dP: " + deltaPitch + ", lDP: " + lastDeltaPitch + ", b: " + buffer);
                }
            } else {
                buffer = Math.max(buffer - 2, 0);
            }
        }
    }
}