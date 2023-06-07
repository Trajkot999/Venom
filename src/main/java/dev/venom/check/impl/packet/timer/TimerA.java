package dev.venom.check.impl.packet.timer;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.MovingStats;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Timer (A)", category = Category.PACKET)
public final class TimerA extends Check {

    private final MovingStats movingStats = new MovingStats(20);

    private long lastFlying = 0L;
    private long allowance = 0;

    public TimerA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && (System.currentTimeMillis() - data.getJoinTime()) > 4000L) {
            final long now = now();

            final boolean exempt = this.isExempt(ExemptType.TPS, ExemptType.TP, ExemptType.JOINED, ExemptType.VEHICLE, ExemptType.LAGGING, ExemptType.PING);

            handle: {
                if (exempt) break handle;

                final long delay = now - lastFlying;
                if (delay < 10) break handle;

                movingStats.add(delay);

                final double threshold = 7.07;
                final double deviation = movingStats.getStdDev(threshold);

                if (deviation < threshold) {
                    allowance += 50 - delay;

                    if (allowance > Math.ceil(threshold)) {
                        if(buffer++ > 5) fail("a: " + allowance + ", dev: " + deviation);
                    }else buffer = Math.max(buffer - 1, 0);

                } else {
                    buffer = buffer / 2;
                    allowance = 0;
                }
            }

            this.lastFlying = now;
        } else if (packet.isTeleport()) {
            movingStats.add(125L);
        }
    }
}
