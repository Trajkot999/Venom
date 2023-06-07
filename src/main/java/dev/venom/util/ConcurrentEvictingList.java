package dev.venom.util;

import lombok.Getter;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
@Getter
public final class ConcurrentEvictingList<T> extends ConcurrentLinkedDeque<T> {

    private final int maxSize;

    public ConcurrentEvictingList(final int maxSize) {
        this.maxSize = maxSize;
    }

    public ConcurrentEvictingList(final Collection<? extends T> c, final int maxSize) {
        super(c);
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(final T t) {
        if (size() >= getMaxSize()) removeFirst();
        return super.add(t);
    }

    public boolean isFull() {
        return size() >= getMaxSize();
    }
}