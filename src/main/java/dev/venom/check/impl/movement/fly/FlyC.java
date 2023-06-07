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
@CheckInfo(name = "Fly (C)", category = Category.MOVEMENT)
public final class FlyC extends Check {

    public FlyC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean onGround = data.getPositionProcessor().isServerGround() || data.getPositionProcessor().isClientGround();

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double difference = Math.abs(deltaY - lastDeltaY);

            final boolean exempt = isExempt(ExemptType.VELOCITY, ExemptType.PISTON, ExemptType.VEHICLE, ExemptType.TP,
                    ExemptType.LIQUID, ExemptType.BOAT, ExemptType.FLYING, ExemptType.WEB, ExemptType.SLIME, ExemptType.VOID,
                    ExemptType.CLIMBABLE, ExemptType.CHUNK) || Math.abs(deltaY) > 3.0 || Math.abs(lastDeltaY) > 3.0;
            final boolean invalid = difference < 0.01 && !onGround;

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