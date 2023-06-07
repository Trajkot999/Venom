package dev.venom.check.impl.combat.autoclicker;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.config.ConfigValue;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "AutoClicker (A)", category = Category.COMBAT)
public final class AutoClickerA extends Check {
    public AutoClickerA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation()) {
            final double cps = data.getClickProcessor().getCps();

            final boolean exempt = isExempt(ExemptType.AUTO_CLICKER);
            final boolean invalid = cps > 25 && !Double.isInfinite(cps);

            if (invalid && !exempt) {
                fail("cps: " + cps);
            }
        }
    }
}
