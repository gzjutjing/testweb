package com.test.utils.levenshtein;

/**
 * Created by admin on 2016/9/18.
 */
public class DistancePair {
    private int distance;

    public DistancePair(int distance) {
        this.distance = distance;
    }

    public DistancePair(int distance, DistancePair pair) {
        this.distance = distance;
        if (pair != null) {
            this.distance = Math.min(distance, pair.distance);
        }
    }
}
