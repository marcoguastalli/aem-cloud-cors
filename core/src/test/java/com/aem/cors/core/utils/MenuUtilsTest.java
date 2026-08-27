package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;

import static com.aem.cors.core.utils.MenuUtils.calculateIsActive;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MenuUtilsTest {

    @Test
    void testCalculateIsActiveExactMatch() {
        assertThat(calculateIsActive("/content/site/en/home", "/content/site/en/home"), is(true));
    }

    @Test
    void testCalculateIsActiveDeeperCurrentPage() {
        assertThat(calculateIsActive("/content/site/en/home/subpage", "/content/site/en/home"), is(true));
    }

    @Test
    void testCalculateIsActiveDifferentBranch() {
        assertThat(calculateIsActive("/content/site/en/other", "/content/site/en/home"), is(false));
    }
}
