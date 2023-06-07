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
@CheckInfo(name = "Move (E)", category = Category.MOVEMENT)
public final class MoveE extends Check {
    public MoveE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = Math.abs(data.getPositionProcessor().getDeltaY());
            final double lastDeltaY = Math.abs(data.getPositionProcessor().getLastDeltaY());

            final double modifierJump = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F;
            final double modifierVelocity = isExempt(ExemptType.VELOCITY) ? Math.abs(data.getVelocityProcessor().getVelocityY()) : 0.0;

            final double limit = 1.0 + modifierJump + modifierVelocity;

            final boolean exempt = isExempt(ExemptType.TP, ExemptType.PISTON,
                    ExemptType.VEHICLE, ExemptType.BOAT, ExemptType.VEHICLE,
                    ExemptType.SLIME, ExemptType.CHUNK, ExemptType.FLYING);
            final boolean invalid = deltaY > limit && lastDeltaY < 0.2;

            if (invalid && !exempt) fail();
        }
    }
}