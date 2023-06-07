package dev.venom.check.impl.movement.sprint;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Sprint (A)", category = Category.MOVEMENT)
public final class SprintA extends Check {

    public SprintA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean onGround = data.getPositionProcessor().isClientGround();
            final boolean sprinting = data.getActionProcessor().isSprinting();

            final double yaw = data.getRotationProcessor().getYaw();
            final Vector direction = new Vector(-Math.sin(yaw * Math.PI / 180.0F) * (float) 1 * 0.5F, 0, Math.cos(yaw * Math.PI / 180.0F) * (float) 1 * 0.5F);

            final double deltaX = data.getPositionProcessor().getDeltaX();
            final double deltaZ = data.getPositionProcessor().getDeltaZ();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            final Vector move = new Vector(deltaX, 0.0, deltaZ);
            final double delta = move.distanceSquared(direction);

            final boolean exempt = isExempt(ExemptType.VELOCITY, ExemptType.CHUNK, ExemptType.UNDER_BLOCK, ExemptType.ICE, ExemptType.LIQUID);
            final boolean invalid = delta > getLimit() && deltaXZ > 0.1 && sprinting && onGround;

            if (invalid && !exempt) {
                if (buffer++ > 2) {
                    fail("b: " + buffer);
                }
            } else {
                buffer = 0;
            }
        }
    }

    private double getLimit() {
        return data.getPlayer().getWalkSpeed() > 0.2f ? .23 * 1 +
                ((data.getPlayer().getWalkSpeed() / 0.2f) * 0.36) : 0.23 + (PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED) * 0.062f);
    }
}