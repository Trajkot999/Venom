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
@CheckInfo(name = "Move (B)", category = Category.MOVEMENT)
public final class MoveB extends Check {
    public MoveB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final boolean exempt = isExempt(ExemptType.JOINED, ExemptType.TP, ExemptType.CHUNK);
            final boolean invalid = deltaY < -3.92;

            if (invalid && !exempt) {
                fail("dY: " + deltaY);
            }
        }
    }
}