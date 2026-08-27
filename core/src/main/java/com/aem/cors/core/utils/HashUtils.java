package com.aem.cors.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static java.lang.String.format;

/**
 * Util class for hashes
 */
@Slf4j
public class HashUtils {

    private HashUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    private static final String ALGORITHM_SHA1 = "SHA-1";
    private static final String HEX_FORMAT = "%02x";

    @SuppressWarnings("squid:S4790")
    public static String getStringHash(@NotNull String s) {
        try {
            final MessageDigest md = MessageDigest.getInstance(ALGORITHM_SHA1);
            final byte[] hashBytes = md.digest(s.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(format(HEX_FORMAT, b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error getStringHash", e);
        }
        return s;
    }

    /**
     * @param encodedString a base64 encoded String
     * @return the decoded String
     */
    public static String decodeStringFromBase64(@NotNull String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes);
    }
}
