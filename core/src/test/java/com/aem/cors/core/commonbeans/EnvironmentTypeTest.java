package com.aem.cors.core.commonbeans;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

class EnvironmentTypeTest {

    @Test
    void testEnvironmentTypeValues() {
        assertThat(EnvironmentType.AUTHOR, notNullValue());
        assertThat(EnvironmentType.PUBLISH, notNullValue());
        assertThat(EnvironmentType.DISPATCHER, notNullValue());
        assertThat(EnvironmentType.PROD_AUTHOR, notNullValue());
        assertThat(EnvironmentType.PROD_PUBLISH, notNullValue());
        assertThat(EnvironmentType.STAGE_AUTHOR, notNullValue());
        assertThat(EnvironmentType.STAGE_PUBLISH, notNullValue());
        assertThat(EnvironmentType.DEV_AUTHOR, notNullValue());
        assertThat(EnvironmentType.DEV_PUBLISH, notNullValue());
        assertThat(EnvironmentType.RDE_AUTHOR, notNullValue());
        assertThat(EnvironmentType.RDE_PUBLISH, notNullValue());
    }

    @Test
    void testEnvironmentTypeNames() {
        assertThat(EnvironmentType.AUTHOR.name(), is("AUTHOR"));
        assertThat(EnvironmentType.PROD_AUTHOR.name(), is("PROD_AUTHOR"));
        assertThat(EnvironmentType.PROD_PUBLISH.name(), is("PROD_PUBLISH"));
        assertThat(EnvironmentType.STAGE_AUTHOR.name(), is("STAGE_AUTHOR"));
        assertThat(EnvironmentType.DEV_AUTHOR.name(), is("DEV_AUTHOR"));
        assertThat(EnvironmentType.RDE_PUBLISH.name(), is("RDE_PUBLISH"));
    }

    @Test
    void testEnvironmentTypeOrdinals() {
        assertThat(EnvironmentType.AUTHOR.ordinal(), is(0));
        assertThat(EnvironmentType.PUBLISH.ordinal(), is(1));
        assertThat(EnvironmentType.PROD_AUTHOR.ordinal() > EnvironmentType.PUBLISH.ordinal(), is(true));
    }
}
