package dev.venom.util;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/*
  This class may contain Islandscout code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/HawkAnticheat/Hawk
*/
public class wrappedBlock {

    public AABB hitbox;
    AABB[] collisionBoxes;
    boolean is18;

    public static wrappedBlock getWrappedBlock(Block b, boolean is18) {
        return new wrappedBlock(b, is18);
    }

    public wrappedBlock(Block block, boolean is18) {
        BlockPosition.MutableBlockPosition bPos = new BlockPosition.MutableBlockPosition();
        bPos.c(block.getX(), block.getY(), block.getZ());
        IBlockData data;

        while (true) {
            try {
                data = ((CraftWorld) block.getWorld()).getHandle().getType(bPos);
                break;
            } catch (ConcurrentModificationException ignore) {}
        }

        net.minecraft.server.v1_8_R3.Block b = data.getBlock();
        b.updateShape(((CraftWorld) block.getWorld()).getHandle(), bPos);
        this.is18 = is18;
        hitbox = getHitBox(b, block.getLocation());
        collisionBoxes = getCollisionBoxes(b, block.getLocation(), bPos, data);
    }
    public static AABB getHitBox(net.minecraft.server.v1_8_R3.Block b, Location loc) {

        Vector min = new Vector(loc.getX() + b.B(), loc.getY() + b.D(), loc.getZ() + b.F());
        Vector max = new Vector(loc.getX() + b.C(), loc.getY() + b.E(), loc.getZ() + b.G());

        return new AABB(min, max);
    }

    private AABB[] getCollisionBoxes(net.minecraft.server.v1_8_R3.Block b, Location loc, BlockPosition bPos, IBlockData data) {

        if (b instanceof BlockCarpet) {
            AABB[] aabbarr = new AABB[1];
            if (is18) {
                aabbarr[0] = new AABB(loc.toVector(), loc.toVector().add(new Vector(1, 0.0625, 1)));
            } else {
                aabbarr[0] = new AABB(loc.toVector(), loc.toVector().add(new Vector(1, 0, 1)));
            }

            return aabbarr;
        }
        if (b instanceof BlockSnow && data.get(BlockSnow.LAYERS) == 1) {
            AABB[] aabbarr = new AABB[1];
            aabbarr[0] = new AABB(loc.toVector(), loc.toVector().add(new Vector(1, 0, 1)));
            return aabbarr;
        }

        List<AxisAlignedBB> bbs = new ArrayList<>();
        AxisAlignedBB cube = AxisAlignedBB.a(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getBlockX() + 1, loc.getBlockY() + 1, loc.getBlockZ() + 1);
        b.a(((CraftWorld) loc.getWorld()).getHandle(), bPos, data, cube, bbs, null);

        AABB[] collisionBoxes = new AABB[bbs.size()];
        for (int i = 0; i < bbs.size(); i++) {
            AxisAlignedBB bb = bbs.get(i);
            AABB collisionBox = new AABB(new Vector(bb.a, bb.b, bb.c), new Vector(bb.d, bb.e, bb.f));
            collisionBoxes[i] = collisionBox;
        }

        return collisionBoxes;
    }
}
