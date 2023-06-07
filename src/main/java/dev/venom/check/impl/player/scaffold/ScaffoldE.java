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
import org.bukkit.GameMode;
import org.bukkit.util.Vector;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@CheckInfo(name = "Scaffold (E)", category = Category.PLAYER)
public final class ScaffoldE extends Check {

    public ScaffoldE(final PlayerData data) {
        super(data);
    }

    private WrappedPacketInBlockPlace wrapper = null;

    @Override
    public void handle(final Packet packet) {

        if(PlayerUtil.getClientVersion(data.getPlayer()).isOlderThanOrEquals(ClientVersion.v_1_7_10) || PlayerUtil.getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_9)) return;

        if (packet.isBlockPlace()) {
            if (data.getPlayer().getItemInHand().getType().isBlock()) {
                wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());
            }
        } else if (packet.isFlying()) {
            if (wrapper != null) {
                final Vector eyeLocation = data.getPlayer().getEyeLocation().toVector();
                final Vector blockLocation = new Vector(
                        wrapper.getBlockPosition().getX(),
                        wrapper.getBlockPosition().getY(),
                        wrapper.getBlockPosition().getZ()
                );

                final double deltaXZ = Math.abs(data.getPositionProcessor().getDeltaXZ());
                final double deltaY = Math.abs(data.getPositionProcessor().getDeltaY());

                final double maxDistance = data.getPlayer().getGameMode() == GameMode.CREATIVE ? 7.25 : 5.25;
                final double distance = eyeLocation.distance(blockLocation) - 0.7071 - deltaXZ - deltaY;

                final boolean exempt = blockLocation.getX() == -1.0 && blockLocation.getY() == -1.0 && blockLocation.getZ() == -1.0
                        || isExempt(ExemptType.JOINED) || data.getActionProcessor().isLagging();
                final boolean invalid = distance > maxDistance;

                if (invalid && !exempt) {
                    if (buffer++ > 1) {
                        fail(distance);
                    }
                } else {
                    buffer = Math.max(buffer - 0.05, 0);
                }
            }

            wrapper = null;
        }
    }
}