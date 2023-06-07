package dev.venom.check.impl.packet.post;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.packet.Packet;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Post (G)", category = Category.PACKET)
public final class PostG extends Check {

    private boolean sent;
    public long lastFlying, lastPacket;

    public PostG(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final long now = System.currentTimeMillis();
            final long delay = now - lastPacket;

            if (sent) {
                if (delay > 40L && delay < 100L) {
                    buffer+= 0.25;

                    if (buffer > 0.75) {
                        fail("b: " + buffer);
                    }
                } else {
                    buffer = Math.max(buffer - 0.025, 0);
                }

                sent = false;
            }

            this.lastFlying = now;
        } else if (packet.isIncomingHeldItemSlot()) {
            final long now = System.currentTimeMillis();
            final long delay = now - lastFlying;

            if (delay < 10L) {
                lastPacket = now;
                sent = true;
            } else {
                buffer = Math.max(buffer - 0.0025, 0);
            }
        }
    }
}
