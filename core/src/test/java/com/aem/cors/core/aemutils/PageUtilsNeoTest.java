package com.aem.cors.core.aemutils;

import com.aem.cors.core.AppAemContext;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.aem.cors.core.aemutils.PageUtilsNeo.getPageChildren;
import static com.aem.cors.core.aemutils.PageUtilsNeo.getPageForLanguage;
import static com.aem.cors.core.aemutils.PageUtilsNeo.getPageFromResource;
import static com.aem.cors.core.aemutils.PageUtilsNeo.getPageNavigationTitle;
import static com.aem.cors.core.aemutils.PageUtilsNeo.getPageProperty;
import static com.aem.cors.core.aemutils.PageUtilsNeo.getPageTemplateResourceType;
import static com.aem.cors.core.aemutils.PageUtilsNeo.getParentPage;
import static com.aem.cors.core.aemutils.PageUtilsNeo.getParentPageAtSpecificLevel;
import static com.aem.cors.core.aemutils.PageUtilsNeo.getPagesRecursively;
import static com.aem.cors.core.aemutils.PageUtilsNeo.getVanityUrls;
import static com.aem.cors.core.aemutils.PageUtilsNeo.isPageEmpty;
import static com.aem.cors.core.aemutils.PageUtilsNeo.isPageOfType;
import static com.aem.cors.core.aemutils.PageUtilsNeo.isResourceOfTypePage;
import static com.aem.cors.core.aemutils.PageUtilsNeo.isTargetedPage;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(AemContextExtension.class)
class PageUtilsNeoTest {

    private final AemContext context = AppAemContext.newAemContext();

    private Page rootPage;
    private Page childPage;

    @BeforeEach
    void setup() {
        rootPage = context.create().page("/content/site/en", "template", "Home");
        childPage = context.create().page("/content/site/en/child", "template", "Child");
    }

    @Test
    void testGetPageNavigationTitleNullPage() {
        assertThat(getPageNavigationTitle(null), is(""));
    }

    @Test
    void testGetPageNavigationTitleFallsBackToTitle() {
        context.create().resource(rootPage, "jcr:content").getValueMap();
        assertThat(getPageNavigationTitle(rootPage), is("Home"));
    }

    @Test
    void testGetVanityUrlsNullPage() {
        assertThat(getVanityUrls(null), is(List.of()));
    }

    @Test
    void testGetVanityUrlsPresent() {
        Page vanityPage = context.create().page("/content/site/en/vanity", "template", "Vanity");
        vanityPage.adaptTo(org.apache.sling.api.resource.Resource.class).getChild("jcr:content")
                .adaptTo(org.apache.sling.api.resource.ModifiableValueMap.class)
                .put("sling:vanityPath", new String[] {"/vanity-url"});

        assertThat(getVanityUrls(vanityPage), is(List.of("/vanity-url")));
    }

    @Test
    void testGetPageFromResourceNullResource() {
        assertThat(getPageFromResource(null), nullValue());
    }

    @Test
    void testGetPageFromResource() {
        Resource resource = rootPage.adaptTo(Resource.class);
        Page result = getPageFromResource(resource);
        assertThat(result.getPath(), is(rootPage.getPath()));
    }

    @Test
    void testGetParentPageAtSpecificLevelNullPage() {
        assertThat(getParentPageAtSpecificLevel(null, 0), nullValue());
    }

    @Test
    void testGetParentPageAtSpecificLevelDeeperThanCurrent() {
        assertThat(getParentPageAtSpecificLevel(childPage, 99), nullValue());
    }

    @Test
    void testGetPagesRecursivelyNullResource() {
        assertThat(getPagesRecursively(null), is(List.of()));
    }

    @Test
    void testGetPagesRecursivelyNonPageResource() {
        Resource nonPage = context.create().resource("/content/notapage", "jcr:primaryType", "nt:unstructured");
        assertThat(getPagesRecursively(nonPage), is(List.of()));
    }

    @Test
    void testGetPagesRecursivelyIncludesChildPages() {
        Resource rootResource = rootPage.adaptTo(Resource.class);
        List<Resource> result = getPagesRecursively(rootResource);
        List<String> paths = result.stream().map(Resource::getPath).collect(Collectors.toList());
        assertThat(paths, is(List.of(rootPage.getPath(), childPage.getPath())));
    }

