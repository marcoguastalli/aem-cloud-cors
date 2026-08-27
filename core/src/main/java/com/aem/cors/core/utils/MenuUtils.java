package com.aem.cors.core.utils;

import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

public class MenuUtils {

    private static final String FILE_SEPARATOR = "/";
    private static final String FILE_SEPARATOR_REG_EXP = "/";

    private MenuUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** @param currentPagePath the path of the Page that is being rendered
     * @param menuPath the path of the Page that will be rendered in the menu
     * @return true if the paths matches, false instead */
    public static boolean calculateIsActive(final String currentPagePath, final String menuPath) {
        boolean result = false;
        // calculate tokens of the currentPagePath
        final int currentPagePathLevel = new StringTokenizer(currentPagePath, FILE_SEPARATOR, Boolean.FALSE).countTokens();
        StringBuilder stringBuilder = new StringBuilder();
        // StringTokenizer tokens are not so precise as split tokens
        final String[] tokens = currentPagePath.split(FILE_SEPARATOR_REG_EXP);
        for (int i = 0; i < tokens.length; i++) {
            final String token = tokens[i];
            if (i < currentPagePathLevel) {
                stringBuilder.append(token).append(FILE_SEPARATOR);
            } else if (i == currentPagePathLevel) {
                stringBuilder.append(token);
            } else {
                break;
            }
            result = StringUtils.startsWith(stringBuilder.toString(), menuPath);
        }
        return result;
    }
}
