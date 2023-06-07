package dev.venom.check.impl.movement.speed;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Speed (A)", category = Category.MOVEMENT)
public final class SpeedA extends Check {

    public SpeedA(final PlayerData data) {
        super(data);
    }

    private double blockSlipperiness = 0.91;
    private double lastHorizontalDistance = 0.0;

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final Player player = data.getPlayer();

            final double deltaY = data.getPositionProcessor().getDeltaY();

            double blockSlipperiness = this.blockSlipperiness;
            double attributeSpeed = 1.d;

            final boolean lastOnGround = data.getPositionProcessor().isLastClientGround();

            final boolean exempt = this.isExempt(ExemptType.TP_DELAY, ExemptType.PISTON, ExemptType.VELOCITY_ON_TICK,
                    ExemptType.FLYING, ExemptType.VEHICLE, ExemptType.CLIMBABLE, ExemptType.LIQUID, ExemptType.CHUNK, ExemptType.GHOST_BLOCK);

            attributeSpeed += PlayerUtil.getPotionLevel(player, PotionEffectType.SPEED) * (float) 0.2 * attributeSpeed;
            attributeSpeed += PlayerUtil.getPotionLevel(player, PotionEffectType.SLOW) * (float) -.15 * attributeSpeed;

            if (lastOnGround) {
                blockSlipperiness *= 0.91f;

                attributeSpeed *= 1.3;
                attributeSpeed *= 0.16277136 / Math.pow(blockSlipperiness, 3);

                if (deltaY > 0.0) {
                    attributeSpeed += 0.2;
                }
            } else {
                attributeSpeed = 0.026f;
                blockSlipperiness = 0.91f;
            }

            final double horizontalDistance = data.getPositionProcessor().getDeltaXZ();
            final double movementSpeed = (horizontalDistance - lastHorizontalDistance) / attributeSpeed;

            if (movementSpeed > 1.0 && !exempt) {
                increaseBufferBy(10);

                if (buffer > 20) {
                    fail("speed: " + movementSpeed + ", horDist: " + horizontalDistance + ", b: " + buffer);
                }
            } else {
                decreaseBufferBy(1);
            }

            final double x = data.getPositionProcessor().getX();
            final double y = data.getPositionProcessor().getY();
            final double z = data.getPositionProcessor().getZ();

            final Location blockLocation = new Location(data.getPlayer().getWorld(), x, Math.floor(y) - 1, z);

            this.blockSlipperiness = PlayerUtil.getBlockFriction(blockLocation);
            this.lastHorizontalDistance = horizontalDistance * blockSlipperiness;
        }
    }
}
