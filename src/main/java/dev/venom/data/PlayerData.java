package dev.venom.data;

import dev.venom.check.Check;
import dev.venom.data.processor.*;
import dev.venom.exempt.ExemptProcessor;
import dev.venom.check.CheckManager;
import dev.venom.util.ConcurrentEvictingList;
import dev.venom.util.Pair;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.List;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@Getter
@Setter
public final class PlayerData {

    private final Player player;
    private String clientBrand;
    private int totalViolations, combatViolations, movementViolations, playerViolations, packetViolations;
    private long flying, lastFlying;
    private long joinTime = System.currentTimeMillis();
    private final List<Check> checks = CheckManager.loadChecks(this);
    private final ConcurrentEvictingList<Pair<Location, Integer>> targetLocations = new ConcurrentEvictingList<>(30);
    private final ExemptProcessor exemptProcessor = new ExemptProcessor(this);
    private final CombatProcessor combatProcessor = new CombatProcessor(this);
    private final ActionProcessor actionProcessor = new ActionProcessor(this);
    private final ClickProcessor clickProcessor = new ClickProcessor(this);
    private final GhostBlockProcessor ghostBlockProcessor = new GhostBlockProcessor(this);
    private final PositionProcessor positionProcessor = new PositionProcessor(this);
    private final RotationProcessor rotationProcessor = new RotationProcessor(this);
    private final VelocityProcessor velocityProcessor = new VelocityProcessor(this);

    public PlayerData(final Player player) {
        this.player = player;
    }
}