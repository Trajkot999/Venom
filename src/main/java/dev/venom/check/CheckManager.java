package dev.venom.check;

import dev.venom.check.impl.combat.aimassist.*;
import dev.venom.check.impl.combat.autoclicker.*;
import dev.venom.check.impl.combat.hitbox.*;
import dev.venom.check.impl.combat.velocity.*;
import dev.venom.check.impl.combat.killaura.*;
import dev.venom.check.impl.combat.reach.*;
import dev.venom.check.impl.movement.fastclimb.*;
import dev.venom.check.impl.movement.fly.*;
import dev.venom.check.impl.movement.largemove.*;
import dev.venom.check.impl.movement.jesus.*;
import dev.venom.check.impl.movement.move.*;
import dev.venom.check.impl.movement.phase.*;
import dev.venom.check.impl.movement.speed.*;
import dev.venom.check.impl.movement.sprint.*;
import dev.venom.check.impl.movement.step.*;
import dev.venom.check.impl.movement.strafe.*;
import dev.venom.check.impl.packet.badpackets.*;
import dev.venom.check.impl.packet.post.*;
import dev.venom.check.impl.packet.timer.*;
import dev.venom.check.impl.player.inventory.*;
import dev.venom.check.impl.player.scaffold.*;
import dev.venom.config.Config;
import dev.venom.data.PlayerData;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
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
public final class CheckManager {

    public static final Class<?>[] CHECKS = new Class[] {

            //Combat checks
            AimAssistA.class,
            AimAssistB.class,
            AimAssistC.class,
            AimAssistD.class,
            AimAssistE.class,
            AimAssistF.class,
            AimAssistG.class,
            AimAssistH.class,
            AimAssistI.class,
            KillAuraA.class,
            KillAuraB.class,
            KillAuraC.class,
            KillAuraD.class,
            KillAuraE.class,
            KillAuraF.class,
            AutoClickerA.class,
            AutoClickerB.class,
            AutoClickerC.class,
            AutoClickerD.class,
            AutoClickerE.class,
            AutoClickerF.class,
            HitBoxA.class,
            HitBoxB.class,
            VelocityA.class,
            VelocityB.class,
            ReachA.class,

            //Movement checks
            FastClimbA.class,
            FlyA.class,
            FlyB.class,
            FlyC.class,
            FlyD.class,
            FlyE.class,
            FlyF.class,
            FlyG.class,
            FlyH.class,
            JesusA.class,
            JesusB.class,
            JesusC.class,
            JesusD.class,
            LargeMoveA.class,
            LargeMoveB.class,
            MoveA.class,
            MoveB.class,
            MoveC.class,
            MoveD.class,
            MoveE.class,
            PhaseA.class,
            SpeedA.class,
            SpeedB.class,
            SpeedC.class,
            SpeedD.class,
            SpeedE.class,
            SpeedF.class,
            SprintA.class,
            StepA.class,
            StrafeA.class,
            StrafeB.class,

            //Packet checks
            BadPacketsA.class,
            BadPacketsB.class,
            BadPacketsC.class,
            BadPacketsD.class,
            BadPacketsE.class,
            BadPacketsF.class,
            BadPacketsG.class,
            BadPacketsH.class,
            BadPacketsI.class,
            BadPacketsJ.class,
            BadPacketsK.class,
            BadPacketsL.class,
            PostA.class,
            PostB.class,
            PostC.class,
            PostD.class,
            PostE.class,
            PostF.class,
            PostG.class,
            TimerA.class,
            TimerB.class,
            TimerC.class,
            TimerD.class,

            //Player checks
            ScaffoldA.class,
            ScaffoldB.class,
            ScaffoldC.class,
            ScaffoldD.class,
            ScaffoldE.class,
            ScaffoldF.class,
            ScaffoldG.class,
            ScaffoldH.class,
            InventoryA.class,
            InventoryB.class,
    };

    private static final List<Constructor<?>> CONSTRUCTORS = new ArrayList<>();

    public static List<Check> loadChecks(final PlayerData data) {
        final List<Check> checkList = new ArrayList<>();
        for (Constructor<?> constructor : CONSTRUCTORS) {
            try {
                checkList.add((Check) constructor.newInstance(data));
            } catch (Exception exception) {
                System.err.println("Failed to load checks for " + data.getPlayer().getName());
                exception.printStackTrace();
            }
        }
        return checkList;
    }

    public static void setup() {
        for (Class<?> clazz : CHECKS) {
            if (Config.ENABLED_CHECKS.contains(clazz.getSimpleName())) {
                try {
                    CONSTRUCTORS.add(clazz.getConstructor(PlayerData.class));
                } catch (NoSuchMethodException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}