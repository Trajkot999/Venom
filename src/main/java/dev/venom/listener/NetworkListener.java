package dev.venom.listener;

import dev.venom.Venom;
import dev.venom.data.PlayerData;
import dev.venom.packet.Packet;
import io.github.retrooper.packetevents.event.PacketListenerAbstract;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
public final class NetworkListener extends PacketListenerAbstract {

    @Override
    public void onPacketPlayReceive(final PacketPlayReceiveEvent event) {
        final PlayerData data = Venom.INSTANCE.getPlayerDataManager().getPlayerData(event.getPlayer());

        handle: {
            if (data == null) break handle;

            Venom.INSTANCE.getPacketExecutor().execute(() -> Venom.INSTANCE.getReceivingPacketProcessor()
                    .handle(data, new Packet(Packet.Direction.RECEIVE, event.getNMSPacket(), event.getPacketId(), event.getTimestamp())));
        }
    }

    @Override
    public void onPacketPlaySend(final PacketPlaySendEvent event) {
        final PlayerData data = Venom.INSTANCE.getPlayerDataManager().getPlayerData(event.getPlayer());

        handle: {
            if (data == null) break handle;
            Venom.INSTANCE.getPacketExecutor().execute(() -> Venom.INSTANCE.getSendingPacketProcessor().handle(data, new Packet(Packet.Direction.SEND, event.getNMSPacket(), event.getPacketId(), event.getTimestamp())));
        }
    }
}