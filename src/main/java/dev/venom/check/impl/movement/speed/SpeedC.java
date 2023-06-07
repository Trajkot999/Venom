package dev.venom.check.impl.movement.speed;

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
@CheckInfo(name = "Speed (C)", category = Category.MOVEMENT)
public final class SpeedC extends Check {

    public SpeedC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final int groundTicks = data.getPositionProcessor().getClientGroundTicks();
            final int airTicks = data.getPositionProcessor().getClientAirTicks();

            final float modifierJump = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F;
            final float jumpMotion = 0.42F + modifierJump;

            double groundLimit = PlayerUtil.getBaseGroundSpeed(data.getPlayer());
            double airLimit = PlayerUtil.getBaseSpeed(data.getPlayer());

            if (Math.abs(deltaY - jumpMotion) < 1.0E-4
                    && airTicks == 1) {
                groundLimit = getAfterJumpSpeed();
                airLimit = getAfterJumpSpeed();
            }

            if (data.getPositionProcessor().isNearStairs()) {
                airLimit += 0.91F;
                groundLimit += 0.91F;
            }

            if (data.getPositionProcessor().getSinceIceTicks() < 20
                    || data.getPositionProcessor().getSinceSlimeTicks() < 20) {
                airLimit += 0.34F;
                groundLimit += 0.34F;
            }

            if (data.getPositionProcessor().getSinceBlockNearHeadTicks() < 6) {
                airLimit += 0.91F / Math.max(1, data.getPositionProcessor().getSinceBlockNearHeadTicks());
                groundLimit += 0.91F / Math.max(1, data.getPositionProcessor().getSinceBlockNearHeadTicks());
            }

            if (groundTicks < 7) {
                groundLimit += (0.25F / groundTicks);
            }

            if (data.getVelocityProcessor().isTakingVelocity()) {
                groundLimit += data.getVelocityProcessor().getVelocityXZ() + 0.05;
                airLimit += data.getVelocityProcessor().getVelocityXZ() + 0.05;
            }

            // Problematic way of fixing it but good enough.
            if (data.getPositionProcessor().getSinceTeleportTicks() < 15) {
                airLimit += 0.1;
                groundLimit += 0.1;
            }

            if (isExempt(ExemptType.VEHICLE, ExemptType.PISTON, ExemptType.GHOST_BLOCK,
                    ExemptType.FLYING, ExemptType.TP, ExemptType.CHUNK, ExemptType.SINCE_SPEED)) return;

            if (airTicks > 0) {
                if (deltaXZ > airLimit) {
                    if (increaseBuffer() > 3) {
                        fail("In air");
                    }
                } else {
                    decreaseBufferBy(0.15);
                }
            } else {
                if (deltaXZ > groundLimit) {
                    if (increaseBuffer() > 3) {
                        fail("On ground");
                    }
                } else {
                    decreaseBufferBy(0.15);
                }
            }
        }
    }

    private double getAfterJumpSpeed() {
        return 0.62 + 0.033 * (double) (PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED));
    }
}
