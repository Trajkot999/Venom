package dev.venom.check.impl.movement.phase;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.data.processor.PositionProcessor;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.BoundingBox;
import org.bukkit.block.Block;
import java.util.List;

/*
  This class may contain Tecnio, GladUrBad code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/GladUrBad/Medusa/tree/f00848c2576e4812283e6dc2dc05e29e2ced866a
*/
@CheckInfo(name = "Phase (A)", category = Category.MOVEMENT)
public class PhaseA extends Check {

    public PhaseA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && !isExempt(ExemptType.JOINED, ExemptType.TP_DELAY, ExemptType.SPECTATOR, ExemptType.PISTON, ExemptType.WEB, ExemptType.CLIMBABLE, ExemptType.PLACING, ExemptType.BLOCK_BREAK)) {
            final PositionProcessor position = data.getPositionProcessor();

            final BoundingBox toBB = new BoundingBox(
                    position.getX() - 0.3, position.getX() + 0.3,
                    position.getY(), position.getY() + 1.705,
                    position.getZ() - 0.3, position.getZ() + 0.3
            );

            final BoundingBox fromBB = new BoundingBox(
                    position.getLastX() - 0.3, position.getLastX() + 0.3,
                    position.getLastY(), position.getLastY() + 0.3,
                    position.getLastZ() - 0.3, position.getLastZ() + 0.3
            );

            final BoundingBox unionBox = toBB.union(fromBB);

            if (unionBox.getSize() > 10) return;

            final List<Block> collidedBlocks = unionBox.getBlocks(data.getPlayer().getWorld());

            final int size = (int) collidedBlocks.stream().filter(block -> block.getType().isSolid()).count();

            if (size >= 4) {
                // FIXME: 06.05.2023 flags in cactus, slab, trapdors, maybe water and way more
                //fail("collided=" + size);
            }
        }
    }
}
