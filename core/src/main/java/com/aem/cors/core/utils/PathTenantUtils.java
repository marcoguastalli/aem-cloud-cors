package com.aem.cors.core.utils;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

/** Util Class used to get the tenant of a page */
public class PathTenantUtils {

    private static final String FILE_SEPARATOR = "/";
    private static final Pattern PATTERN_CONTENT_TENANT = Pattern.compile("(/content/)(.*)([/]?)");
    private static final int PATTERN_CONTENT_TENANT_GROUP_INDEX = 2;

    private PathTenantUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** @param path of a page
     * @return a String with the segment or an empty value */
    public static String getTenantFromPath(final String path) {
        final Matcher matcher = PATTERN_CONTENT_TENANT.matcher(path);
        if (matcher.find() && matcher.groupCount() >= PATTERN_CONTENT_TENANT_GROUP_INDEX) {
            final String result = matcher.group(PATTERN_CONTENT_TENANT_GROUP_INDEX);
            if (result.contains(FILE_SEPARATOR)) {
                return substringBeforeSlashes(result);
            }
            return result;
        }
        return EMPTY;
    }

    private static String substringBeforeSlashes(@NotNull final String path) {
        final String result = path.endsWith(FILE_SEPARATOR) ? substringBeforeLast(path, FILE_SEPARATOR)
                : substringBefore(path, FILE_SEPARATOR);
        if (result.contains(FILE_SEPARATOR)) {
            return substringBeforeSlashes(result);
        }
        return result;
    }
}
