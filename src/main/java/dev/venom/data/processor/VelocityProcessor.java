package dev.venom.data.processor;

import dev.venom.data.PlayerData;
import dev.venom.util.MathUtil;
import dev.venom.util.Velocity;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.out.transaction.WrappedPacketOutTransaction;
import lombok.Getter;
import org.bukkit.util.Vector;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@Getter
public final class VelocityProcessor {

    private final PlayerData data;

    private double velocityX, velocityY, velocityZ, velocityXZ;
    private double lastVelocityX, lastVelocityY, lastVelocityZ, lastVelocityXZ;

    private int maxVelocityTicks, velocityTicks, ticksSinceVelocity, takingVelocityTicks;
    private short velocityID;

    private final Map<Short, Vector> pendingVelocities = new HashMap<>();
    private final Velocity transactionVelocity = new Velocity(0, 0, 0, 0);

    private int flyingTicks;

    public VelocityProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handle(final double velocityX, final double velocityY, final double velocityZ) {
        lastVelocityX = this.velocityX;
        lastVelocityY = this.velocityY;
        lastVelocityZ = this.velocityZ;
        lastVelocityXZ = this.velocityXZ;

        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.velocityXZ = MathUtil.hypot(velocityX, velocityZ);

        this.velocityID = (short) ThreadLocalRandom.current().nextInt(Short.MAX_VALUE);

        PacketEvents.get().getPlayerUtils().sendPacket(data.getPlayer(),
                new WrappedPacketOutTransaction(0, velocityID, false));
        pendingVelocities.put(velocityID, new Vector(velocityX, velocityY, velocityZ));
    }

    public void handleTransaction(final WrappedPacketInTransaction wrapper) {
        pendingVelocities.computeIfPresent(wrapper.getActionNumber(), (id, vector) -> {
            this.ticksSinceVelocity = 0;

            transactionVelocity.setVelocityX(vector.getX());
            transactionVelocity.setVelocityY(vector.getY());
            transactionVelocity.setVelocityZ(vector.getZ());

            transactionVelocity.setIndex(transactionVelocity.getIndex() + 1);

            this.velocityTicks = flyingTicks;
            this.maxVelocityTicks = (int) (((vector.getX() + vector.getZ()) / 2 + 2) * 15);

            pendingVelocities.remove(wrapper.getActionNumber());

            return vector;
        });
    }

    public void handleFlying() {
        ++ticksSinceVelocity;
        ++flyingTicks;

        if (isTakingVelocity()) {
            ++takingVelocityTicks;
        } else {
            takingVelocityTicks = 0;
        }
    }

    public boolean isTakingVelocity() {
        return Math.abs(flyingTicks - this.velocityTicks) < this.maxVelocityTicks;
    }
}