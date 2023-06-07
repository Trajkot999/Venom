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
@CheckInfo(name = "Move (D)", category = Category.MOVEMENT)
public final class MoveD extends Check {
    public MoveD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final double modifierJump = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1;
            final double modifierVelocity = isExempt(ExemptType.VELOCITY) ? data.getVelocityProcessor().getVelocityY() + 0.5 : 0.0;

            final double maximum = 0.6 + modifierJump + modifierVelocity;

            final boolean exempt = isExempt(ExemptType.PISTON, ExemptType.LIQUID, ExemptType.FLYING, ExemptType.WEB,
                    ExemptType.TP, ExemptType.RESPAWN, ExemptType.SLIME, ExemptType.CHUNK, ExemptType.BOAT, ExemptType.VEHICLE);
            final boolean invalid = deltaY > maximum;

            if (invalid && !exempt) fail();
        }
    }
}