    @Test
    void testGetPageChildrenNullPage() {
        assertThat(getPageChildren(null), is(List.of()));
    }

    @Test
    void testGetPageChildrenReturnsSubPages() {
        List<Page> result = getPageChildren(rootPage);
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getPath(), is(childPage.getPath()));
    }

    @Test
    void testGetParentPageFound() {
        Resource childContentResource = childPage.adaptTo(Resource.class);
        Optional<Page> result = getParentPage(childContentResource);
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getPath(), is(rootPage.getPath()));
    }

    @Test
    void testGetParentPageNotFoundAtRoot() {
        Resource rootResource = rootPage.adaptTo(Resource.class);
        Optional<Page> result = getParentPage(rootResource);
        assertThat(result.isPresent(), is(false));
    }

    @Test
    void testIsResourceOfTypePageTrue() {
        assertThat(isResourceOfTypePage(context.resourceResolver(), rootPage.getPath()), is(true));
    }

    @Test
    void testIsResourceOfTypePageFalseForNonPage() {
        context.create().resource("/content/notapage", "jcr:primaryType", "nt:unstructured");
        assertThat(isResourceOfTypePage(context.resourceResolver(), "/content/notapage"), is(false));
    }

    @Test
    void testIsPageOfTypeNullPage() {
        assertThat(isPageOfType(null, "any/type"), is(false));
    }

    @Test
    void testIsPageOfTypeMatches() {
        assertThat(isPageOfType(rootPage, rootPage.getContentResource().getResourceType()), is(true));
    }

    @Test
    void testGetPageTemplateResourceTypeNullPage() {
        assertThat(getPageTemplateResourceType(null), nullValue());
    }

    @Test
    void testGetPageTemplateResourceType() {
        assertThat(getPageTemplateResourceType(rootPage), is(rootPage.getContentResource().getResourceType()));
    }

    @Test
    void testGetPagePropertyDefaultWhenMissing() {
        assertThat(getPageProperty(rootPage, "missingProperty", "default"), is("default"));
    }

    @Test
    void testGetPagePropertyNullPage() {
        assertThat(getPageProperty(null, "any", "default"), is("default"));
    }

    @Test
    void testGetPagePropertyFound() {
        context.create().resource(rootPage, "jcr:content").getValueMap();
        assertThat(getPageProperty(rootPage, "jcr:title", "default"), is("Home"));
    }

    @Test
    void testIsTargetedPageNullPage() {
        assertThat(isTargetedPage(null), is(false));
    }

    @Test
    void testIsTargetedPageFalseForRegularPage() {
        assertThat(isTargetedPage(rootPage), is(false));
    }

    @Test
    void testGetPageForLanguageNullMaster() {
        assertThat(getPageForLanguage(null, "fr"), is(Optional.empty()));
    }

    @Test
    void testGetPageForLanguageSwitchesSegment() {
        Page enPage = context.create().page("/content/site/en/about");
        context.create().page("/content/site/fr/about");

        Optional<Page> result = getPageForLanguage(enPage, "fr");

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getPath(), is("/content/site/fr/about"));
    }

    @Test
    void testIsPageEmptyNullPath() {
        assertThat(isPageEmpty(context.resourceResolver(), null, "root/parsys"), is(false));
    }

    @Test
    void testIsPageEmptyNonPagePath() {
        context.create().resource("/content/notapage", "jcr:primaryType", "nt:unstructured");
        assertThat(isPageEmpty(context.resourceResolver(), "/content/notapage", "root/parsys"), is(false));
    }

    @Test
    void testIsPageEmptyTrueWhenNoParsysChildren() {
        context.create().resource(rootPage.getPath() + "/jcr:content/root/parsys");
        assertThat(isPageEmpty(context.resourceResolver(), rootPage.getPath(), "root/parsys"), is(true));
    }

    @Test
    void testIsPageEmptyFalseWhenParsysHasChildren() {
        context.create().resource(rootPage.getPath() + "/jcr:content/root/parsys/component1");
        assertThat(isPageEmpty(context.resourceResolver(), rootPage.getPath(), "root/parsys"), is(false));
    }
}
