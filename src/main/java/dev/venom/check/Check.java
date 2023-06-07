package dev.venom.check;

import dev.venom.Venom;
import dev.venom.check.api.CheckInfo;
import dev.venom.check.api.VenomCheck;
import dev.venom.check.api.VenomFlagEvent;
import dev.venom.config.Config;
import dev.venom.data.PlayerData;
import dev.venom.exempt.ExemptType;
import dev.venom.packet.Packet;
import dev.venom.util.AlertUtil;
import dev.venom.util.PunishUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import java.util.Objects;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
/*
  This class may contain Tecnio, GladUrBad code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/GladUrBad/Medusa/tree/f00848c2576e4812283e6dc2dc05e29e2ced866a
*/
@Getter
@Setter
public abstract class Check implements VenomCheck {

    public final PlayerData data;

    private final boolean enabled;
    private final int maxVl;
    private final String punishCommand;

    private int vl;
    private long lastFlagTime;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    public double buffer;

    public String name;
    public Category category;
    public char type;

    public Check(final PlayerData data) {
        this.data = data;

        this.enabled = Config.ENABLED_CHECKS.contains(this.getClass().getSimpleName());
        this.maxVl = Config.MAX_VIOLATIONS.get(this.getClass().getSimpleName());
        this.punishCommand = Config.PUNISH_COMMANDS.get(this.getClass().getSimpleName());

        this.category = this.getCheckInfo().category();

        this.name = this.getCheckInfo().name().split("\\(")[0].replace(" ", "");
        this.type = this.getCheckInfo().name().split("\\(")[1].split("\\)")[0].replaceAll(" ", "").toCharArray()[0];
    }
    public abstract void handle(final Packet packet);

    public void fail(final Object info) {
        final VenomFlagEvent event = new VenomFlagEvent(data.getPlayer(), this, category);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        vl++;

        switch (this.category) {
            case COMBAT:
                data.setCombatViolations(data.getCombatViolations() + 1);
                break;
            case MOVEMENT:
                data.setMovementViolations(data.getMovementViolations() + 1);
                break;
            case PLAYER:
                data.setPlayerViolations(data.getPlayerViolations() + 1);
                break;
            case PACKET:
                data.setPacketViolations(data.getPacketViolations() + 1);
                break;
        }

        data.setTotalViolations(data.getTotalViolations() + 1);

        if (System.currentTimeMillis() - lastFlagTime > Config.ALERT_COOLDOWN && vl >= Config.MIN_VL_TO_ALERT) {
            AlertUtil.handleAlert(this, data, Objects.toString(info));
            this.lastFlagTime = System.currentTimeMillis();
        }

        if (vl > maxVl) {
            PunishUtil.punish(this, data);
        }
    }

    public void fail() {
        fail("No info");
    }

    protected boolean isExempt(final ExemptType exemptType) {
        return data.getExemptProcessor().isExempt(exemptType);
    }

    protected boolean isExempt(final ExemptType...exemptTypes) {
        return data.getExemptProcessor().isExempt(exemptTypes);
    }

    public final int ticks() { return Venom.INSTANCE.getTickManager().getTicks(); }

    public final double increaseBuffer() {
        return buffer = Math.min(10000, buffer + 1);
    }

    public final void increaseBufferBy(final double amount) {
        buffer = Math.min(10000, buffer + amount);
    }

    public final void decreaseBuffer() {
        buffer = Math.max(0, buffer - 1);
    }

    public final void decreaseBufferBy(final double amount) {
        buffer = Math.max(0, buffer - amount);
    }

    public final void resetBuffer() {
        buffer = 0;
    }

    public final void setBuffer(final double amount) {
        buffer = amount;
    }

    public final void multiplyBuffer(final double multiplier) {
        buffer *= multiplier;
    }

    protected long now() {
        return System.currentTimeMillis();
    }

    public CheckInfo getCheckInfo() {
        if (this.getClass().isAnnotationPresent(CheckInfo.class)) {
            return this.getClass().getAnnotation(CheckInfo.class);
        } else {
            System.err.println("CheckInfo annotation hasn't been added to the class " + this.getClass().getSimpleName() + ".");
        }
        return null;
    }

    public boolean isBridging() {
        return data.getPlayer().getLocation().clone().subtract(0, 2, 0).getBlock().getType() == Material.AIR;
    }
}