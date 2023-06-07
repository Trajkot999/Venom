package dev.venom.check.impl.combat.killaura;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.PlayerUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "KillAura (E)", category = Category.COMBAT)
public final class KillAuraE extends Check {

    public KillAuraE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && data.getCombatProcessor().getHitTicks() < 2) {
            final Entity target = data.getCombatProcessor().getTarget();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double baseSpeed = PlayerUtil.getBaseSpeed(data.getPlayer(), 0.22F);
            final boolean sprinting = data.getActionProcessor().isSprinting();

            final double acceleration = Math.abs(deltaXZ - lastDeltaXZ);

            final boolean exempt = !(target instanceof Player);
            final boolean invalid = acceleration < 0.0027 && sprinting && deltaXZ > baseSpeed;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.05);
            }
        }
    }
}
