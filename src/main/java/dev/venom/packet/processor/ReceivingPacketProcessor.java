package dev.venom.packet.processor;

import dev.venom.data.PlayerData;
import dev.venom.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand.WrappedPacketInClientCommand;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.in.helditemslot.WrappedPacketInHeldItemSlot;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
public final class ReceivingPacketProcessor  {

    public void handle(final PlayerData data, final Packet packet) {
        if (packet.isEntityAction()) {
            final WrappedPacketInEntityAction wrapper = new WrappedPacketInEntityAction(packet.getRawPacket());

            data.getActionProcessor().handleEntityAction(wrapper);
        } else if (packet.isBlockDig()) {
            final WrappedPacketInBlockDig wrapper = new WrappedPacketInBlockDig(packet.getRawPacket());

            data.getActionProcessor().handleBlockDig(wrapper);
        } else if (packet.isClientCommand()) {
            final WrappedPacketInClientCommand wrapper = new WrappedPacketInClientCommand(packet.getRawPacket());
            data.getActionProcessor().handleClientCommand(wrapper);
        } else if (packet.isBlockPlace()) {
            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());
            data.getActionProcessor().handleBlockPlace(wrapper);
        } else if (packet.isIncomingHeldItemSlot()) {
            final WrappedPacketInHeldItemSlot wrapper = new WrappedPacketInHeldItemSlot(packet.getRawPacket());
            data.getActionProcessor().handleHeldItemSlot(wrapper);
        } else if (packet.isSteerVehicle()) {
            data.getActionProcessor().handleSteerVehicle();
        } else if (packet.isCloseWindow()) {
            data.getActionProcessor().handleCloseWindow();
        } else if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());
            data.getCombatProcessor().handleUseEntity(wrapper);
        } else if (packet.isFlying()) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());

            data.setLastFlying(data.getFlying());
            data.setFlying(System.currentTimeMillis());

            data.getActionProcessor().handleFlying();
            data.getVelocityProcessor().handleFlying();
            data.getCombatProcessor().handleFlying();
            data.getGhostBlockProcessor().handleFlying();
            data.getPositionProcessor().handle(wrapper);

            if (wrapper.isLook()) data.getRotationProcessor().handle(wrapper.getYaw(), wrapper.getPitch());

        } else if (packet.isArmAnimation()) {
            data.getClickProcessor().handleArmAnimation();
            data.getActionProcessor().handleArmAnimation();
            data.getCombatProcessor().handleArmAnimation();
        }else if (packet.isIncomingTransaction()) {
            final WrappedPacketInTransaction wrapper = new WrappedPacketInTransaction(packet.getRawPacket());
            data.getVelocityProcessor().handleTransaction(wrapper);
        }

        if (!data.getPlayer().hasPermission("venom.bypass") || data.getPlayer().isOp()) {
            data.getChecks().forEach(check -> check.handle(packet));
        }
    }
}