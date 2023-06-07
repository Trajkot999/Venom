package dev.venom.check.impl.movement.fly;

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
@CheckInfo(name = "Fly (H)", category = Category.MOVEMENT)
public final class FlyH extends Check {
    public FlyH(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean onGround = data.getPositionProcessor().isClientGround();
            final boolean inAir = data.getPositionProcessor().getServerAirTicks() > 3;
            final boolean mathGround = data.getPositionProcessor().isMathGround();

            final boolean exempt = isExempt(ExemptType.TP, ExemptType.BOAT, ExemptType.WEB,
                    ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CHUNK, ExemptType.GHOST_BLOCK);
            final boolean invalid = onGround && inAir && !mathGround;

            if (invalid && !exempt) fail();
        }
    }
}
