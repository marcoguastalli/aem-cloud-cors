package com.aem.cors.core.servlets;

import com.aem.cors.core.AppAemContext;
import com.aem.cors.core.services.EnvironmentInfoService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static com.aem.cors.core.CoreConstants.OSGI_CONFIG_PREFIX;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class CoreBundleServletTest {
    static final String SERVLET_OUTPUT = "{\"result\":\"The bundle 'aem-cors.core' is up and active in '' with ORGANIZATION_ID: '" + OSGI_CONFIG_PREFIX + "'\"}";

    AemContext aemContext = AppAemContext.newAemContext();
    @Mock
    EnvironmentInfoService environmentInfoService;
    @InjectMocks
    CoreBundleServlet coreBundleServlet;

    @BeforeEach
    void init() {
        aemContext.registerService(EnvironmentInfoService.class, environmentInfoService);
        // when
        when(environmentInfoService.getOrganizationId()).thenReturn(OSGI_CONFIG_PREFIX);
        when(environmentInfoService.getEnvironmentAndRunMode()).thenReturn(EMPTY);
    }

    @Test
    void testDoGet(AemContext context) throws IOException {
        // given
        MockSlingHttpServletRequest request = context.request();
        MockSlingHttpServletResponse response = context.response();
        // then
        coreBundleServlet.doGet(request, response);
        assertThat(coreBundleServlet, notNullValue());
        assertThat(response.getOutputAsString(), is(SERVLET_OUTPUT));
    }

    @Test
    void testDoPost(AemContext context) throws IOException {
        // given
        MockSlingHttpServletRequest request = context.request();
        MockSlingHttpServletResponse response = context.response();
        // then
        coreBundleServlet.doPost(request, response);
        assertThat(coreBundleServlet, notNullValue());
        assertThat(response.getOutputAsString(), is(SERVLET_OUTPUT));
    }
}
