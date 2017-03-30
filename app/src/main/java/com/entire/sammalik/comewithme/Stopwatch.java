package com.entire.sammalik.comewithme;

/**
 * Created by Deepak on 10-02-2017.
 */

public class Stopwatch {
    private static long startTime = 0;
    private static long stopTime = 0;
    private static boolean running = false;


    public static void start() {
        Stopwatch.startTime = System.currentTimeMillis();
        Stopwatch.running = true;
    }


    public static void stop() {
        Stopwatch.stopTime = System.currentTimeMillis();
        Stopwatch.running = false;
    }


    // elaspsed time in milliseconds
    public static long getElapsedTime() {
        if (running) {
            return System.currentTimeMillis() - startTime;
        }
        return stopTime - startTime;
    }


    // elaspsed time in seconds
    public long getElapsedTimeSecs() {
        if (running) {
            return ((System.currentTimeMillis() - startTime) / 1000);
        }
        return ((stopTime - startTime) / 1000);
    }




    // sample usage
    public static void main(String[] args) {
        Stopwatch s = new Stopwatch();
        s.start();
        // code you want to time goes here
        s.stop();
        System.out.println("elapsed time in milliseconds: " + s.getElapsedTime());
    }
}
