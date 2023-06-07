package dev.venom.check.impl.movement.step;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.MathUtil;
import dev.venom.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Step (A)", category = Category.MOVEMENT)
public final class StepA extends Check {

    public StepA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && !isExempt(ExemptType.TP, ExemptType.GHOST_BLOCK, ExemptType.PISTON, ExemptType.SLIME)) {

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastPosY = data.getPositionProcessor().getLastY();

            boolean step = (MathUtil.mathOnGround(deltaY) && MathUtil.mathOnGround(lastPosY));

            if (step && deltaY > 0.6000000238418579D) {
                fail(String.format("%.2f > 0.6", deltaY));
            }
        }
    }
}