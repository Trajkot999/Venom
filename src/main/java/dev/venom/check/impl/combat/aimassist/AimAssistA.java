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
@CheckInfo(name = "AimAssist (A)", category = Category.COMBAT)
public final class AimAssistA extends Check {

    private final Predicate<Float> validRotation = rotation -> rotation > 3F && rotation < 35F;

    public AimAssistA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float deltaPitch = Math.abs(data.getRotationProcessor().getDeltaPitch());
            final float deltaYaw =  Math.abs(data.getRotationProcessor().getDeltaYaw() % 360F);

            final float pitch = Math.abs(data.getRotationProcessor().getPitch());


            //validRotation.test(deltaYaw);?
            //validRotation.test(deltaPitch);?
            final boolean invalidPitch = deltaPitch < 0.009 && validRotation.test(deltaYaw);
            final boolean invalidYaw = deltaYaw < 0.009 && validRotation.test(deltaPitch);

            final boolean exempt = isExempt(ExemptType.VEHICLE);
            final boolean invalid = !exempt && (invalidPitch || invalidYaw) && pitch < 89F;

            if (invalid && (deltaYaw > 0 && deltaPitch > 0)) {
                if (++buffer > 20) {
                    fail(String.format("dY: %.2f, dP: %.2f", deltaYaw, deltaPitch));
                }
            } else {
                buffer -= buffer > 0 ? 1 : 0;
            }
        }
    }
}
