package dev.venom.check.impl.movement.largemove;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "LargeMove (B)", category = Category.MOVEMENT)
public final class LargeMoveB extends Check {
    public LargeMoveB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = Math.abs(data.getPositionProcessor().getDeltaY());

            final boolean exempt = isExempt(ExemptType.JOINED, ExemptType.TP);
            final boolean invalid = deltaY > 10.0;

            if (invalid && !exempt) {
                fail();
            }
        }
    }
}