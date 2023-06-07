package dev.venom.check.impl.combat.hitbox;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.packet.Packet;
import dev.venom.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "HitBox (A)", category = Category.COMBAT)
public final class HitBoxA extends Check {

    private boolean attacked;

    public HitBoxA(final PlayerData data) {
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
            if (data.getTargetLocations().size() < 30) return;

            final int now = data.getPositionProcessor().getTicks();
            final int latencyInTicks = msToTicks(PlayerUtil.getPing(data.getPlayer()));

            final double x = data.getPositionProcessor().getX();
            final double z = data.getPositionProcessor().getZ();

            final Vector origin = new Vector(x, 0.0, z);

            final double size = data.getTargetLocations().stream()
                    .filter(pair -> Math.abs(now - pair.getY() - latencyInTicks) < 3)
                    .mapToDouble(pair -> {
                        final Vector targetLocation = pair.getX().toVector().setY(0.0);

                        final Vector dirToDestination = targetLocation.clone().subtract(origin);
                        final Vector playerDirection = data.getPlayer().getEyeLocation().getDirection().setY(0.0);

                        return dirToDestination.angle(playerDirection);
                    })
                    .min().orElse(-1);

            final boolean exempt = data.getCombatProcessor().getDistance() < 1.8;
            final boolean invalid = size > 0.22;
            if (invalid && !exempt) {
                if (increaseBuffer() > 5) {
                    fail(String.format("size: %.3f", size));
                }
            } else {
                decreaseBuffer();
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