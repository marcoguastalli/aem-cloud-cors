package com.aem.cors.core.services;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class EnvironmentInfoServiceTest {

    @Test
    void testEnvironmentInfoServiceInterface() {
        assertThat(EnvironmentInfoService.class, notNullValue());
        assertThat(EnvironmentInfoService.class.isInterface(), notNullValue());
    }

    @Test
    void testEnvironmentInfoServiceMethods() throws NoSuchMethodException {
        assertThat(EnvironmentInfoService.class.getMethod("getHost"), notNullValue());
        assertThat(EnvironmentInfoService.class.getMethod("getHostname"), notNullValue());
        assertThat(EnvironmentInfoService.class.getMethod("getEnvironmentShortName"), notNullValue());
        assertThat(EnvironmentInfoService.class.getMethod("getEnvironmentAndRunMode"), notNullValue());
        assertThat(EnvironmentInfoService.class.getMethod("getOrganizationId"), notNullValue());
        assertThat(EnvironmentInfoService.class.getMethod("isAuthor"), notNullValue());
        assertThat(EnvironmentInfoService.class.getMethod("isPublish"), notNullValue());
        assertThat(EnvironmentInfoService.class.getMethod("isProdPublish"), notNullValue());
        assertThat(EnvironmentInfoService.class.getMethod("isProd"), notNullValue());
        assertThat(EnvironmentInfoService.class.getMethod("isStage"), notNullValue());
        assertThat(EnvironmentInfoService.class.getMethod("isDev"), notNullValue());
        assertThat(EnvironmentInfoService.class.getMethod("isRde"), notNullValue());
        assertThat(EnvironmentInfoService.class.getMethod("getEnvironmentString"), notNullValue());
    }
}
