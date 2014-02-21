package edu.yale.library;

import java.util.concurrent.TimeUnit;

/**
 * General utility. Subject to removal.
 */
public class TimeUtils
{
    public static String elapsedMinutes(long start)
    {
        return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start) + " minutes.";
    }

    public static String elapsedMilli(long start)
    {
        return (System.currentTimeMillis() - start) + " ms.";
    }
}
