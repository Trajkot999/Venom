package dev.venom.check.impl.combat.reach;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.config.ConfigValue;
import dev.venom.data.PlayerData;
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
@CheckInfo(name = "Reach (A)", category = Category.COMBAT)
public final class ReachA extends Check {
    private static final ConfigValue maxReach = new ConfigValue(ConfigValue.ValueType.DOUBLE, "max-reach");
    private boolean attacked;

    public ReachA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if (!attacked) return;
            attacked = false;

            final Entity target = data.getCombatProcessor().getTarget();
            final Entity lastTarget = data.getCombatProcessor().getLastTarget();

            if (target != lastTarget) return;

            if (!(target instanceof Player)) return;
            if (data.getTargetLocations().isFull()) return;

            final int now = data.getPositionProcessor().getTicks();
            final int latencyInTicks = msToTicks(PlayerUtil.getPing(data.getPlayer())) + 2;

            final double x = data.getPositionProcessor().getX();
            final double z = data.getPositionProcessor().getZ();

            final Vector origin = new Vector(x, 0, z);

            final double distance = data.getTargetLocations().stream()
                    .filter(pair -> Math.abs(now - pair.getY() - latencyInTicks) < 5)
                    .mapToDouble(pair -> {
                        final Vector targetLocation = pair.getX().toVector().setY(0);
                        final BoundingBox boundingBox = new BoundingBox(targetLocation);
                        return Math.sqrt(Stream.of(
                                origin.distanceSquared(new Vector(boundingBox.getMaxX(), 0, boundingBox.getMaxZ())),
                                origin.distanceSquared(new Vector(boundingBox.getMinX(), 0, boundingBox.getMinZ())),
                                origin.distanceSquared(new Vector(boundingBox.getMinX(), 0, boundingBox.getMaxZ())),
                                origin.distanceSquared(new Vector(boundingBox.getMaxX(), 0, boundingBox.getMinZ())),
                                origin.distanceSquared(new Vector(boundingBox.getMaxX() - 0.4D, 0, boundingBox.getMaxZ())),
                                origin.distanceSquared(new Vector(boundingBox.getMaxX(), 0, boundingBox.getMaxZ() - 0.4D)),
                                origin.distanceSquared(new Vector(boundingBox.getMinX() + 0.4D, 0, boundingBox.getMinZ())),
                                origin.distanceSquared(new Vector(boundingBox.getMinX(), 0, boundingBox.getMinZ() + 0.4D)),
                                origin.distanceSquared(targetLocation)
                        ).mapToDouble(value -> value).min().orElse(-1));
                    })
                    .min().orElse(-1);

            if (distance > (data.getPlayer().getGameMode() == GameMode.CREATIVE ? 6.1 : maxReach.getDouble())) {
                if (increaseBuffer() > 2) {
                    fail(String.format("dist: %.3f", distance));
                }
            } else {
                decreaseBufferBy(0.08);
            }
        } else if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                attacked = true;
            }
        }
    }

    public int msToTicks(final double time) {
        return (int) Math.round(time / 50);
    }
}