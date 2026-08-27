package com.aem.cors.core.aemutils;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.jcr.Session;
import java.security.Principal;

import static com.aem.cors.core.aemutils.UserUtils.getUserNameFromRequest;
import static com.aem.cors.core.aemutils.UserUtils.getUserNameIdFromRequest;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserUtilsTest {

    @Mock
    SlingHttpServletRequest request;
    @Mock
    ResourceResolver resourceResolver;
    @Mock
    UserManager userManager;
    @Mock
    Session session;
    @Mock
    Authorizable authorizable;
    @Mock
    Principal principal;

    @Test
    void testGetUserNameFromRequest() throws Exception {
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(session.getUserID()).thenReturn("admin");
        when(userManager.getAuthorizable("admin")).thenReturn(authorizable);
        when(authorizable.getPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("Administrator");

        assertThat(getUserNameFromRequest(request), is("Administrator"));
    }

    @Test
    void testGetUserNameFromRequestNullUserManager() throws Exception {
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(UserManager.class)).thenReturn(null);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);

        assertThat(getUserNameFromRequest(request), nullValue());
    }

    @Test
    void testGetUserNameIdFromRequestUsesPrincipalName() throws Exception {
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Authorizable.class)).thenReturn(authorizable);
        when(authorizable.getPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("john");

        assertThat(getUserNameIdFromRequest(request), is("john"));
    }

    @Test
    void testGetUserNameIdFromRequestFallsBackToId() throws Exception {
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Authorizable.class)).thenReturn(authorizable);
        when(authorizable.getPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(null);
        when(authorizable.getID()).thenReturn("john-id");

        assertThat(getUserNameIdFromRequest(request), is("john-id"));
    }

    @Test
    void testGetUserNameIdFromRequestNullAuthorizable() {
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Authorizable.class)).thenReturn(null);

        assertThat(getUserNameIdFromRequest(request), nullValue());
    }
}
