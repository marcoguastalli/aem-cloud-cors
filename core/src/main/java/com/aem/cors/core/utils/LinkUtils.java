package com.aem.cors.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Original Source Code:
 * <p>
 * https://github.com/adobe/aem-core-wcm-components/blob/main/bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/link/LinkUtil.java
 */
@SuppressWarnings("ALL")
@Slf4j
public class LinkUtils {

    private static final String MAIL_TO_PATTERN = "mailto:";
    private static final String TEL_PATTERN = "tel:";
    private static final String SPACE_ESCAPED = "%20";
    private static final String UNDERSCORE = "_";

    private LinkUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    //RFC 3986 section 2.2 Reserved Characters
    private static final String[] RESERVED_CHARACTERS_ENCODED = {
            "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27", "%28", "%29",
            "%2A", "%2B", "%2C", "%2F", "%3A", "%3B", "%3D", "%3F", "%40", "%5B", "%5D",
            "%2a", "%2b", "%2c", "%2f", "%3a", "%3b", "%3d", "%3f", "%5b", "%5d"
    };
    private final static List<Pattern> PATTERNS = new ArrayList<>();

    static {
        PATTERNS.add(Pattern.compile("(<%[=@].*?%>)"));
        PATTERNS.addAll(Arrays.stream(RESERVED_CHARACTERS_ENCODED)
                .map(encoded -> Pattern.compile("(" + encoded + ")"))
                .collect(Collectors.toList()));
    }

    /**
     * Masks a given {@link String} by replacing all occurrences of {@link LinkUtils#PATTERNS} with a placeholder.
     * The generated placeholders are put into the given {@link Map} and can be used to unmask a {@link String} later on.
     * <p>
     * For example the given original {@link String} {@code /path/to/page.html?r=<%= recipient.id %>} will be transformed to
     * {@code /path/to/page.html?r=_abcd_} and the placeholder with the expression will be put into the given {@link Map}.
     *
     * @param original     the original {@link String}
     * @param placeholders a {@link Map} the generated placeholders will be put in
     * @return the masked {@link String}
     * @see LinkUtils#unmask(String, Map)
     */
    public static String mask(final String original, final Map<String, String> placeholders) {
        if (original == null) {
            return null;
        }
        String masked = original;
        for (Pattern pattern : PATTERNS) {
            Matcher matcher = pattern.matcher(masked);
            while (matcher.find()) {
                String expression = matcher.group(1);
                String placeholder = newPlaceholder(masked);
                masked = masked.replaceFirst(Pattern.quote(expression), placeholder);
                placeholders.put(placeholder, expression);
            }
        }
        return masked;
    }

    /**
     * Unmasks the given {@link String} by replacing the given placeholders with their original value.
     * <p>
     * For example the given masked {@link String} {@code /path/to/page.html?r=_abcd_} will be transformed to
     * {@code /path/to/page.html?r=<%= recipient.id %>} by replacing each of the given {@link Map}s keys with the corresponding value.
     *
     * @param masked       the masked {@link String}
     * @param placeholders the {@link Map} of placeholders to replace
     * @return the unmasked {@link String}
     */
    public static String unmask(final String masked, final Map<String, String> placeholders) {
        if (masked == null) {
            return null;
        }
        String unmasked = masked;
        for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
            unmasked = unmasked.replaceFirst(placeholder.getKey(), placeholder.getValue());
        }
        return unmasked;
    }

    /**
     * Generate a new random placeholder that is not conflicting with any character sequence in the given {@link String}.
     * <p>
     * For example the given {@link String} {@code "foo"} a new random {@link String} will be returned that is not contained in the
     * given {@link String}. In this example the following {@link String}s will never be returned "f", "fo", "foo", "o", "oo".
     *
     * @param str the given {@link String}
     * @return the placeholder name
     */
    static String newPlaceholder(final String str) {
        SecureRandom random = new SecureRandom();
        StringBuilder placeholderBuilder = new StringBuilder(5);

        do {
            placeholderBuilder.setLength(0);
            placeholderBuilder
                    .append(UNDERSCORE)
                    .append(new BigInteger(16, random).toString(16))
                    .append(UNDERSCORE);
        } while (str.contains(placeholderBuilder));

        return placeholderBuilder.toString();
    }

    static String replaceEncodedCharactersInFragment(final String str) {
        return str.replace("%2B", "+")
                .replace("%3D", "=")
                .replace("%7E", "~")
                .replace("%24", "$")
                .replace("%26", "&")
                .replace("%3B", ";")
                .replace("%3A", ":")
                .replace("%40", "@")
                .replace("%21", "!")
                .replace("%27", "'")
                .replace("%28", "(")
                .replace("%29", ")")
                .replace("%2C", ",")
                .replace("%2F", "/")
                .replace("%3F", "?");
    }

    static boolean isMailToLink(String link) {
        if (link != null) {
            return link.startsWith(MAIL_TO_PATTERN);
        } else {
            return false;
        }
    }

    /**
     * @param link to check
     * @return true if the link is a tel:// link
     */
    public static boolean isTelLink(@NotNull String link) {
        return link.startsWith(TEL_PATTERN);
    }

    /**
     * @param s to trim
     * @return a String without any space char
     */
    public static String trimAllSpacesInString(@NotNull String s) {
        return s.trim().replaceAll(SPACE_ESCAPED, EMPTY).replaceAll(" +", EMPTY);
    }

    public static String toKebabCase(@NotNull String title) {
        return title.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }

}
