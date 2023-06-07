package dev.venom.exempt;

import dev.venom.data.PlayerData;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.function.Function;
/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@RequiredArgsConstructor
public final class ExemptProcessor {

    private final PlayerData data;

    public boolean isExempt(final ExemptType exemptType) {
        return exemptType.getException().apply(data);
    }

    public boolean isExempt(final ExemptType... exemptTypes) {
        return Arrays.stream(exemptTypes).anyMatch(this::isExempt);
    }

    public boolean isExempt(final Function<PlayerData, Boolean> exception) {
        return exception.apply(data);
    }
}