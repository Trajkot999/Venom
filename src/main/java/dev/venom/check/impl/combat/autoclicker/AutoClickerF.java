package dev.venom.check.impl.combat.autoclicker;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.MathUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

import java.util.ArrayDeque;
import java.util.Deque;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "AutoClicker (F)", category = Category.COMBAT)
public final class AutoClickerF extends Check {

    private int movements = 0, lastMovements = 0, total = 0, invalid = 0;

    public AutoClickerF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                final boolean proper = data.getClickProcessor().getCps() > 7.2 && movements < 4 && lastMovements < 4;

                if (proper) {
                    final boolean flag = movements == lastMovements;

                    if (flag) {
                        ++invalid;
                    }

                    if (++total == 40) {

                        if (invalid >= 40) {
                            fail();
                        }

                        total = 0;
                    }
                }

                lastMovements = movements;
                movements = 0;
            }
        } else if (packet.isFlying()) {
            movements++;
        }
    }
}
