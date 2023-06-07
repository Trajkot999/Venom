package dev.venom.util;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
public final class Pair<X, Y> {
    private X x;

    private Y y;

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Pair))
            return false;
        Pair<?, ?> other = (Pair<?, ?>)o;
        Object this$x = getX(), other$x = other.getX();
        if ((this$x == null) ? (other$x != null) : !this$x.equals(other$x))
            return false;
        Object this$y = getY(), other$y = other.getY();
        return !((this$y == null) ? (other$y != null) : !this$y.equals(other$y));
    }

    public Pair(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public X getX() {
        return this.x;
    }

    public Y getY() {
        return this.y;
    }
}