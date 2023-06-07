package dev.venom.check.impl.movement.move;

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
@CheckInfo(name = "Move (A)", category = Category.MOVEMENT)
public final class MoveA extends Check {
    public MoveA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean onGround = data.getPositionProcessor().isClientGround();

            final double deltaY = data.getPositionProcessor().getDeltaY();

            final double y = data.getPositionProcessor().getY();
            final double lastY = data.getPositionProcessor().getLastY();

            final boolean step = y % 0.015625 == 0.0 && lastY % 0.015625 == 0.0;

            final double modifierJump = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F;
            final double expectedJumpMotion = 0.42F + modifierJump;

            final boolean exempt = isExempt(ExemptType.VEHICLE, ExemptType.CLIMBABLE, ExemptType.VELOCITY, ExemptType.PISTON,
                    ExemptType.LIQUID, ExemptType.TP, ExemptType.WEB, ExemptType.BOAT, ExemptType.FLYING, ExemptType.SLIME,
                    ExemptType.UNDER_BLOCK, ExemptType.CHUNK) || data.getPositionProcessor().getSinceBlockNearHeadTicks() < 5;
            final boolean invalid = deltaY != expectedJumpMotion && deltaY > 0.0 && !onGround && lastY % 0.015625 == 0.0 && !step;

            if (invalid && !exempt)
                fail("dY: " + deltaY + ", vel: " + isExempt(ExemptType.VELOCITY));
            if (step && deltaY > 0.6F && !exempt)
                fail("dY: " + deltaY);
        }
    }
}