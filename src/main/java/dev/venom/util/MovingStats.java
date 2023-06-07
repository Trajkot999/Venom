package dev.venom.util;

import java.io.Serializable;

/*
  This class may contain Tecnio code (2020 - 2021) under the GNU license.
  All credits are given to the authors.
  Find more about original anticheat here: https://github.com/Tecnio/AntiHaxerman/tree/master
*/
public final class MovingStats implements Serializable {

    //this array contains all elements we have
    private final double[] elements;

    //this is the current element index
    private int currentElement;
    private int windowCount;

    private double variance;

    public MovingStats(final int size) {
        this.elements = new double[size];
        this.variance = size * 2.5;

        //We need to assign the sum to the entire double array
        for (int i = 0, len = this.elements.length; i < len; i++) {
            this.elements[i] = size * 2.5 / size;
        }
    }

    public void add(double sum) {
        sum /= this.elements.length;

        this.variance -= this.elements[currentElement];
        this.variance += sum;

        //apply the sum to the current element value
        this.elements[currentElement] = sum;

        //change our element index so it doesn't idle
        this.currentElement = (currentElement + 1) % this.elements.length;
    }

    public double getStdDev(final double required) {
        final double stdDev = Math.sqrt(variance);

        //the standard deviation is less than the requirement
        if (stdDev < required) {
            //count it and make sure all match
            if (++windowCount > this.elements.length) {
                return stdDev;
            }
        } else {
            //the stand deviation is greater than required, reset the count
            if (windowCount > 0) {
                windowCount = 0;
            }

            return required;
        }

        //This should never happen
        return Double.NaN;
    }
}