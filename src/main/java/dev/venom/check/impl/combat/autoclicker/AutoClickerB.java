package dev.venom.check.impl.combat.autoclicker;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.EvictingList;
import dev.venom.util.MathUtil;
import java.util.ArrayDeque;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "AutoClicker (B)", category = Category.COMBAT)
public final class AutoClickerB extends Check {

    private final EvictingList<Long> tickList = new EvictingList<>(30);
    private double lastDeviation;
    private int tick;

    public AutoClickerB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation()) {
            final boolean exempt = isExempt(ExemptType.AUTO_CLICKER);
            if (!exempt) tickList.add((long) (tick * 50.0));

            if (tickList.isFull()) {
                final double deviation = MathUtil.getStandardDeviation(tickList);
                final double difference = Math.abs(deviation - lastDeviation);

                final boolean invalid = difference < 6;

                if (invalid && !exempt) {
                    if (increaseBuffer() > 5) {
                        fail("deviation=" + deviation + " difference=" + difference);
                    }
                } else {
                    decreaseBuffer();
                }

                lastDeviation = deviation;
            }
        } else if (packet.isFlying()) {
            tick++;
        }
    }
}
