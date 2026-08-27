package com.aem.cors.core.utils.page.domain;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

class PageInfoTest {

    @Test
    void testGetters() {
        PageInfo pageInfo = new PageInfo("/content/foo", "site/page", "/content/parent", "/content/en/parent");

        assertThat(pageInfo.getPath(), is("/content/foo"));
        assertThat(pageInfo.getResourceType(), is("site/page"));
        assertThat(pageInfo.getParentPagePath(), is("/content/parent"));
        assertThat(pageInfo.getParentLanguagePagePath(), is("/content/en/parent"));
    }

    @Test
    void testEqualsAndHashCode() {
        PageInfo a = new PageInfo("/content/foo", "site/page", "/content/parent", "/content/en/parent");
        PageInfo b = new PageInfo("/content/foo", "site/page", "/content/parent", "/content/en/parent");
        PageInfo c = new PageInfo("/content/other", "site/page", "/content/parent", "/content/en/parent");

        assertThat(a, is(b));
        assertThat(a.hashCode(), is(b.hashCode()));
        assertThat(a, is(not(c)));
        assertThat(a.equals("not a PageInfo"), is(false));
        assertThat(a.equals(null), is(false));
    }
}
