package dev.venom.check.impl.packet.badpackets;

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
@CheckInfo(name = "BadPackets (L)", category = Category.PACKET)
public final class BadPacketsL extends Check {

    public BadPacketsL(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final int groundTicks = data.getPositionProcessor().getClientGroundTicks();
            final int airTicks = data.getPositionProcessor().getClientAirTicks();

            final boolean exempt = isExempt(ExemptType.SLIME, ExemptType.TP, ExemptType.JOINED) || data.getActionProcessor().isRespawning();
            final boolean invalid = deltaY == 0.0 && groundTicks == 1 && airTicks == 0;

            if (invalid && !exempt) {
                if (buffer++ > 8) {
                    fail("b: " + buffer);
                }
            } else {
                buffer = 0;
            }
        }
    }
}