package com.aem.cors.core.commonbeans;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.aem.cors.core.CoreConstantsTest.PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

class PathsBeanTest {

    @Test
    void tesBean() {
        PathsBean result = new PathsBean(List.of(PATH_CONTENT_LANGUAGE_MASTERS_EN_HOME));
        assertThat(result, notNullValue());
        assertThat(new PathsBean(), notNullValue());
    }
}
