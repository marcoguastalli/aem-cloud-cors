package com.aem.cors.core.services;

import com.aem.cors.core.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SlingResourceResolverFactoryImplTest {

    AemContext aemContext = AppAemContext.newAemContext();
    @Mock
    ResourceResolverFactory resourceResolverFactory;
    @InjectMocks
    SlingResourceResolverFactoryImpl slingResourceResolverFactory;

    @BeforeEach
    void init() throws LoginException {
        when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenReturn(aemContext.resourceResolver());
    }

    @Test
    void testGetResourceResolver() {
        assertThat(slingResourceResolverFactory.getResourceResolver(anyString()), notNullValue());
    }
}
