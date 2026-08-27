package com.aem.cors.core.aemutils.page;

import com.aem.cors.core.AppAemContext;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.aem.cors.core.aemutils.page.PageManagingUtils.copyPage;
import static com.aem.cors.core.aemutils.page.PageManagingUtils.equalsPagesStructure;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class PageManagingUtilsTest {

    @Mock
    PageManager mockPageManager;
    @Mock
    Page mockPage;
    @Mock
    Page copiedPage;

    private final AemContext context = AppAemContext.newAemContext();

    private Page sourcePage;

    @BeforeEach
    void setup() {
        sourcePage = context.create().page("/content/site/en/source");
        context.create().page("/content/site/en/other");
    }

    @Test
    void testCopyPageDelegatesWithShallowCopyAndNoConflictResolution() throws Exception {
        // io.wcm's mock PageManager doesn't implement the Page-based copy() overloads (they're
        // stubs that always throw UnsupportedOperationException), so this verifies the delegation
        // contract - shallow=true, resolveConflict=false - via a Mockito mock instead.
        when(mockPageManager.copy(mockPage, "/content/site/en/copy", "sibling", true, false)).thenReturn(copiedPage);

        Page result = copyPage(mockPageManager, "/content/site/en/copy", mockPage, "sibling");

        assertThat(result, is(copiedPage));
        verify(mockPageManager).copy(mockPage, "/content/site/en/copy", "sibling", true, false);
    }

    @Test
    void testEqualsPagesStructureIdenticalPaths() {
        PageManager pageManager = context.pageManager();
        assertThat(equalsPagesStructure(pageManager, sourcePage.getPath(), sourcePage.getPath()), is(true));
    }

    @Test
    void testEqualsPagesStructureDifferentContent() {
        PageManager pageManager = context.pageManager();
        assertThat(equalsPagesStructure(pageManager, "/content/site/en/source", "/content/site/en/other"), is(true));
    }

    @Test
    void testEqualsPagesStructureMissingPageReturnsFalse() {
        PageManager pageManager = context.pageManager();
        assertThat(equalsPagesStructure(pageManager, "/content/missing", "/content/site/en/other"), is(false));
    }

    @Test
    void testEqualsPagesStructureDifferentChildResourceTypes() {
        context.create().resource(sourcePage.getPath() + "/jcr:content/component", "sling:resourceType", "type/a");
        Page targetPage = context.create().page("/content/site/en/target");
        context.create().resource(targetPage.getPath() + "/jcr:content/component", "sling:resourceType", "type/b");

        PageManager pageManager = context.pageManager();
        assertThat(equalsPagesStructure(pageManager, sourcePage.getPath(), targetPage.getPath()), is(false));
    }
}
