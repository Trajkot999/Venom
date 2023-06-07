package dev.venom.check.impl.movement.jesus;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Jesus (D)", category = Category.MOVEMENT)
public class JesusD extends Check {
    public JesusD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final boolean isFullySubmerged = data.getPositionProcessor().isFullySubmergedInLiquidStat();
            final boolean onGround = data.getPositionProcessor().isClientGround();

            final boolean sprinting = data.getActionProcessor().isSprinting();

            final double deltaX = data.getPositionProcessor().getDeltaX();
            final double deltaZ = data.getPositionProcessor().getDeltaZ();

            final double lastDeltaX = data.getPositionProcessor().getLastDeltaX();
            final double lastDeltaZ = data.getPositionProcessor().getLastDeltaZ();

            final ItemStack boots = data.getPlayer().getInventory().getBoots();

            float f1 = 0.8F;
            float f3;

            if (boots != null) f3 = boots.getEnchantmentLevel(Enchantment.DEPTH_STRIDER);
            else f3 = 0.0F;

            if (f3 > 3.0F) f3 = 3.0F;
            if (!onGround) f3 *= 0.5F;
            if (f3 > 0.0F) f1 += (0.54600006F - f1) * f3 / 3.0F;

            final double predictedX = lastDeltaX * f1 + (sprinting ? 0.0263 : 0.02);
            final double predictedZ = lastDeltaZ * f1 + (sprinting ? 0.0263 : 0.02);

            final double differenceX = deltaX - predictedX;
            final double differenceZ = deltaZ - predictedZ;

            final boolean exempt = isExempt(ExemptType.TP, ExemptType.VEHICLE, ExemptType.FLYING,
                    ExemptType.PISTON, ExemptType.CLIMBABLE, ExemptType.VELOCITY, ExemptType.WEB,
                    ExemptType.SLIME, ExemptType.BOAT, ExemptType.CHUNK);
            final boolean invalid = (differenceX > 0.05 || differenceZ > 0.05) && isFullySubmerged;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail("diffX: " + differenceX + " diffZ: " + differenceZ);
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }
}