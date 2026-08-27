package com.aem.cors.core.aemutils.page;

import com.aem.cors.core.AppAemContext;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.aem.cors.core.aemutils.page.PageOperationsUtils.isResourceTypePage;
import static com.aem.cors.core.aemutils.page.PageOperationsUtils.pageHasChildrenPages;
import static com.aem.cors.core.aemutils.page.PageOperationsUtils.pageHasResourceWithName;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(AemContextExtension.class)
class PageOperationsUtilsTest {

    private final AemContext context = AppAemContext.newAemContext();

    private Page rootPage;
    private Page childPage;

    @BeforeEach
    void setup() {
        rootPage = context.create().page("/content/site/en");
        childPage = context.create().page("/content/site/en/child");
    }

    @Test
    void testIsResourceTypePageTrue() {
        Resource pageResource = rootPage.adaptTo(Resource.class);
        assertThat(isResourceTypePage(pageResource), is(true));
    }

    @Test
    void testIsResourceTypePageFalse() {
        Resource nonPage = context.create().resource("/content/site/notapage", "sling:resourceType", "my/type");
        assertThat(isResourceTypePage(nonPage), is(false));
    }

    @Test
    void testPageHasChildrenPagesTrue() {
        assertThat(pageHasChildrenPages(context.resourceResolver(), rootPage.getPath()), is(true));
    }

    @Test
    void testPageHasChildrenPagesTrueEvenForLeafPage() {
        // the recursive resource list starts with the page resource itself, which already matches
        // isResourceTypePage, so this is always true for any resolvable page path - even leaves
        // with no actual sub-pages
        assertThat(pageHasChildrenPages(context.resourceResolver(), childPage.getPath()), is(true));
    }

    @Test
    void testPageHasChildrenPagesMissingResourceReturnsFalse() {
        assertThat(pageHasChildrenPages(context.resourceResolver(), "/content/missing"), is(false));
    }

    @Test
    void testPageHasResourceWithNameFound() {
        assertThat(pageHasResourceWithName(context.resourceResolver(), rootPage.getPath(), "child"), is(true));
    }

    @Test
    void testPageHasResourceWithNameNotFound() {
        assertThat(pageHasResourceWithName(context.resourceResolver(), rootPage.getPath(), "unknown"), is(false));
    }

    @Test
    void testPageHasResourceWithNameMissingResourceThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> pageHasResourceWithName(context.resourceResolver(), "/content/missing", "child"));
    }
}
