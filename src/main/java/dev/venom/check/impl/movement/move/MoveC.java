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
@CheckInfo(name = "Move (C)", category = Category.MOVEMENT)
public final class MoveC extends Check {
    public MoveC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final boolean exempt = isExempt(ExemptType.UNDER_BLOCK, ExemptType.PISTON, ExemptType.SLIME, ExemptType.TP, ExemptType.CHUNK, ExemptType.VEHICLE, ExemptType.BOAT);
            final boolean invalid = deltaY == -lastDeltaY && deltaY != 0.0;

            if (invalid && !exempt) {
                if (increaseBuffer() > 4) {
                    fail("dY: " + deltaY + ", lDY: " + lastDeltaY);
                }
            } else {
                resetBuffer();
            }
        }
    }
}