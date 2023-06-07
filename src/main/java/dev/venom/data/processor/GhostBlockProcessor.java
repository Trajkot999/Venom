package dev.venom.data.processor;

import dev.venom.Venom;
import dev.venom.data.PlayerData;
import dev.venom.util.PlayerUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@Getter
public final class GhostBlockProcessor {

    private final PlayerData data;

    private boolean onGhostBlock;
    private int ghostTicks, sinceSetbackTicks;

    private Location lastGroundLocation;

    public GhostBlockProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handleFlying() {
        final boolean onGhostBlock = data.getPositionProcessor().isClientGround()
                && data.getPositionProcessor().isMathGround()
                && !data.getPositionProcessor().isServerGround()
                && data.getPositionProcessor().getServerAirTicks() > 2;

        final double deltaY = data.getPositionProcessor().getDeltaY();
        final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

        double predictedY = (lastDeltaY - 0.08) * 0.98F;
        if (Math.abs(predictedY) < 0.005) predictedY = 0.0;

        final boolean underGhostBlock = data.getPositionProcessor().getSinceBlockNearHeadTicks() > 3
                && Math.abs(deltaY - ((-0.08) * 0.98F)) < 1E-5
                && Math.abs(deltaY - predictedY) > 1E-5;

        this.onGhostBlock = onGhostBlock || underGhostBlock;

        if (this.onGhostBlock) {
            ++ghostTicks;
        } else {
            ghostTicks = 0;
        }

        //Now it should detect collide jump fly :)
        int ticks = 0;
        final int ping = msToTicks(PlayerUtil.getPing(data.getPlayer()));

        final int fixedPing = Math.min(0 , ping - 1);

        ticks = Math.min(fixedPing, Math.round(400 / 50F));

        if (ghostTicks > ticks && lastGroundLocation != null) {
            Bukkit.getScheduler().runTask(Venom.INSTANCE.getPlugin(), () -> data.getPlayer().teleport(lastGroundLocation));
            sinceSetbackTicks = 0;
        } else {
            //Added sinceSetbackTicks to prevent flagging timer from tp?
            sinceSetbackTicks++;
        }

        if (data.getPositionProcessor().isServerGround() && data.getPositionProcessor().isClientGround()) {
            final Location location = data.getPositionProcessor().getLocation().clone();

            location.setYaw(data.getRotationProcessor().getYaw());
            location.setPitch(data.getRotationProcessor().getPitch());

            lastGroundLocation = location;
        }
    }

    public int msToTicks(final double time) {
        return (int) Math.round(time / 50.);
    }
}