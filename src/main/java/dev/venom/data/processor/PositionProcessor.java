package dev.venom.data.processor;

import dev.venom.data.PlayerData;
import dev.venom.util.BoundingBox;
import dev.venom.util.Cuboid;
import dev.venom.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.out.position.WrappedPacketOutPosition;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import java.util.*;
import java.util.function.Predicate;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@Getter
public final class PositionProcessor {

    private final PlayerData data;
    private double x, y, z,
            lastX, lastY, lastZ,
            deltaX, deltaY, deltaZ, deltaXZ,
            lastDeltaX, lastDeltaZ, lastDeltaY, lastDeltaXZ;
    private boolean flying, inVehicle, inLiquid, inWeb, fullySubmergedInLiquidStat,
            blockNearHead, onClimbable, nearVehicle, onSlime,
            onIce, nearPiston, onSolidGround, nearTrapdoor, nearWeb, nearSlab, nearWall, nearStairs;
    private int sinceVehicleTicks, sinceFlyingTicks, sinceSlimeTicks, sinceWebTicks,
            iceTicks, sinceIceTicks, blockNearHeadTicks, sinceBlockNearHeadTicks,
            sinceNearPistonTicks, sinceSpeedTicks, sinceTeleportTicks, ticks;

    private float fallDistance;

    private boolean teleported;

    private final Deque<Vector> teleportList = new ArrayDeque<>();

    private final List<Block> blocks = new ArrayList<>();
    private final List<Block> blocksBelow = new ArrayList<>();
    private final List<Block> blocksAbove = new ArrayList<>();

    private List<Entity> nearbyEntities = new ArrayList<>();

    private Location lastGroundLocation, lastLocation, location;

    private BoundingBox boundingBox;

    private boolean clientGround, lastClientGround;
    private boolean serverGround, lastServerGround;
    private boolean mathGround, lastMathGround;
    private int serverAirTicks, serverGroundTicks;
    private int clientAirTicks, clientGroundTicks;

    public PositionProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handle(final WrappedPacketInFlying wrapper) {

        teleported = false;

        final boolean position = wrapper.isPosition();
        final boolean look = wrapper.isLook();

        lastX = this.x;
        lastY = this.y;
        lastZ = this.z;
        this.lastClientGround = this.clientGround;

        this.x = position ? wrapper.getX() : this.x;
        this.y = position ? wrapper.getY() : this.y;
        this.z = position ? wrapper.getZ() : this.z;

        this.lastLocation = (this.location != null) ? this.location : null;
        this.location = new Location(this.data.getPlayer().getWorld(), this.x, this.y, this.z);

        this.clientGround = wrapper.isOnGround();

        this.fallDistance = data.getPlayer().getFallDistance();

        handleCollisions();

        lastDeltaX = deltaX;
        lastDeltaY = deltaY;
        lastDeltaZ = deltaZ;
        lastDeltaXZ = deltaXZ;

        deltaX = this.x - lastX;
        deltaY = this.y - lastY;
        deltaZ = this.z - lastZ;
        deltaXZ = Math.hypot(deltaX, deltaZ);

        lastMathGround = mathGround;

        if (position) {
            if (look) {
                for (Vector wantedLocation : teleportList) {
                    if ((wantedLocation.getX() == x
                            && wantedLocation.getY() == y
                            && wantedLocation.getZ() == z)
                            && !wrapper.isOnGround()) {
                        teleported = true;
                        sinceTeleportTicks = 0;
                        teleportList.remove(wantedLocation);
                        break;
                    }
                }
            }
        }

        mathGround = y % 0.015625 == 0.0;

        if(mathGround && clientGround && serverGround) {
            lastGroundLocation = new Location(data.getPlayer().getWorld(), lastX, lastY, lastZ);
        }
    }

    public void handleTicks() {
        ticks++;
        sinceSpeedTicks = data.getPlayer().hasPotionEffect(PotionEffectType.SPEED) ? sinceSpeedTicks + 1 : 0;
        clientGroundTicks = clientGround ? clientGroundTicks + 1 : 0;
        clientAirTicks = !clientGround ? clientAirTicks + 1 : 0;
        serverGroundTicks = serverGround ? serverGroundTicks + 1 : 0;
        serverAirTicks = !serverGround ? serverAirTicks + 1 : 0;
        ++sinceTeleportTicks;
        blockNearHeadTicks = blockNearHead ? blockNearHeadTicks + 1 : 0;
        sinceNearPistonTicks = nearPiston ? 0 : sinceNearPistonTicks + 1;
        sinceBlockNearHeadTicks = blockNearHead ? 0 : sinceBlockNearHeadTicks + 1;
        inVehicle = data.getPlayer().isInsideVehicle();
        sinceVehicleTicks = inVehicle ? 0 : sinceVehicleTicks + 1;
        iceTicks = onIce ? iceTicks + 1 : 0;
        sinceIceTicks = onIce ? 0 : sinceIceTicks + 1;
        flying = data.getPlayer().isFlying();
        sinceFlyingTicks = flying ? 0 : sinceFlyingTicks + 1;
        sinceSlimeTicks = onSlime ? 0 : sinceSlimeTicks + 1;
        sinceWebTicks = inWeb ? 0 : sinceWebTicks + 1;
    }

