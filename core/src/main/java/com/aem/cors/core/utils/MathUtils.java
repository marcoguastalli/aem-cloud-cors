package com.aem.cors.core.utils;

import java.util.Random;

public final class MathUtils {

    private MathUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** Generate a Double Random number with min and max range
     *
     * @param minRange the min range
     * @param maxRange the max range
     * @return a random value */
    public static Long randomLongInRange(final long minRange, final long maxRange) {
        return minRange + (long) (Math.random() * (maxRange - minRange));
    }

    /** Generate a Long Random number with min and max range
     *
     * @param minRange the min range
     * @param maxRange the max range
     * @param roundNearest round nearest
     * @return a random value */
    public static Double randomDoubleInRange(final double minRange, final double maxRange, final double roundNearest) {
        double random = minRange + (maxRange - minRange) * new Random().nextDouble();
        return Math.round(random / roundNearest) * roundNearest;
    }

}
