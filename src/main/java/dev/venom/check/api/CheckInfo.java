package dev.venom.check.api;

import dev.venom.check.Category;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/*
  This class may contain Tecnio, GladUrBad code under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/GladUrBad/Medusa/tree/f00848c2576e4812283e6dc2dc05e29e2ced866a
*/
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckInfo {
    String name();
    Category category();
    boolean experimental() default false;
}