package com.aem.cors.core.aemutils;

import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

@Slf4j
public final class UserUtils {

    private UserUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /**
     * Retrieve the logged user name from the request
     *
     * @param request the request
     * @return a String with the logged user name
     */
    public static String getUserNameFromRequest(final SlingHttpServletRequest request) {
        try {
            UserManager userManager = request.getResourceResolver().adaptTo(UserManager.class);
            Session session = request.getResourceResolver().adaptTo(Session.class);
            if (userManager == null || session == null) {
                return null;
            }
            final Authorizable auth = userManager.getAuthorizable(session.getUserID());
            if (auth != null) {
                return auth.getPrincipal().getName();
            }
        } catch (RepositoryException e) {
            log.error("Error getUserNameFromRequest", e);
        }
        return null;
    }

    /**
     * Retrieve the logged user name ID from the request
     *
     * @param request the request
     * @return a String with the logged user name ID
     */
    public static String getUserNameIdFromRequest(final SlingHttpServletRequest request) {
        try {
            final Authorizable authorizable = request.getResourceResolver().adaptTo(Authorizable.class);
            if (authorizable == null) {
                log.error("Cannot adapt resolver to Authorizable");
                return null;
            }
            final String user = authorizable.getPrincipal().getName();
            if (user == null) {
                return authorizable.getID();
            }
            return user;
        } catch (Exception e) {
            log.error("Error getUserNameIdFromRequest", e);
        }
        return null;
    }

}
