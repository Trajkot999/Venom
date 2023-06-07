package dev.venom.check.impl.player.scaffold;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.packet.Packet;
import dev.venom.util.AABB;
import dev.venom.util.MathUtil;
import dev.venom.util.PlayerUtil;
import dev.venom.util.wrappedBlock;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import io.github.retrooper.packetevents.utils.player.Direction;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Objects;

/*
  This class may contain Islandscout code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/HawkAnticheat/Hawk
*/
@CheckInfo(name = "Scaffold (H)", category = Category.PLAYER)
public final class ScaffoldH extends Check {

    public ScaffoldH(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            if (PlayerUtil.getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_9)) return;

            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());
            if (wrapper.getBlockPosition().getX() == 1 && wrapper.getBlockPosition().getY() == 1 && wrapper.getBlockPosition().getZ() == 1 || wrapper.getDirection().equals(Direction.OTHER)) return;
            Location location = new Location(data.getPlayer().getWorld(), wrapper.getBlockPosition().getX(), wrapper.getBlockPosition().getY(), wrapper.getBlockPosition().getZ());
            Block b = PlayerUtil.getBlock(location);
            AABB hitbox;
            if (b != null) {
                hitbox = wrappedBlock.getWrappedBlock(b, PlayerUtil.getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_8)).hitbox;
            } else {
                hitbox = new AABB(new Vector(), new Vector());
            }

            Vector headPos = getHeadPosition();

            if (Objects.requireNonNull(getTargetedBlockFaceNormal(wrapper)).dot(MathUtil.getDirection(data.getRotationProcessor().getYaw(), data.getRotationProcessor().getPitch())) >= 0 &&
                    !hitbox.containsPoint(headPos)) {
                fail();
            }
        }
    }

    public Vector getTargetedBlockFaceNormal(WrappedPacketInBlockPlace wrapper) {
        switch (wrapper.getDirection()) {
            case UP:
                return new Vector(0, 1, 0);
            case DOWN:
                return new Vector(0, -1, 0);
            case SOUTH:
                return new Vector(0, 0, 1);
            case NORTH:
                return new Vector(0, 0, -1);
            case WEST:
                return new Vector(-1, 0, 0);
            case EAST:
                return new Vector(1, 0, 0);
            case INVALID:
        }
        return null;
    }

    public Vector getHeadPosition() {
        Vector add = new Vector(0, 0, 0);
        add.setY(data.getActionProcessor().isSneaking() ? 1.54 : 1.62);
        return data.getPlayer().getLocation().toVector().clone().add(add);
    }
}