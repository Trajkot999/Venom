package dev.venom.data.processor;

import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.util.EvictingList;
import dev.venom.util.MathUtil;
import lombok.Getter;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@Getter
public final class ClickProcessor {

    private final PlayerData data;
    private long lastSwing = -1;
    private long delay;
    private int movements;
    private double cps, rate;
    private final EvictingList<Integer> clicks = new EvictingList<>(10);

    public ClickProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handleArmAnimation() {
        if (!data.getActionProcessor().isDigging() && !data.getActionProcessor().isPlacing()) {
            if (lastSwing > 0) {
                delay = System.currentTimeMillis() - lastSwing;
            }
            lastSwing = System.currentTimeMillis();
        }

        final boolean exempt = data.getExemptProcessor().isExempt(ExemptType.PLACING, ExemptType.DIGGING);

        click: {
            if (exempt || movements > 5) break click;

            clicks.add(movements);
        }

        if (clicks.size() > 5) {
            final double cps = MathUtil.getCps(clicks);
            final double rate = cps * movements;

            this.cps = cps;
            this.rate = rate;
        }

        movements = 0;
    }

    public void handleFlying() {
        movements++;
    }
}