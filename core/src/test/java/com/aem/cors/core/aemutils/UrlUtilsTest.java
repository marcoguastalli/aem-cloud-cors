package com.aem.cors.core.aemutils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.StringJoiner;

import static com.aem.cors.core.aemutils.UrlUtils.addParam;
import static com.aem.cors.core.aemutils.UrlUtils.addParamsInUrl;
import static com.aem.cors.core.aemutils.UrlUtils.encodeUrl;
import static com.aem.cors.core.aemutils.UrlUtils.getRequestParam;
import static com.aem.cors.core.aemutils.UrlUtils.showBreadcrumb;
import static com.aem.cors.core.aemutils.UrlUtils.showFooter;
import static com.aem.cors.core.aemutils.UrlUtils.showHeader;
import static com.aem.cors.core.aemutils.UrlUtils.validateUrlUsingOwasp;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UrlUtilsTest {

    @Mock
    SlingHttpServletRequest request;

    @Test
    void testValidateUrlUsingOwaspValid() {
        assertThat(validateUrlUsingOwasp("mailto:foo@bar.com"), is(true));
    }

    @Test
    void testValidateUrlUsingOwaspInvalid() {
        assertThat(validateUrlUsingOwasp("not a url"), is(false));
    }

    @Test
    void testShowHeaderDefaultTrue() {
        when(request.getParameter("showHeader")).thenReturn(null);
        assertThat(showHeader(request), is(true));
    }

    @Test
    void testShowHeaderExplicitFalse() {
        when(request.getParameter("showHeader")).thenReturn("false");
        assertThat(showHeader(request), is(false));
    }

    @Test
    void testShowFooterDefaultTrue() {
        when(request.getParameter("showFooter")).thenReturn(null);
        assertThat(showFooter(request), is(true));
    }

    @Test
    void testShowBreadcrumbExplicitFalse() {
        when(request.getParameter("showBreadcrumb")).thenReturn("false");
        assertThat(showBreadcrumb(request), is(false));
    }

    @Test
    void testAddParamsInUrlEmptyParams() throws URISyntaxException {
        assertThat(addParamsInUrl("https://example.com", Map.of()), is("https://example.com"));
    }

    @Test
    void testAddParamsInUrlWithParams() throws URISyntaxException {
        String result = addParamsInUrl("https://example.com", Map.of("a", "1"));
        assertThat(result, is("https://example.com?a=1"));
    }

    @Test
    void testEncodeUrl() {
        assertThat(encodeUrl("a b"), is("a+b"));
    }

    @Test
    void testGetRequestParam() {
        when(request.getParameter("foo")).thenReturn("bar");
        assertThat(getRequestParam(request, "foo"), is("bar"));
    }

    @Test
    void testGetRequestParamNullRequest() {
        assertThat(getRequestParam(null, "foo"), nullValue());
    }

    @Test
    void testAddParamNotBlank() {
        StringJoiner joiner = new StringJoiner(",");
        addParam(joiner, "key", "value");
        assertThat(joiner.toString(), is("key=value"));
    }

    @Test
    void testAddParamBlankSkipped() {
        StringJoiner joiner = new StringJoiner(",");
        addParam(joiner, "key", "  ");
        assertThat(joiner.toString(), is(""));
    }
}
