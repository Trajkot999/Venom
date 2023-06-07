package dev.venom.check.impl.movement.jesus;

import dev.venom.check.Category;
import dev.venom.util.MathUtil;
import dev.venom.util.PlayerUtil;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.Material;
import org.bukkit.block.Block;
import java.util.List;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;

/*
  This class may contain Tecnio, GladUrBad code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/GladUrBad/Medusa/tree/f00848c2576e4812283e6dc2dc05e29e2ced866a
*/
@CheckInfo(name = "Jesus (A)", category = Category.MOVEMENT)
public class JesusA extends Check {
    public JesusA(PlayerData data) {
        super(data);
    }

    private double lastAccel;

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final List<Block> blocks = data.getPositionProcessor().getBlocks();

            final boolean touchingInvalidMaterial = blocks.stream().anyMatch(block -> block.getType() == Material.WATER_LILY || block.getType() == Material.CARPET || block.getType() == Material.SNOW);
            final boolean touchingWater = data.getPositionProcessor().isInLiquid();
            final boolean nearGround = data.getPositionProcessor().isOnSolidGround();

            final boolean exempt = isExempt(ExemptType.VELOCITY, ExemptType.BOAT, ExemptType.VEHICLE, ExemptType.FLYING, ExemptType.DEPTH_STRIDER)
                    || touchingInvalidMaterial
                    || nearGround;

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double accel = Math.abs(deltaY - lastDeltaY);
            final double lastAccel = this.lastAccel;

            this.lastAccel = accel;

            final double diff = Math.abs(accel - lastAccel);

            final ClientVersion clientVersion = PlayerUtil.getClientVersion(data.getPlayer());

            final boolean invalidAccel = diff == 0
                    && clientVersion.isOlderThanOrEquals(ClientVersion.v_1_12_2)
                    && Math.abs(deltaY) <= 0.05;

            final double deltaMax = clientVersion.isNewerThanOrEquals(ClientVersion.v_1_13) ? 0.45 : 0.101;

            final boolean invalidDelta = deltaY == 0 || MathUtil.isScientificNotation(deltaY) || deltaY > deltaMax;

            if (!exempt && touchingWater && (invalidAccel || invalidDelta)) {
                if (++buffer > 15) {
                    fail("deltaY: " + deltaY + ", diff: " + diff + ", invalidAccel: " + invalidAccel + ", invalidDelta: " + invalidDelta);
                }
            } else {
                buffer = Math.max(buffer - 0.5, 0);
            }
        }
    }
}