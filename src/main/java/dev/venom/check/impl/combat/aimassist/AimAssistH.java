package dev.venom.check.impl.combat.aimassist;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import org.bukkit.Bukkit;
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
@CheckInfo(name = "AimAssist (H)", category = Category.COMBAT)
public class AimAssistH extends Check {

    private float lastDeltaYaw, lastLastDeltaYaw;

    public AimAssistH(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation() && !isExempt(ExemptType.TP_DELAY)) {

            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            if (deltaYaw < 5F && lastDeltaYaw > 20F && lastLastDeltaYaw < 5F) {
                final double low = (deltaYaw + lastLastDeltaYaw) / 2;
                final double high = lastDeltaYaw;
                if(buffer++ > 4) {
                    fail(String.format("l: %.2f, h: %.2f", low, high));
                    buffer = buffer/2;
                }
            }else {
                buffer = Math.max(buffer - 1, 0);
            }

            lastLastDeltaYaw = lastDeltaYaw;
            lastDeltaYaw = deltaYaw;
        }
    }
}