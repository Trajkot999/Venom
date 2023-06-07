package dev.venom.check.api;

import dev.venom.check.Category;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
/*
  This class may contain Tecnio, GladUrBad code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/GladUrBad/Medusa/tree/f00848c2576e4812283e6dc2dc05e29e2ced866a
*/
public final class VenomFlagEvent extends Event implements Cancellable {
    private boolean cancelled;
    private final Player player;
    private final VenomCheck check;
    private final Category category;

    public VenomFlagEvent(Player player, VenomCheck check, Category category) {
        super(true);
        this.player = player;
        this.check = check;
        this.category = category;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public VenomCheck getCheck() {
        return check;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Category getCategory() { return category; }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean b) {
        cancelled = b;
    }
}