    public void handleTeleport(final WrappedPacketOutPosition wrapper) {
        final Vector requestedLocation = new Vector(
                wrapper.getPosition().getX(),
                wrapper.getPosition().getY(),
                wrapper.getPosition().getZ()
        );

        teleportList.add(requestedLocation);
    }

    public void handleCollisions() {
        blocks.clear();
        final BoundingBox boundingBox = new BoundingBox(data.getPlayer()).expandSpecific(0, 0, 0.55, 0.6, 0, 0);

        this.boundingBox = boundingBox;

        final double minX = boundingBox.getMinX();
        final double minY = boundingBox.getMinY();
        final double minZ = boundingBox.getMinZ();
        final double maxX = boundingBox.getMaxX();
        final double maxY = boundingBox.getMaxY();
        final double maxZ = boundingBox.getMaxZ();

        for (double x = minX; x <= maxX; x += (maxX - minX)) {
            for (double y = minY; y <= maxY + 0.01; y += (maxY - minY) / 5) {
                for (double z = minZ; z <= maxZ; z += (maxZ - minZ)) {
                    final Location location = new Location(data.getPlayer().getWorld(), x, y, z);
                    final Block block = PlayerUtil.getBlock(location);
                    blocks.add(block);
                }
            }
        }

        lastServerGround = serverGround;

        handleClimbableCollision();
        handleNearbyEntities();

        serverGround = blocks.stream().anyMatch(block -> block.getType() != Material.AIR);
        Cuboid box = new Cuboid(data.getPlayer().getLocation()).expand(0.5, 0, 0.5).move(0, -0.15, 0);
        nearWall = !box.checkBlocks(data.getPlayer().getWorld(), air -> air == Material.AIR);
        nearWeb = box.count(data.getPlayer().getWorld(), Material.WEB) > 0;
        fullySubmergedInLiquidStat = true;
        blocksAbove.clear();
        blocksBelow.clear();
        for (final Block block : blocks) {
            final Material material = block.getType();
            if (block.getLocation().getY() - data.getPositionProcessor().getY() >= 1) blocksAbove.add(block);
            if (block.getLocation().getY() - data.getPositionProcessor().getY() < 0) blocksBelow.add(block);
            if (material != Material.STATIONARY_WATER && material != Material.STATIONARY_LAVA) fullySubmergedInLiquidStat = false;
        }

        inLiquid = blocks.stream().anyMatch(Block::isLiquid);
        onSolidGround = blocks.stream().anyMatch(block -> block.getType().isSolid());
        inWeb = blocks.stream().anyMatch(block -> block.getType() == Material.WEB);
        onIce = blocks.stream().anyMatch(block -> block.getType().toString().contains("ICE"));
        nearSlab = blocks.stream().anyMatch(block -> block.getType().getData() == Step.class);
        nearStairs = blocks.stream().anyMatch(block -> block.getType().getData() == Stairs.class);
        nearTrapdoor = this.isCollidingAtLocation(1.801, material -> material == Material.TRAP_DOOR, CollisionType.ANY);
        blockNearHead = blocks.stream().filter(block -> block.getLocation().getY() - data.getPositionProcessor().getY() > 1.5).anyMatch(block -> block.getType() != Material.AIR) || nearTrapdoor;
        onSlime = blocks.stream().anyMatch(block -> block.getType().toString().equalsIgnoreCase("SLIME_BLOCK"));
        nearPiston = blocks.stream().anyMatch(block -> block.getType().toString().contains("PISTON"));
        handleTicks();
    }

    public void handleClimbableCollision() {
        final Location location = data.getPlayer().getLocation();
        final int var1 = NumberConversions.floor(location.getX());
        final int var2 = NumberConversions.floor(location.getY());
        final int var3 = NumberConversions.floor(location.getZ());
        final Block var4 = PlayerUtil.getBlock(new Location(location.getWorld(), var1, var2, var3));
        assert var4 != null;
        this.onClimbable = var4.getType() == Material.LADDER || var4.getType() == Material.VINE;
    }


    public void handleNearbyEntities() {
        try {
            nearbyEntities = PlayerUtil.getEntitiesWithinRadius(data.getPlayer().getLocation(), 2);

            if (nearbyEntities == null) {
                nearVehicle = false;
                return;
            }

            nearVehicle = false;

            for (final Entity nearbyEntity : nearbyEntities) {
                nearVehicle |= nearbyEntity instanceof Vehicle;
            }
        } catch (final Throwable ignored) {}
    }

    public boolean isCollidingAtLocation(double drop, Predicate<Material> predicate, CollisionType collisionType) {
        final ArrayList<Material> materials = new ArrayList<>();

        for (double x = -0.3; x <= 0.3; x += 0.3) {
            for (double z = -0.3; z <= 0.3; z += 0.3) {
                final Material material = Objects.requireNonNull(PlayerUtil.getBlock(data.getPlayer().getLocation().clone().add(x, drop, z))).getType();
                if (material != null) {
                    materials.add(material);
                }
            }
        }

        return collisionType == CollisionType.ALL ? materials.stream().allMatch(predicate) : materials.stream().anyMatch(predicate);
    }

    public enum CollisionType {
        ANY, ALL
    }
}