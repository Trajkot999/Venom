package dev.venom.check.impl.packet.timer;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.EvictingList;
import dev.venom.util.MathUtil;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Timer (B)", category = Category.PACKET)
public final class TimerB extends Check {
    private final EvictingList<Long> samples = new EvictingList<>(50);
    private long lastFlying;

    public TimerB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && (System.currentTimeMillis() - data.getJoinTime()) > 4000L) {
            final long now = now();

            final boolean exempt = this.isExempt(ExemptType.TPS, ExemptType.TP, ExemptType.JOINED, ExemptType.VEHICLE);

            handle: {
                if (exempt) break handle;

                final long delay = now - lastFlying;

                if (delay > 0) {
                    samples.add(delay);
                }

                if (samples.isFull()) {
                    final double average = MathUtil.getAverage(samples);
                    final double deviation = MathUtil.getStandardDeviation(samples);

                    final double speed = 50.0 / average;

                    final boolean invalid = deviation < 40.0 && speed < 0.6;

                    if (invalid) {
                        if (buffer++ > 30) {
                            fail("dev: " +  deviation + ", buffer: " + buffer);
                            buffer *= 0.5;
                        }
                    } else {
                        buffer = Math.max(0, buffer - 10);
                    }
                }
            }

            this.lastFlying = now;
        } else if (packet.isTeleport()) {
            samples.add(125L);
        }
    }
}
