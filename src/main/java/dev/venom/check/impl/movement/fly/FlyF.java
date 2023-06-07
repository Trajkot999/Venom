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
@CheckInfo(name = "Fly (F)", category = Category.MOVEMENT)
public final class FlyF extends Check {
    private double serverFallDistance;
    public FlyF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final boolean inAir = !data.getPositionProcessor().isServerGround();
            final boolean nearStair = data.getPositionProcessor().isNearStairs();
            final boolean inLiquid = data.getPositionProcessor().isInLiquid();

            if (deltaY < 0.0 && !inAir && !nearStair && !inLiquid) {
                serverFallDistance -= deltaY;
            } else {
                serverFallDistance = 0.0;
            }

            final double serverFallDistance = this.serverFallDistance;
            final double clientFallDistance = data.getPlayer().getFallDistance();

            final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.CREATIVE, ExemptType.WEB, ExemptType.CLIMBABLE,ExemptType.LIQUID, ExemptType.BOAT, ExemptType.VOID, ExemptType.VEHICLE, ExemptType.CHUNK, ExemptType.PISTON);
            final boolean invalid = Math.abs(serverFallDistance - clientFallDistance) - clientFallDistance >= 1.0 && inAir;

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
