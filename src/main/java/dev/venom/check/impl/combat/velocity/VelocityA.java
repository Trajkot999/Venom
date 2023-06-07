package dev.venom.check.impl.combat.velocity;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.config.ConfigValue;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.BoundingBox;
import dev.venom.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.stream.Stream;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Velocity (A)", category = Category.COMBAT)
public final class VelocityA extends Check {
    public VelocityA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final int ticksSinceVelocity = data.getVelocityProcessor().getTicksSinceVelocity();
            if (ticksSinceVelocity != 1) return;

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double expectedDeltaY = data.getVelocityProcessor().getVelocityY();

            final double difference = Math.abs(deltaY - expectedDeltaY);
            final double percentage = (deltaY * 100.0) / expectedDeltaY;

            final boolean exempt = isExempt(ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CLIMBABLE,
                    ExemptType.UNDER_BLOCK, ExemptType.TP, ExemptType.FLYING, ExemptType.JUMP, ExemptType.NEAR_WALL);
            final boolean invalid = difference > 1E-10 && expectedDeltaY > 1E-2;

            if (invalid && !exempt) {
                if (increaseBuffer() > 3) {
                    fail( percentage + "%");
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }
}