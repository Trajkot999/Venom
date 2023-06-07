package dev.venom.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@Getter @Setter
@AllArgsConstructor
public final class Velocity {
    private int index;
    private double velocityX, velocityY, velocityZ;
}