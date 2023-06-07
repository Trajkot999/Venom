package dev.venom.exempt;

import dev.venom.Venom;
import dev.venom.data.PlayerData;
import dev.venom.util.PlayerUtil;
import dev.venom.util.ServerUtil;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.NumberConversions;
import java.util.function.Function;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@Getter
public enum ExemptType {

    TP(data -> data.getPositionProcessor().isTeleported()),

    TP_DELAY_SMALL(data -> data.getPositionProcessor().getSinceTeleportTicks() < 3),

    TP_DELAY(data -> data.getPositionProcessor().getSinceTeleportTicks() < 5),

    VELOCITY(data -> data.getVelocityProcessor().isTakingVelocity()),

    VELOCITY_ON_TICK(data -> data.getVelocityProcessor().getTicksSinceVelocity() == 1),

    CHUNK(data -> !data.getPlayer().getWorld().isChunkLoaded(
            NumberConversions.floor(data.getPositionProcessor().getX()) >> 4,
            NumberConversions.floor(data.getPositionProcessor().getZ()) >> 4)
    ),

    JOINED(data -> System.currentTimeMillis() - data.getJoinTime() < 1000L),

    GHOST_BLOCK(data -> data.getGhostBlockProcessor().isOnGhostBlock()),

    GHOST_BLOCK_TICKS(data -> data.getGhostBlockProcessor().getSinceSetbackTicks() < 12),

    GHOST_BLOCK_TICKS_SMALL(data -> data.getGhostBlockProcessor().getSinceSetbackTicks() < 5),

    STEPPED(data -> data.getPositionProcessor().isClientGround() && data.getPositionProcessor().getDeltaY() > 0),

    CINEMATIC(data -> data.getRotationProcessor().isCinematic()),

    SLIME(data -> data.getPositionProcessor().getSinceSlimeTicks() < 20),

    SLIME_ON_TICK(data -> data.getPositionProcessor().getSinceSlimeTicks() < 2),

    ICE(data -> data.getPositionProcessor().getSinceIceTicks() < 40),

    STAIRS(data -> data.getPositionProcessor().isNearStairs()),

    SLAB(data -> data.getPositionProcessor().isNearSlab()),

    TRAPDOOR(data -> data.getPositionProcessor().isNearTrapdoor()),

    WEB(data -> data.getPositionProcessor().isInWeb() || data.getPositionProcessor().isNearWeb()),

    CLIMBABLE(data -> data.getPositionProcessor().isOnClimbable()),

    DIGGING(data -> Venom.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastDiggingTick() < 10),

    BLOCK_BREAK(data -> Venom.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastBreakTick() < 10),

    PLACING(data -> Venom.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastPlaceTick() < 10),

    RESPAWN(data -> data.getActionProcessor().isRespawning() || data.getActionProcessor().getSinceRespawnTicks() < 10),

    ELYTRA(data -> data.getActionProcessor().isElytra()),

    VEHICLE(data -> data.getPositionProcessor().isInVehicle() || data.getPositionProcessor().getSinceVehicleTicks() < 20),

    BOAT(data -> data.getPositionProcessor().isNearVehicle()),

    LIQUID(data -> data.getPositionProcessor().isInLiquid()),

    UNDER_BLOCK(data -> data.getPositionProcessor().isBlockNearHead()),

    PISTON(data -> data.getPositionProcessor().isNearPiston()),

    VOID(data -> data.getPlayer().getLocation().getY() < 4),

    COMBAT(data -> data.getCombatProcessor().getHitTicks() < 5),

    FLYING(data -> data.getPositionProcessor().isFlying() || data.getPositionProcessor().getSinceFlyingTicks() < 40),

    AUTO_CLICKER(data -> data.getExemptProcessor().isExempt(ExemptType.PLACING, ExemptType.DIGGING, ExemptType.BLOCK_BREAK)),

    DEPTH_STRIDER(data -> PlayerUtil.getDepthStriderLevel(data.getPlayer()) > 0),

    SPECTATOR(data -> data.getPlayer().getGameMode() == GameMode.SPECTATOR),

    CREATIVE(data -> data.getPlayer().getGameMode() == GameMode.CREATIVE),

    JUMP(data -> {
        final boolean onGround = data.getPositionProcessor().isClientGround();
        final boolean lastOnGround = data.getPositionProcessor().isLastClientGround();

        final double deltaY = data.getPositionProcessor().getDeltaY();
        final double lastY = data.getPositionProcessor().getLastY();

        final boolean deltaModulo = deltaY % 0.015625 == 0.0;
        final boolean lastGround = lastY % 0.015625 == 0.0;

        final boolean step = deltaModulo && lastGround;

        final double modifierJump = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F;
        final double expectedJumpMotion = 0.41999998688697815D + modifierJump;

        return Math.abs(expectedJumpMotion - deltaY) < 1E-5 && !onGround && lastOnGround && !step;
    }),

    NEAR_WALL(data -> data.getPositionProcessor().isNearWall()),

    LAGGING(data -> {
        final long delta = data.getFlying() - data.getLastFlying();

        return delta > 100 || delta < 2 || data.getActionProcessor().isLagging();
    }),

    PING(data -> PlayerUtil.getPing(data.getPlayer()) > 485),

    SINCE_SPEED(data -> data.getPositionProcessor().getSinceSpeedTicks() < 10 && data.getPositionProcessor().getSinceSpeedTicks() != 0),

    TPS(data -> ServerUtil.getTPS() < 18.0D);

    private final Function<PlayerData, Boolean> exception;

    ExemptType(final Function<PlayerData, Boolean> exception) {
        this.exception = exception;
    }
}