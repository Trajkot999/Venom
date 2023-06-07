package dev.venom.check.impl.movement.fastclimb;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "FastClimb (A)", category = Category.MOVEMENT)
public final class FastClimbA extends Check {

    public FastClimbA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && !isExempt(ExemptType.TP, ExemptType.VELOCITY)) {
            final boolean onGround = data.getPositionProcessor().isOnSolidGround();
            final boolean onLadder = data.getPositionProcessor().isOnClimbable();
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double deltaDeltaY = Math.abs(data.getPositionProcessor().getDeltaY() - data.getPositionProcessor().getLastDeltaY());
            final double realDeltaY = 0.41999998688697815D + PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1;
            final boolean invalid = (!onGround && onLadder && deltaDeltaY == 0 && deltaY > 0.1177);
            final boolean invalidDeltaY = deltaY > realDeltaY && onLadder;

            if (invalid) {
                if (++buffer > 2) {
                    fail(String.format("dy=%.2f > 0.1176", deltaY));
                }
            } else {
                buffer = Math.max(buffer - 0.5, 0);
            }

            if (invalidDeltaY) {
                fail(String.format("dy=%.2f > %.2f", deltaY, realDeltaY));
            }
        }
    }
}
