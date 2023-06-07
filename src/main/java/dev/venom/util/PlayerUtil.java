package dev.venom.util;

import dev.venom.Venom;
import dev.venom.data.PlayerData;
import dev.venom.data.processor.PositionProcessor;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.FutureTask;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@UtilityClass
public final class PlayerUtil {

    public ClientVersion getClientVersion(final Player player) {
        return PacketEvents.get().getPlayerUtils().getClientVersion(player);
    }

    public String getPlayerVersion(final Player player) {
        return getClientVersion(player).toString().toLowerCase().replace("_", ".");
    }

    public int getPing(final Player player) {
        return  PacketEvents.get().getPlayerUtils().getPing(player);
    }

    public int getDepthStriderLevel(final Player player) {
        if (player.getInventory().getBoots() != null && !ServerUtil.isLowerThan1_8()) {
            return player.getInventory().getBoots().getEnchantmentLevel(Enchantment.DEPTH_STRIDER);
        }
        return 0;
    }

    public float getBaseSpeed(final Player player, final float base) {
        return base + (getPotionLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    public double getBaseSpeed(final Player player) {
        return 0.362 + (getPotionLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    public double getBaseGroundSpeed(final Player player) {
        return 0.289 + (getPotionLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    public int getPotionLevel(final Player player, final PotionEffectType effect) {
        final int effectId = effect.getId();

        if (!player.hasPotionEffect(effect)) return 0;

        return player.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().getId() == effectId).map(PotionEffect::getAmplifier).findAny().orElse(0) + 1;
    }

    public boolean shouldCheckJesus(final PlayerData data) {
        final boolean onWater = data.getPositionProcessor().isCollidingAtLocation(
                -0.001,
                material -> material.toString().contains("WATER"),
                PositionProcessor.CollisionType.ANY
        );

        final boolean hasEdgeCases = data.getPositionProcessor().isCollidingAtLocation(
                -0.001,
                material -> material == Material.CARPET || material == Material.WATER_LILY || material.isSolid(),
                PositionProcessor.CollisionType.ANY
        );

        return onWater && !hasEdgeCases;
    }

    /**
     * Gets the block in the given distance, If the block is null we immediately break to avoid issues
     *
     * @param player The player
     * @param distance The distance
     * @return The block in the given distance, otherwise null
     */
    public Block getLookingBlock(final Player player, final int distance) {
        final Location loc = player.getEyeLocation();

        final Vector v = loc.getDirection().normalize();

        for (int i = 1; i <= distance; i++) {
            loc.add(v);

            Block b = loc.getBlock();
            /*
            I'd recommend moving fiona's getBlock method in a seperate class
            So we can use it in instances like this.
             */
            if (b == null) break;

            if (b.getType() != Material.AIR) return b;
        }

        return null;
    }

    public List<Entity> getEntitiesWithinRadius(final Location location, final double radius) {
        try {
            final double expander = 16.0D;

            final double x = location.getX();
            final double z = location.getZ();

            final int minX = (int) Math.floor((x - radius) / expander);
            final int maxX = (int) Math.floor((x + radius) / expander);

            final int minZ = (int) Math.floor((z - radius) / expander);
            final int maxZ = (int) Math.floor((z + radius) / expander);

            final World world = location.getWorld();

            final List<Entity> entities = new LinkedList<>();

            for (int xVal = minX; xVal <= maxX; xVal++) {

                for (int zVal = minZ; zVal <= maxZ; zVal++) {

                    if (!world.isChunkLoaded(xVal, zVal)) continue;

                    for (final Entity entity : world.getChunkAt(xVal, zVal).getEntities()) {
                        if (entity == null) break;

                        if (entity.getLocation().distanceSquared(location) > radius * radius) continue;

                        entities.add(entity);
                    }
                }
            }

            return entities;
        } catch (final Throwable ignored) {}

        return null;
    }

    public static int getAmplifier(Player player, PotionEffectType effectType) {
        if(!player.hasPotionEffect(effectType)) {
            return 0;
        }
        for(PotionEffect effect : player.getActivePotionEffects()) {
            if(effect.getType().getName().equals(effectType.getName())) {
                return effect.getAmplifier() + 1;
            }
        }
        return 0;
    }

    public double getBlockFriction(final Location to) {
        try {
            return (Objects.requireNonNull(getBlock(to)).getType()) == Material.PACKED_ICE
                    || Objects.requireNonNull(getBlock(to)).getType() == Material.ICE ? 0.9800000190734863
                    : (Objects.requireNonNull(getBlock(to)).getType()).toString().toLowerCase().contains("slime") ? 0.800000011920929
                    : 0.6000000238418579;
        } catch (final Exception ignored) {
            return 0.6000000238418579;
        }
    }

    public double getBlockFriction(final Block block) {
        return block.getType() == Material.PACKED_ICE || block.getType() == Material.ICE ? 0.9800000190734863 : block.getType().toString().contains("SLIME") ? 0.800000011920929 : 0.6000000238418579;
    }

    public Block getBlock(final Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getWorld().getBlockAt(location);
        } else {
            FutureTask<Block> futureTask = new FutureTask<>(() -> {
                location.getWorld().loadChunk(location.getBlockX() >> 4, location.getBlockZ() >> 4);
                return location.getWorld().getBlockAt(location);
            });
            Bukkit.getScheduler().runTask(Venom.INSTANCE.getPlugin(), futureTask);
            try { return futureTask.get(); } catch (final Exception exception) { exception.printStackTrace(); }
            return null;
        }
    }
}