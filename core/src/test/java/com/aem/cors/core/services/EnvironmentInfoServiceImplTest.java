package com.aem.cors.core.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.aem.cors.core.CoreConstantsTest.JUST_A_STRING;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnvironmentInfoServiceImplTest {

    EnvironmentInfoServiceImpl environmentInfoService;
    @Mock
    EnvironmentInfoServiceImpl.Configuration config;

    @BeforeEach
    void init() {
        environmentInfoService = new EnvironmentInfoServiceImpl();
        when(config.hostname()).thenReturn(JUST_A_STRING);
        when(config.environmentShortName()).thenReturn(JUST_A_STRING);
        when(config.environmentAndRunMode()).thenReturn(JUST_A_STRING);
        when(config.organizationId()).thenReturn(JUST_A_STRING);
        when(config.organizationSecret()).thenReturn(JUST_A_STRING);
        environmentInfoService.activate(config);
    }

    @Test
    void testGetEnvironmentInfo() {
        assertThat(environmentInfoService, notNullValue());
        assertThat(environmentInfoService.getHostname(), is(JUST_A_STRING));
        assertThat(environmentInfoService.getEnvironmentShortName(), is(JUST_A_STRING));
        assertThat(environmentInfoService.getEnvironmentAndRunMode(), is(JUST_A_STRING));
        assertThat(environmentInfoService.getOrganizationId(), is(JUST_A_STRING));
        assertThat(environmentInfoService.getOrganizationSecret(), is(JUST_A_STRING));
        assertThat(environmentInfoService.isAuthor(), notNullValue());
        assertThat(environmentInfoService.isPublish(), notNullValue());
        assertThat(environmentInfoService.isProdPublish(), notNullValue());
        assertThat(environmentInfoService.isProd(), notNullValue());
        assertThat(environmentInfoService.isStage(), notNullValue());
        assertThat(environmentInfoService.isDev(), notNullValue());
        assertThat(environmentInfoService.isRde(), notNullValue());
        assertThat(environmentInfoService.getEnvironmentString(), notNullValue());
    }
}
