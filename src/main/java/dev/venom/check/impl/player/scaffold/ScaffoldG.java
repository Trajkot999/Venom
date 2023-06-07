package dev.venom.check.impl.player.scaffold;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.AABB;
import dev.venom.util.MathUtil;
import dev.venom.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import io.github.retrooper.packetevents.utils.player.Direction;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/*
  This class may contain Islandscout code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/HawkAnticheat/Hawk
*/
@CheckInfo(name = "Scaffold (G)", category = Category.PLAYER)
public final class ScaffoldG extends Check {

    public ScaffoldG(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            if (PlayerUtil.getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_9) || isExempt(ExemptType.BOAT)) return;

            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());
            if (wrapper.getBlockPosition().getX() == 1 && wrapper.getBlockPosition().getY() == 1 && wrapper.getBlockPosition().getZ() == 1 || wrapper.getDirection().equals(Direction.OTHER)) return;
            Location bLoc = new Location(data.getPlayer().getWorld(), wrapper.getBlockPosition().getX(), wrapper.getBlockPosition().getY(), wrapper.getBlockPosition().getZ());
            Vector pos = getHeadPosition();

            Vector dir = MathUtil.getDirection(data.getRotationProcessor().getYaw(), data.getRotationProcessor().getPitch());
            Vector extraDir = MathUtil.getDirection(data.getRotationProcessor().getYaw() + data.getRotationProcessor().getDeltaYaw(), data.getRotationProcessor().getPitch() + data.getRotationProcessor().getDeltaPitch());

            Vector min = bLoc.toVector();
            Vector max = bLoc.toVector().add(new Vector(1, 1, 1));
            AABB targetAABB = new AABB(min, max);
            targetAABB.expand(0.08, 0.08, 0.08);

            if (targetAABB.betweenRays(pos, dir, extraDir)) {
                buffer = buffer > 0 ? buffer-1 : 0;
            } else {
                if(buffer++ > 2) {
                    fail("direction=" + wrapper.getDirection());
                }
            }
        }
    }
    public Vector getHeadPosition() {
        Vector add = new Vector(0, 0, 0);
        add.setY(data.getActionProcessor().isSneaking() ? 1.54 : 1.62);
        return data.getPlayer().getLocation().toVector().clone().add(add);
    }
}