package com.jose.randomdecider.controller;

import java.util.List;
import java.util.Random;

/**
 * Created by jose on 2/5/2017.
 */

public class Shuffler {

    private Random mRandomGen;
    private final int MIN_REPS = 25;
    private List<String> mList;

    public Shuffler(List<String> list) {
        mList = list;
        mRandomGen = new Random();
    }

    public int getShuffleNumber() {
        int size = mList.size();
        int plusReps = mRandomGen.nextInt(size);
        int reps = MIN_REPS + plusReps;
        return reps;
    }
}
