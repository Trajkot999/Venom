package dev.venom.data.processor;

import dev.venom.data.PlayerData;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import lombok.Getter;
import org.bukkit.entity.Entity;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@Getter
public final class CombatProcessor {

    private final PlayerData data;

    private int hitTicks, swings, hits, currentTargets;

    private double hitMissRatio, distance;

    private Entity target, lastTarget;

    private long lastAttack;

    public CombatProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handleUseEntity(final WrappedPacketInUseEntity wrapper) {
        if (wrapper.getAction() != WrappedPacketInUseEntity.EntityUseAction.ATTACK || wrapper.getEntity() == null) {
            return;
        }

        lastAttack = System.currentTimeMillis();

        lastTarget = target == null ? wrapper.getEntity() : target;
        target = wrapper.getEntity();

        distance = data.getPlayer().getLocation().toVector().setY(0).distance(target.getLocation().toVector().setY(0)) - .42;

        ++hits;

        hitTicks = 0;

        if (target != lastTarget) {
            ++currentTargets;
        }
    }

    public void handleArmAnimation() {
        ++swings;
    }

    public void handleFlying() {
        ++hitTicks;
        currentTargets = 0;

        if (swings > 1) {
            hitMissRatio = ((double) hits / (double) swings) * 100;
        }
        if (hits > 100 || swings > 100) {
            hits = swings = 0;
        }
    }
}