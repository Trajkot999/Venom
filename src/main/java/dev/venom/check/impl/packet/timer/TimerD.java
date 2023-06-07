package dev.venom.check.impl.packet.timer;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Timer (D)", category = Category.PACKET)
public class TimerD extends Check {
    private long balance = 0L;
    private long lastFlying = 0L;

    public TimerD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && (System.currentTimeMillis() - data.getJoinTime()) > 5000L) {
            final long now = packet.getTimeStamp();

            handle: {
                if (isExempt(ExemptType.TPS, ExemptType.GHOST_BLOCK_TICKS) || (System.currentTimeMillis() - data.getJoinTime()) < 5000L) break handle;
                if (lastFlying == 0L) break handle;

                final long delay = now - lastFlying;

                balance += 50L - delay;

                if (balance > 5L) {
                    if (buffer++ > 5) {
                        fail("balance: " + balance);
                    }

                    balance = 0;
                } else {
                    buffer = Math.max(0, buffer - 0.001);
                }
            }

            this.lastFlying = now;
        } else if (packet.isTeleport()) {
            if (isExempt(ExemptType.TPS, ExemptType.GHOST_BLOCK_TICKS) || (System.currentTimeMillis() - data.getJoinTime()) < 5000L) return;
            if (lastFlying == 0L) return;

            balance -= 50L;
        }
    }
}
