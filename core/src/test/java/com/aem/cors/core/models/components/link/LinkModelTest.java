package com.aem.cors.core.models.components.link;

import com.aem.cors.core.AppAemContext;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(AemContextExtension.class)
class LinkModelTest {

    private final AemContext context = AppAemContext.newAemContext();

    private Page page;

    @BeforeEach
    void setup() {
        page = context.create().page("/content/aemcors/en/home");
    }

    @Test
    void testGetLinkURL_withLinkURL() {
        Resource resource = context.create().resource(page, "link",
                "sling:resourceType", "aemcors/components/link",
                "linkURL", "/content/aemcors/en/home",
                "linkText", "reload");

        LinkModel linkModel = adaptToLinkModel(resource);

        assertThat(linkModel.getLinkText(), is("reload"));
        assertThat(linkModel.getLinkURL(), notNullValue());
    }

    @Test
    void testGetLinkURL_emptyLink_doesNotThrow() {
        // regression test: an author-created link component with no linkURL/linkText set
        // (i.e. an invalid Link and a null linkURL) used to NPE in getLinkURL()
        Resource resource = context.create().resource(page, "link_empty",
                "sling:resourceType", "aemcors/components/link");

        LinkModel linkModel = adaptToLinkModel(resource);

        assertDoesNotThrow(linkModel::getLinkURL);
        assertThat(linkModel.getLinkURL(), nullValue());
        assertThat(linkModel.isEmpty(), is(true));
    }

    private LinkModel adaptToLinkModel(Resource resource) {
        MockSlingHttpServletRequest request = context.request();
        request.setResource(resource);
        return request.adaptTo(LinkModel.class);
    }
}
