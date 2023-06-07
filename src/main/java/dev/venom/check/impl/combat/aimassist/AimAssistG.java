package dev.venom.check.impl.combat.aimassist;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.EvictingList;
import dev.venom.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
/*
  This class may contain Tecnio, GladUrBad code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/GladUrBad/Medusa/tree/f00848c2576e4812283e6dc2dc05e29e2ced866a
*/
@CheckInfo(name = "AimAssist (G)", category = Category.COMBAT)
public class AimAssistG extends Check {

    private final EvictingList<Double> differenceSamples = new EvictingList<>(25);

    public AimAssistG(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && isExempt(ExemptType.COMBAT)) {
            final Player player = data.getPlayer();
            final Entity target = data.getCombatProcessor().getTarget();

            if (target != null) {
                final Location origin = player.getLocation().clone();
                final Vector end = target.getLocation().clone().toVector();

                final float optimalYaw = origin.setDirection(end.subtract(origin.toVector())).getYaw() % 360F;
                final float rotationYaw = data.getRotationProcessor().getYaw();
                final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
                final float fixedRotYaw = (rotationYaw % 360F + 360F) % 360F;

                final double difference = Math.abs(fixedRotYaw - optimalYaw);

                if (deltaYaw > 3f) differenceSamples.add(difference);
                if (differenceSamples.isFull()) {
                    final double average = MathUtil.getAverage(differenceSamples);
                    final double deviation = MathUtil.getStandardDeviation(differenceSamples);

                    final boolean invalid = average < 7 && deviation < 5;

                    if (invalid) {
                        if (++buffer > 15) {
                            fail(String.format("dev: %.2f, avg: %.2f, b: %.2f", deviation, average, buffer));
                        }
                    } else {
                        buffer -= buffer > 0 ? 1 : 0;
                    }
                }
            }
        }
    }
}
