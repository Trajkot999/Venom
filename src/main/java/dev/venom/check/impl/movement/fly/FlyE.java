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
@CheckInfo(name = "Fly (E)", category = Category.MOVEMENT)
public final class FlyE extends Check {

    public FlyE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final boolean clientGround = data.getPositionProcessor().isClientGround();
            final boolean serverGround = data.getPositionProcessor().getY() % 0.015625 == 0.0;

            final boolean exempt = isExempt(ExemptType.BOAT, ExemptType.LIQUID, ExemptType.CLIMBABLE,
                    ExemptType.VEHICLE, ExemptType.TP_DELAY, ExemptType.CHUNK, ExemptType.SLIME,
                    ExemptType.FLYING, ExemptType.PISTON, ExemptType.STAIRS);
            final boolean invalid = clientGround != serverGround;

            if (invalid && !exempt) {
                if (increaseBuffer() > 4) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }
}
