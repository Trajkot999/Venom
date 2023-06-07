package dev.venom.check.impl.player.scaffold;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.potion.PotionEffectType;

/*
  This class may contain Tecnio, GladUrBad code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/GladUrBad/Medusa/tree/f00848c2576e4812283e6dc2dc05e29e2ced866a
*/
@CheckInfo(name = "Scaffold (A)", category = Category.PLAYER)
public final class ScaffoldA extends Check {

    private int movements;
    private int lastX, lastY, lastZ;
    public ScaffoldA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace() && !isExempt(ExemptType.CREATIVE, ExemptType.FLYING)) {
            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());

            if (PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) > 0 || data.getPositionProcessor().getDeltaY() <= 0 || PlayerUtil.getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_9)) return;

            if (!(wrapper.getBlockPosition().getX() == 1 && wrapper.getBlockPosition().getY() == 1 && wrapper.getBlockPosition().getZ() != 1)) {
                if (data.getPlayer().getItemInHand().getType().isBlock()) {
                    if (lastX == wrapper.getBlockPosition().getX() && wrapper.getBlockPosition().getY() > lastY && lastZ == wrapper.getBlockPosition().getZ()) {
                        if (movements < 7) {
                            if (++buffer > 2) {
                                fail("m: " + movements);
                            }
                        } else {
                            buffer = 0;
                        }
                        movements = 0;
                    }
                    lastX = wrapper.getBlockPosition().getX();
                    lastY = wrapper.getBlockPosition().getY();
                    lastZ = wrapper.getBlockPosition().getZ();
                }
            }
        } else if (packet.isFlying()) {
            ++movements;
        }
    }
}
