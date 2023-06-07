package dev.venom.check.impl.combat.killaura;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

/*
  This class may contain Tecnio, GladUrBad code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/GladUrBad/Medusa/tree/f00848c2576e4812283e6dc2dc05e29e2ced866a
*/
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "KillAura (C)", category = Category.COMBAT)
public final class KillAuraC extends Check {

    public KillAuraC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            final boolean invalid = data.getActionProcessor().isPlacing() || data.getActionProcessor().isBlocking();

            final boolean sword = data.getPlayer().getItemInHand().getType().toString().contains("SWORD");

            if (invalid && sword && wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                fail();
            }
        }
    }
}
