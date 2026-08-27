package com.aem.cors.core.aemutils;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Map;

import static com.aem.cors.core.aemutils.LightBoxUtils.createDataLightboxMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LightBoxUtilsTest {

    @Mock
    ResourceResolver resourceResolver;

    @Test
    void testCreateDataLightboxMapFullwidth() {
        when(resourceResolver.map("/content/foo")).thenReturn("/content/foo");

        Map<String, String> result = createDataLightboxMap(resourceResolver, "/content/foo", "lightbox-fullwidth");

        assertThat(result.get("data-lightbox-url"), is("/content/foo.lightbox.html"));
        assertThat(result.get("data-lightbox-variant"), is("fullwidth"));
        assertThat(result.get("data-lightbox"), is("true"));
    }

    @Test
    void testCreateDataLightboxMapSmallWithTitle() {
        when(resourceResolver.map("/content/foo")).thenReturn("/content/foo");

        Map<String, String> result = createDataLightboxMap(resourceResolver, "/content/foo", "lightbox-small-title");

        assertThat(result.get("data-lightbox-url"), is("/content/foo.lightboxtitle.html"));
        assertThat(result.get("data-lightbox-variant"), is("small"));
    }

    @Test
    void testCreateDataLightboxMapUnknownTargetStillSetsFlag() {
        Map<String, String> result = createDataLightboxMap(resourceResolver, "/content/foo", "unknown-target");
        assertThat(result.get("data-lightbox"), is("true"));
        assertThat(result.containsKey("data-lightbox-url"), is(false));
    }
}
