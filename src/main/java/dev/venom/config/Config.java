package dev.venom.config;

import dev.venom.Venom;
import dev.venom.check.api.CheckInfo;
import dev.venom.check.CheckManager;
import dev.venom.util.ServerUtil;
import org.bukkit.Bukkit;
import java.lang.reflect.Field;
import java.util.*;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
public final class Config {
    public static String ALERT_FORMAT;
    public static long ALERT_COOLDOWN;
    public static int MIN_VL_TO_ALERT;
    public static int CLEAR_VIOLATIONS_DELAY;
    public static List<String> ENABLED_CHECKS = new ArrayList<>();
    public static Map<String, Integer> MAX_VIOLATIONS = new HashMap<>();
    public static Map<String, String> PUNISH_COMMANDS = new HashMap<>();

    public static void updateConfig() {
        try {
            //Violations
            ALERT_FORMAT = getStringFromConfig("violations.alert-format");
            ALERT_COOLDOWN = getLongFromConfig("violations.alert-cooldown");
            MIN_VL_TO_ALERT = getIntegerFromConfig("violations.minimum-vl-to-alert");
            CLEAR_VIOLATIONS_DELAY = getIntegerFromConfig("violations.clear-violations-delay");

            //Checks
            for (Class check : CheckManager.CHECKS) {
                final CheckInfo checkInfo = (CheckInfo) check.getAnnotation(CheckInfo.class);
                String checkType = checkInfo.category().toString().toLowerCase();

                for (Field field : check.getDeclaredFields()) {
                    if (field.getType().equals(ConfigValue.class)) {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        String name = ((ConfigValue) field.get(null)).getName();
                        ConfigValue value = ((ConfigValue) field.get(null));
                        ConfigValue.ValueType type = value.getType();

                        switch (type) {
                            case BOOLEAN:
                                value.setValue(getBooleanFromConfig("checks." + checkType + "." + getPathFromCheckName(checkInfo.name()) + "." + name));
                                break;
                            case INTEGER:
                                value.setValue(getIntegerFromConfig("checks." + checkType + "." + getPathFromCheckName(checkInfo.name()) + "." + name));
                                break;
                            case DOUBLE:
                                value.setValue(getDoubleFromConfig("checks." + checkType + "." + getPathFromCheckName(checkInfo.name()) + "." + name));
                                break;
                            case STRING:
                                value.setValue(getStringFromConfig("checks." + checkType + "." + getPathFromCheckName(checkInfo.name()) + "." + name));
                                break;
                            case LONG:
                                value.setValue(getLongFromConfig("checks." + checkType + "." + getPathFromCheckName(checkInfo.name()) + "." + name));
                                break;
                        }
                        field.setAccessible(accessible);
                    }
                }

                final boolean enabled = getBooleanFromConfig("checks." + checkType + "." + getPathFromCheckName(checkInfo.name()) + ".enabled");
                final int maxViolations = getIntegerFromConfig("checks." + checkType + "." + getPathFromCheckName(checkInfo.name()) + ".max-violations");
                final String punishCommand = getStringFromConfig("violations.punish-command");

                if (enabled) {
                    ENABLED_CHECKS.add(check.getSimpleName());
                }

                MAX_VIOLATIONS.put(check.getSimpleName(), maxViolations);
                PUNISH_COMMANDS.put(check.getSimpleName(), punishCommand);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static boolean getBooleanFromConfig(String string) {
        return Venom.INSTANCE.getPlugin().getConfig().getBoolean(string);
    }

    public static String getStringFromConfig(String string) {
        return Venom.INSTANCE.getPlugin().getConfig().getString(string);
    }

    private static int getIntegerFromConfig(String string) {
        return Venom.INSTANCE.getPlugin().getConfig().getInt(string);
    }

    public static double getDoubleFromConfig(String string) {
        return Venom.INSTANCE.getPlugin().getConfig().getDouble(string);
    }

    private static List<String> getStringListFromConfig(String string) {
        return Venom.INSTANCE.getPlugin().getConfig().getStringList(string);
    }

    private static long getLongFromConfig(String string) {
        return Venom.INSTANCE.getPlugin().getConfig().getLong(string);
    }

    public static String getPathFromCheckName(String name) {
        return name.toLowerCase().replaceFirst("[(]", ".").replaceAll("[ ()]", "");
    }
}