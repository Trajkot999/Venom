package dev.venom.check.impl.combat.hitbox;

import dev.venom.check.Category;
import dev.venom.check.Check;
import dev.venom.check.api.CheckInfo;
import dev.venom.data.PlayerData;
import dev.venom.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.entity.Player;

/*
  This class may contain DerRedstoner code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/DerRedstoner/CheatGuard
*/
@CheckInfo(name = "HitBox (B)", category = Category.COMBAT)
public final class HitBoxB extends Check {
    public HitBoxB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());
            if((wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.INTERACT || wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.INTERACT_AT)) {
                if(wrapper.getEntity() instanceof Player) {
                    if(wrapper.getTarget().isPresent()) {
                        if(Math.abs(wrapper.getTarget().get().getX()) > 0.4000000059604645 || Math.abs(wrapper.getTarget().get().getY()) > 1.91 || Math.abs(wrapper.getTarget().get().getZ()) > 0.4000000059604645) {
                            fail("x="+wrapper.getTarget().get().getX()+"\ny="+wrapper.getTarget().get().getY()+"\nz="+wrapper.getTarget().get().getZ());
                        }
                    }
                }
            }
        }
    }
}