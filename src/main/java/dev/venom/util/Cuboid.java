package dev.venom.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/*
  This class may contain DerRedstoner code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/DerRedstoner/CheatGuard
*/
public final class Cuboid {

    private double x1;
    private double x2;
    private double y1;
    private double y2;
    private double z1;
    private double z2;

    public Cuboid(Location playerLocation) {
        this(playerLocation.getX(), playerLocation.getY(), playerLocation.getZ());
    }

    public Cuboid(double n, double n2, double n3) {
        this(n, n, n2, n2, n3, n3);
    }

    public Cuboid move(double n, double n2, double n3) {
        this.x1 = this.x1 + n;
        this.x2 = this.x2 + n;
        this.y1 = this.y1 + n2;
        this.y2 = this.y2 + n2;
        this.z1 = this.z1 + n3;
        this.z2 = this.z2 + n3;
        return this;
    }

    public Cuboid expand(double n, double n2, double n3) {
        this.x1 = this.x1 - n;
        this.x2 = this.x2 + n;
        this.y1 = this.y1 - n2;
        this.y2 += n2;
        this.z1 = this.z1 - n3;
        this.z2 = this.z2 + n3;
        return this;
    }

    public List<Block> getBlocks(World world) {
        int n = (int) Math.floor(this.x1);
        int n2 = (int) Math.ceil(this.x2);
        int n3 = (int) Math.floor(this.y1);
        int n4 = (int) Math.ceil(this.y2);
        int n5 = (int) Math.floor(this.z1);
        int n6 = (int) Math.ceil(this.z2);

        ArrayList<Block> list = new ArrayList<>();
        if (!world.isChunkInUse((int) this.x1 >> 4, (int) this.z1 >> 4) || !world.isChunkLoaded((int) this.x1 >> 4, (int) this.z1 >> 4)) {
            return list;
        }

        try {
            list.add(world.getBlockAt(n, n3, n5));

            for (int i = n; i < n2; ++i) {
                for (int j = n3; j < n4; ++j) {
                    for (int k = n5; k < n6; ++k) {
                        list.add(world.getBlockAt(i, j, k));
                    }
                }
            }
        } catch (Exception ignored) {}

        return list;
    }

    public static boolean checkBlocks(Collection<Block> collection, Predicate<Material> predicate) {
        return collection.stream().allMatch(block -> predicate.test(block.getType()));
    }

    public boolean checkBlocks(World world, Predicate<Material> predicate) {
        return checkBlocks(getBlocks(world), predicate);
    }

    public Cuboid(double x1, double x2, double y1, double y2, double z1, double z2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.z1 = z1;
        this.z2 = z2;
    }

    public int count(final World world, Material material) {
        return (int) this.getBlocks(world).stream().filter(mat -> mat.getType() == material).count();
    }
}