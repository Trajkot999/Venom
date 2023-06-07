package dev.venom.check.impl.combat.autoclicker;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.MathUtil;
import java.util.ArrayDeque;
import java.util.Deque;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "AutoClicker (E)", category = Category.COMBAT)
public final class AutoClickerE extends Check {

    private final Deque<Long> samples = new ArrayDeque<>();
    private int ticks;

    public AutoClickerE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTO_CLICKER)) {
            if (ticks > 50) samples.clear();
            else samples.add(ticks * 50L);

            if (samples.size() == 30) {
                final double kurtosis = MathUtil.getKurtosis(samples);

                final boolean invalid = kurtosis < 30000 || Double.isNaN(kurtosis);

                if (invalid) {
                    if (increaseBuffer() > 1) {
                        fail(kurtosis);
                    }
                } else {
                    resetBuffer();
                }

                samples.clear();
            }

            ticks = 0;
        } else if (packet.isFlying()) {
            ticks++;
        }
    }
}
