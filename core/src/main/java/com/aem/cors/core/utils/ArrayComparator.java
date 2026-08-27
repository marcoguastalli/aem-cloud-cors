package com.aem.cors.core.utils;

import java.util.Arrays;

/** Util class for comparing arrays regardless of element order */
public final class ArrayComparator {

    private ArrayComparator() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** Sorts both input arrays in place, then compares them for equality
     *
     * @param array1 the first array
     * @param array2 the second array
     * @return true if both arrays are non-null and contain the same elements regardless of original order, false instead */
    public static boolean compareSortedArrays(String[] array1, String[] array2) {
        if ((array1 != null) && (array2 != null)) {
            Arrays.sort(array1);
            Arrays.sort(array2);
            return Arrays.equals(array1, array2);
        }
        return false;
    }

}
