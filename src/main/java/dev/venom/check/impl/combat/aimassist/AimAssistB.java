package dev.venom.check.impl.combat.aimassist;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;

import java.util.function.Predicate;
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
@CheckInfo(name = "AimAssist (B)", category = Category.COMBAT)
public final class AimAssistB extends Check {

    public AimAssistB(final PlayerData data) {
        super(data);
    }

    private final Predicate<Float> validRotation = rotation -> rotation > 0 && rotation < 60F;

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw() % 360F;
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

            final boolean exempt = isExempt(ExemptType.TP_DELAY);

            final boolean invalid = !exempt && (deltaPitch % 1 == 0 || deltaYaw % 1 == 0) && deltaPitch != 0 && deltaYaw != 0;

            if (invalid && validRotation.test(deltaYaw) && validRotation.test(deltaPitch)) {
                if (++buffer > 4) {
                    fail(String.format("b: %.2f", buffer) + ", pRot: " + validRotation.test(deltaPitch) + ", yRot: " + validRotation.test(deltaYaw));
                }
            } else {
                buffer = Math.max(0, buffer - 0.25);
            }
        }
    }
}
