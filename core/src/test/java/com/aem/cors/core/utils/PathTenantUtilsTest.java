package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;

import static com.aem.cors.core.utils.PathTenantUtils.getTenantFromPath;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class PathTenantUtilsTest {

    @Test
    void testGetTenantFromPathSimple() {
        assertThat(getTenantFromPath("/content/mysite/en/home"), is("mysite"));
    }

    @Test
    void testGetTenantFromPathTrailingSlash() {
        assertThat(getTenantFromPath("/content/mysite/"), is("mysite"));
    }

    @Test
    void testGetTenantFromPathNoContentSegment() {
        assertThat(getTenantFromPath("/etc/config"), is(""));
    }
}
