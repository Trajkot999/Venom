package dev.venom.config;

import lombok.Getter;
import lombok.Setter;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
public final class ConfigValue {
    @Setter
    private Object value;
    @Getter
    private final ValueType type;
    @Getter
    private final String name;

    public ConfigValue(ValueType type, String name) {
        this.type = type;
        this.name = name;
    }

    public boolean getBoolean() {
        return (boolean) value;
    }

    public double getDouble() {
        return (double) value;
    }

    public int getInt() {
        return (int) value;
    }

    public long getLong() {
        return (long) value;
    }

    public String getString() {
        return (String) value;
    }

    public enum ValueType {
        INTEGER,
        DOUBLE,
        BOOLEAN,
        STRING,
        LONG
    }
}