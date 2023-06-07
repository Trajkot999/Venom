package dev.venom.util;

import java.util.Collection;
import java.util.LinkedList;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
public final class EvictingList<T> extends LinkedList<T> {
    private final int maxSize;

    public int getMaxSize() {
        return this.maxSize;
    }

    public EvictingList(int maxSize) {
        this.maxSize = maxSize;
    }

    public EvictingList(Collection<? extends T> c, int maxSize) {
        super(c);
        this.maxSize = maxSize;
    }

    public boolean add(T t) {
        if (size() >= getMaxSize())
            removeFirst();
        return super.add(t);
    }

    public boolean isFull() {
        return (size() >= getMaxSize());
    }
}