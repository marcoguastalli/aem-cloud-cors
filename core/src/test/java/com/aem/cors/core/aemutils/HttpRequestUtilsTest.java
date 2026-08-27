package com.aem.cors.core.aemutils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static com.aem.cors.core.aemutils.HttpRequestUtils.getRequestAttributeAs;
import static com.aem.cors.core.aemutils.HttpRequestUtils.getRequestHeaderAsBoolean;
import static com.aem.cors.core.aemutils.HttpRequestUtils.getRequestHeaderAsString;
import static com.aem.cors.core.aemutils.HttpRequestUtils.getRequestParameterAsInteger;
import static com.aem.cors.core.aemutils.HttpRequestUtils.getRequestParameterAsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HttpRequestUtilsTest {

    @Mock
    SlingHttpServletRequest request;
    @Mock
    RequestParameter requestParameter;

    @Test
    void testGetRequestParameterAsStringPresent() {
        when(request.getRequestParameter("foo")).thenReturn(requestParameter);
        when(requestParameter.getString()).thenReturn("bar");

        assertThat(getRequestParameterAsString(request, "foo"), is(Optional.of("bar")));
    }

    @Test
    void testGetRequestParameterAsStringMissing() {
        when(request.getRequestParameter("foo")).thenReturn(null);
        assertThat(getRequestParameterAsString(request, "foo"), is(Optional.empty()));
    }

    @Test
    void testGetRequestParameterAsStringNullRequest() {
        assertThat(getRequestParameterAsString(null, "foo"), is(Optional.empty()));
    }

    @Test
    void testGetRequestParameterAsIntegerValid() {
        when(request.getRequestParameter("page")).thenReturn(requestParameter);
        when(requestParameter.getString()).thenReturn("5");

        assertThat(getRequestParameterAsInteger(request, "page"), is(Optional.of(5)));
    }

    @Test
    void testGetRequestParameterAsIntegerNotDigits() {
        when(request.getRequestParameter("page")).thenReturn(requestParameter);
        when(requestParameter.getString()).thenReturn("abc");

        assertThat(getRequestParameterAsInteger(request, "page"), is(Optional.empty()));
    }

    @Test
    void testGetRequestHeaderAsStringPresent() {
        when(request.getHeader("X-Foo")).thenReturn("value");
        assertThat(getRequestHeaderAsString(request, "X-Foo"), is(Optional.of("value")));
    }

    @Test
    void testGetRequestHeaderAsBooleanTrue() {
        when(request.getHeader("X-Foo")).thenReturn("true");
        assertThat(getRequestHeaderAsBoolean(request, "X-Foo"), is(Optional.of(true)));
    }

    @Test
    void testGetRequestAttributeAsMatchingType() {
        when(request.getAttribute("attr")).thenReturn("value");
        assertThat(getRequestAttributeAs(request, "attr", String.class), is(Optional.of("value")));
    }

    @Test
    void testGetRequestAttributeAsWrongType() {
        when(request.getAttribute("attr")).thenReturn(42);
        assertThat(getRequestAttributeAs(request, "attr", String.class), is(Optional.empty()));
    }
}
