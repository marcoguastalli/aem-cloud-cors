package com.aem.cors.core.utils;

import java.util.Arrays;

public final class ArrayComparator {

    private ArrayComparator() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    public static boolean compareSortedArrays(String[] array1, String[] array2) {
        if ((array1 != null) && (array2 != null)) {
            Arrays.sort(array1);
            Arrays.sort(array2);
            return Arrays.equals(array1, array2);
        }
        return false;
    }

}
