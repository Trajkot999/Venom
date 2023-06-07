package dev.venom.check.impl.player.inventory;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.PlayerUtil;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Inventory (A)", category = Category.PLAYER)
public final class InventoryA extends Check {

    public InventoryA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean onGround = data.getPositionProcessor().isClientGround();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double acceleration = deltaXZ - lastDeltaXZ;

            final boolean exempt = isExempt(ExemptType.WEB, ExemptType.FLYING, ExemptType.PISTON, ExemptType.LIQUID, ExemptType.CLIMBABLE, ExemptType.VELOCITY, ExemptType.CREATIVE, ExemptType.SPECTATOR);

            final boolean invalidDelta = deltaXZ > PlayerUtil.getBaseSpeed(data.getPlayer(), 0.2F) && onGround;
            final boolean invalidAcceleration = acceleration >= 0.0 && deltaXZ > PlayerUtil.getBaseSpeed(data.getPlayer(), 0.1F);

            final boolean invalid = invalidDelta || invalidAcceleration;

            if(data.getActionProcessor().isInventory()) {
                if (invalid && !exempt) {
                    if (buffer++ > 6) {
                        fail("iD: " + invalidDelta + ", iA: " + invalidAcceleration + ", b: " + buffer);
                    }
                } else {
                    buffer = Math.max(buffer - 1, 0);
                }
            }
        }
    }
}
