package dev.venom.data.processor;

import dev.venom.Venom;
import dev.venom.check.api.VenomFlagEvent;
import dev.venom.data.PlayerData;
import dev.venom.util.EvictingList;
import dev.venom.util.MathUtil;
import dev.venom.util.PlayerUtil;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand.WrappedPacketInClientCommand;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.play.in.helditemslot.WrappedPacketInHeldItemSlot;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import io.github.retrooper.packetevents.utils.player.Direction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@Getter
public final class ActionProcessor {

    private final PlayerData data;

    private final EvictingList<Long> flyingSamples = new EvictingList<>(50);

    private boolean sprinting;
    private boolean sneaking;
    private boolean insideVehicle;
    private boolean sendingAction;
    private boolean eating;
    private boolean placing;
    private boolean digging;
    @Getter @Setter
    private boolean elytra = false;
    @Getter @Setter
    private boolean blocking;
    private boolean respawning;
    private boolean sendingDig;
    private boolean lagging;
    private double deviation;

    @Setter private boolean inventory = false;

    private int heldItemSlot, lastHeldItemSlot, lastDiggingTick, lastPlaceTick, lastBreakTick, sprintingTicks, sneakingTicks, sinceRespawnTicks;

    private long lastFlyingTime, lastInventoryOpen, ping;

    public ActionProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handleEntityAction(final WrappedPacketInEntityAction wrapper) {
        sendingAction = true;
        switch (wrapper.getAction()) {
            case START_SPRINTING:
                sprinting = true;
                break;

            case START_FALL_FLYING:
                if (PlayerUtil.getClientVersion(data.getPlayer()).isOlderThanOrEquals(ClientVersion.v_1_9)) return;
                elytra = data.getPlayer().getInventory().getChestplate() != null && data.getPlayer().getInventory().getChestplate().toString().contains("ELYTRA");
                break;
            case STOP_SPRINTING:
                sprinting = false;
                break;
            case START_SNEAKING:
                sneaking = true;
                break;
            case STOP_SNEAKING:
                sneaking = false;
                break;
        }
    }

    public void handleBlockDig(final WrappedPacketInBlockDig wrapper) {
        sendingDig = true;
        switch (wrapper.getDigType()) {
            case START_DESTROY_BLOCK:
                digging = true;
                break;
            case STOP_DESTROY_BLOCK:
            case ABORT_DESTROY_BLOCK:
                digging = false;
                break;
            case RELEASE_USE_ITEM:
                blocking = true;
                eating = false;
                break;
        }
    }

    public void fixBukkitInventoryOpen() { inventory = false; }

    public void handleClientCommand(final WrappedPacketInClientCommand wrapper) {
        switch (wrapper.getClientCommand()) {
            case OPEN_INVENTORY_ACHIEVEMENT:
                inventory = true;
                lastInventoryOpen = System.currentTimeMillis();
                break;
            case PERFORM_RESPAWN:
                respawning = true;
                inventory = false;
                sinceRespawnTicks = 0;
                break;
        }
    }

    public void handleHeldItemSlot(final WrappedPacketInHeldItemSlot wrapper) {
        this.lastHeldItemSlot = this.heldItemSlot;
        this.heldItemSlot = wrapper.getCurrentSelectedSlot();
    }

    public void handleSteerVehicle() { insideVehicle = true; }

    public void handleBlockPlace(WrappedPacketInBlockPlace wrapper) {
        if (data.getPlayer().getItemInHand().getType().isBlock() && !data.getPlayer().getItemInHand().getType().equals(Material.AIR) && PlayerUtil.getLookingBlock(data.getPlayer(), 5) != null && !wrapper.getDirection().equals(Direction.OTHER)) {
            placing = true;
            lastPlaceTick = Venom.INSTANCE.getTickManager().getTicks();
        }
        if (data.getPlayer().getItemInHand().getType().isEdible()) eating = true;
    }

    public void handleCloseWindow() {
        inventory = false;
    }

    public void handleArmAnimation() {
        if (digging && PlayerUtil.getLookingBlock(data.getPlayer(), 5) != null) {
            lastDiggingTick = Venom.INSTANCE.getTickManager().getTicks();
        }
    }

    public void handleInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            lastDiggingTick = Venom.INSTANCE.getTickManager().getTicks();
        }else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (data.getPlayer().getItemInHand().getType().isEdible()) eating = true;
        }
    }

    public void handleBukkitBlockBreak() {
        lastBreakTick = Venom.INSTANCE.getTickManager().getTicks();
    }

    public void handleFlying() {
        blocking = false;
        sendingDig = false;
        sendingAction = false;
        placing = false;
        respawning = false;
        insideVehicle = false;
        if (!data.getPlayer().getItemInHand().getType().isEdible()) eating = false;
        sinceRespawnTicks = !respawning ? sinceRespawnTicks + 1 : 0;
        sprintingTicks = sprinting ? sprintingTicks + 1 : 0;
        sneakingTicks = sneaking ? sneakingTicks + 1 : 0;

        final long delay = System.currentTimeMillis() - lastFlyingTime;

        if (delay > 0) {
            flyingSamples.add(delay);
        }

        if (flyingSamples.isFull()) {
            deviation = MathUtil.getStandardDeviation(flyingSamples);
            lagging = deviation > 200;
        }
        lastFlyingTime = System.currentTimeMillis();
        ping = PacketEvents.get().getPlayerUtils().getPing(data.getPlayer());
    }

    //TODO ADD LAG COMPRESSION + HANDLE UNSTABLE CONNECTION + HANDLE LAG SPIKES AND MORE!
}