package dev.venom.check.impl.player.scaffold;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.packet.Packet;
import dev.venom.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import io.github.retrooper.packetevents.utils.player.Direction;
import org.bukkit.Location;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Scaffold (D)", category = Category.PLAYER)
public final class ScaffoldD extends Check {

    public ScaffoldD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {

            if (PlayerUtil.getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_9)) return;

            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());

            final Direction direction = wrapper.getDirection();

            final Location blockLocation = new Location(
                    data.getPlayer().getWorld(),
                    wrapper.getBlockPosition().getX(),
                    wrapper.getBlockPosition().getY(),
                    wrapper.getBlockPosition().getZ()
            );

            final Location eyeLocation = data.getPlayer().getEyeLocation();
            final Location blockAgainstLocation = getBlockAgainst(direction, blockLocation);

            if (!interactedCorrectly(blockAgainstLocation, eyeLocation, direction)) {
                fail("face=" + direction.getFaceValue());
            }
        }
    }

    private Location getBlockAgainst(final Direction direction, final Location blockLocation) {
        if (Direction.UP.equals(direction)) {
            return blockLocation.clone().add(0, -1, 0);
        } else if (Direction.DOWN.equals(direction)) {
            return blockLocation.clone().add(0, 1, 0);
        } else if (Direction.EAST.equals(direction) || Direction.SOUTH.equals(direction)) {
            return blockLocation;
        } else if (Direction.WEST.equals(direction)) {
            return blockLocation.clone().add(1, 0, 0);
        } else if (Direction.NORTH.equals(direction)) {
            return blockLocation.clone().add(0, 0, 1);
        }
        return null;
    }
    private boolean interactedCorrectly(Location block, Location player, Direction face) {
        if (Direction.UP.equals(face)) {
            return player.getY() > block.getY();
        } else if (Direction.DOWN.equals(face)) {
            return player.getY() < block.getY();
        } else if (Direction.WEST.equals(face)) {
            return player.getX() < block.getX();
        } else if (Direction.EAST.equals(face)) {
            return player.getX() > block.getX();
        } else if (Direction.NORTH.equals(face)) {
            return player.getZ() < block.getZ();
        } else if (Direction.SOUTH.equals(face)) {
            return player.getZ() > block.getZ();
        }
        return true;
    }
}