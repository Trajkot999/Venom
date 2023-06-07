package dev.venom.check.impl.combat.killaura;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.entity.Entity;

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
@CheckInfo(name = "KillAura (F)", category = Category.COMBAT)
public final class KillAuraF extends Check {

    public KillAuraF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final int targets = data.getCombatProcessor().getCurrentTargets();

            if (targets > 1) fail("targets: " + targets);

            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                final Entity target = data.getCombatProcessor().getTarget();
                final Entity lastTarget = data.getCombatProcessor().getLastTarget();

                final boolean exempt = target == lastTarget;

                if (!exempt) {
                    if (increaseBuffer() > 1) {
                        fail("b: " + buffer);
                    }
                }
            }
        } else if (packet.isFlying()) {
            resetBuffer();
        }
    }
}
