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
@CheckInfo(name = "Timer (C)", category = Category.PACKET)
public final class TimerC extends Check {
    private final EvictingList<Long> samples = new EvictingList<>(50);
    private long lastFlying;

    public TimerC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && !isExempt(ExemptType.LAGGING, ExemptType.TPS, ExemptType.TP) && (System.currentTimeMillis() - data.getJoinTime()) > 5000L) {
            final long now = now();
            final long delta = now - lastFlying;

            if (delta > 0) {
                samples.add(delta);
            }

            if (samples.isFull()) {
                final double average = MathUtil.getAverage(samples);
                final double speed = 50 / average;

                if (speed >= 1.025) {
                    if (buffer++ > 40) {
                        fail(String.format("speed: %.2f, buffer: %.2f", speed, buffer));
                    }
                } else {
                    buffer = Math.max(0, buffer - 1);
                }
            }

            lastFlying = now;
        } else if (packet.isTeleport()) {
            samples.add(135L);
        }
    }
}
