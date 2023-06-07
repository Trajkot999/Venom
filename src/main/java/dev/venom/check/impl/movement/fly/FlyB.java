package dev.venom.check.impl.movement.fly;

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
@CheckInfo(name = "Fly (B)", category = Category.MOVEMENT)
public final class FlyB extends Check {

    private boolean lastGroundIsSlime;
    public FlyB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if (data.getPositionProcessor().isClientGround()) {
                lastGroundIsSlime = data.getPositionProcessor().isOnSlime();
            }

            final double deltaY = data.getPositionProcessor().getDeltaY();

            final int airTicksModifier = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP);
            final int airTicksLimit = 8 + airTicksModifier;

            final int clientAirTicks = data.getPositionProcessor().getClientAirTicks();

            final boolean exempt = isExempt(ExemptType.VELOCITY, ExemptType.PISTON, ExemptType.VEHICLE,
                    ExemptType.TP, ExemptType.LIQUID, ExemptType.BOAT, ExemptType.FLYING,
                    ExemptType.WEB, ExemptType.SLIME, ExemptType.CLIMBABLE);
            final boolean invalid = (clientAirTicks > airTicksLimit) && deltaY > 0.0 && !lastGroundIsSlime;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.01);
            }
        }
    }
}
