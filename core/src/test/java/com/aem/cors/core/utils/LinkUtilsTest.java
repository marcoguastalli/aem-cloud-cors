package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.aem.cors.core.utils.LinkUtils.isTelLink;
import static com.aem.cors.core.utils.LinkUtils.mask;
import static com.aem.cors.core.utils.LinkUtils.toKebabCase;
import static com.aem.cors.core.utils.LinkUtils.trimAllSpacesInString;
import static com.aem.cors.core.utils.LinkUtils.unmask;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class LinkUtilsTest {

    @Test
    void testMaskAndUnmaskRoundTrip() {
        Map<String, String> placeholders = new HashMap<>();
        String original = "/path/to/page.html?r=<%= recipient.id %>";

        String masked = mask(original, placeholders);

        assertThat(masked, is(org.hamcrest.CoreMatchers.not(original)));
        assertThat(placeholders.isEmpty(), is(false));

        String unmasked = unmask(masked, placeholders);
        assertThat(unmasked, is(original));
    }

    @Test
    void testMaskNull() {
        assertThat(mask(null, new HashMap<>()), nullValue());
    }

    @Test
    void testUnmaskNull() {
        assertThat(unmask(null, new HashMap<>()), nullValue());
    }

    @Test
    void testMaskEncodedReservedCharacter() {
        Map<String, String> placeholders = new HashMap<>();
        String masked = mask("/path%20with%20spaces", placeholders);
        assertThat(placeholders.size(), is(2));
        assertThat(unmask(masked, placeholders), is("/path%20with%20spaces"));
    }

    @Test
    void testIsTelLinkTrue() {
        assertThat(isTelLink("tel:+123456789"), is(true));
    }

    @Test
    void testIsTelLinkFalse() {
        assertThat(isTelLink("mailto:foo@bar.com"), is(false));
    }

    @Test
    void testTrimAllSpacesInString() {
        assertThat(trimAllSpacesInString("  a b%20c  "), is("abc"));
    }

    @Test
    void testToKebabCase() {
        assertThat(toKebabCase("Hello World! Foo_Bar"), is("hello-world-foo-bar"));
    }

    @Test
    void testToKebabCaseTrimsLeadingTrailingHyphens() {
        assertThat(toKebabCase("!Start and End!"), is("start-and-end"));
    }
}
