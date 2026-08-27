package com.aem.cors.core.utils;

import javax.servlet.http.Cookie;

public final class CookieUtils {

    private CookieUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** Create a Cookie object
     *
     * @param cookieName the name
     * @param cookieValue the value
     * @param expiry the max age
     * @param uri the path
     * @return a Cookie Object */
    public static Cookie createCookie(final String cookieName, final String cookieValue, final int expiry, final String uri) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(expiry);
        cookie.setPath(uri);
        return cookie;
    }

}
