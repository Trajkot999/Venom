package dev.venom.check.impl.combat.velocity;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.MathUtil;
import dev.venom.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Velocity (B)", category = Category.COMBAT)
public final class VelocityB extends Check {
    public VelocityB(final PlayerData data) {
        super(data);
    }
    private double kbX, kbZ;
    private float friction = 0.91F;

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {

            final boolean sprinting = data.getActionProcessor().isSprinting();

            final int ticksSinceVelocity = data.getVelocityProcessor().getTicksSinceVelocity();

            if (ticksSinceVelocity == 1) {
                kbX = data.getVelocityProcessor().getVelocityX();
                kbZ = data.getVelocityProcessor().getVelocityZ();
            }

            if (data.getCombatProcessor().getHitTicks() <= 1 && sprinting) {
                kbX *= 0.6D;
                kbZ *= 0.6D;
            }

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double velocityXZ = MathUtil.hypot(kbX, kbZ);

            final double diffH = Math.max((deltaXZ / velocityXZ), (lastDeltaXZ / velocityXZ));
            final double percentage = diffH * 100.0;

            final boolean exempt = isExempt(ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CLIMBABLE,
                    ExemptType.UNDER_BLOCK, ExemptType.NEAR_WALL, ExemptType.TP, ExemptType.FLYING);
            final boolean invalid = percentage < 70.0;

            if (kbX != 0 || kbZ != 0) {
                if (invalid && !exempt) {
                    if (increaseBuffer() > 3) {
                        fail( percentage + "%");
                    }

                    resetState();
                } else {
                    decreaseBuffer();
                }
            }

            kbX *= this.friction;
            kbZ *= this.friction;

            if (Math.abs(kbX) < 0.005 || Math.abs(kbZ) < 0.005) {
                resetState();
            }

            if (ticksSinceVelocity >= 2) {
                resetState();
            }


            final double x = data.getPositionProcessor().getX();
            final double y = data.getPositionProcessor().getY();
            final double z = data.getPositionProcessor().getZ();

            final Location blockLocation = new Location(data.getPlayer().getWorld(), x, Math.floor(y) - 1, z);

            this.friction = (float) (PlayerUtil.getBlockFriction(blockLocation) * 0.91F);
        }
    }

    public void resetState() {
        kbX = 0;
        kbZ = 0;
    }
}