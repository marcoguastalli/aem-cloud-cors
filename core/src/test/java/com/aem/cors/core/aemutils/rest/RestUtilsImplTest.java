package com.aem.cors.core.aemutils.rest;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RestUtilsImplTest {

    private final RestUtilsImpl restUtils = new RestUtilsImpl();

    @Mock
    SlingHttpServletRequest request;

    private static ServletInputStream toServletInputStream(String content) {
        final java.io.ByteArrayInputStream delegate = new java.io.ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public int read() {
                return delegate.read();
            }

            @Override
            public boolean isFinished() {
                return delegate.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // not needed for tests
            }
        };
    }

    @Test
    void testCreateParamsFromMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("a", "1");

        List<BasicNameValuePair> result = restUtils.createParamsFromMap(map);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getName(), is("a"));
        assertThat(result.get(0).getValue(), is("1"));
    }

    @Test
    void testBuildUrlBlankBaseUrlReturnsNull() {
        assertThat(restUtils.buildUrl("", "/endpoint", null), nullValue());
    }

    @Test
    void testBuildUrlNoEndpointNoParams() {
        assertThat(restUtils.buildUrl("https://example.com", "", null), is("https://example.com"));
    }

    @Test
    void testBuildUrlNoEndpointWithParams() {
        String result = restUtils.buildUrl("https://example.com", "", List.of(new BasicNameValuePair("a", "1")));
        assertThat(result, is("https://example.com?a=1"));
    }

    @Test
    void testBuildUrlBothHaveSlash() {
        assertThat(restUtils.buildUrl("https://example.com/", "/rest/endpoint", null), is("https://example.com/rest/endpoint"));
    }

    @Test
    void testBuildUrlNeitherHasSlash() {
        assertThat(restUtils.buildUrl("https://example.com", "rest/endpoint", null), is("https://example.com/rest/endpoint"));
    }

    @Test
    void testBuildUrlOneHasSlash() {
        assertThat(restUtils.buildUrl("https://example.com/", "rest/endpoint", null), is("https://example.com/rest/endpoint"));
    }

    @Test
    void testBuildUrlWithParams() {
        String result = restUtils.buildUrl("https://example.com", "/rest", List.of(new BasicNameValuePair("a", "1")));
        assertThat(result, is("https://example.com/rest?a=1"));
    }

    @Test
    void testCreateGetRequestNoHeaders() {
        HttpGet request = restUtils.createGetRequestNoHeaders("https://example.com", "/rest", null);
        assertThat(request.getURI().toString(), is("https://example.com/rest"));
        assertThat(request.getFirstHeader("Accept"), nullValue());
    }

    @Test
    void testCreateGetRequestSetsJsonHeaders() {
        HttpGet request = restUtils.createGetRequest("https://example.com", "/rest", false, null, false, null, null);
        assertThat(request.getFirstHeader("Accept").getValue(), is("application/json"));
        assertThat(request.getFirstHeader("Content-Type").getValue(), is("application/json"));
    }

    @Test
    void testCreateGetRequestWithBasicAuthAddsAuthorizationHeader() {
        HttpGet request = restUtils.createGetRequest("https://example.com", "/rest", true, null, true, "user", "pass");
        assertThat(request.getFirstHeader("Authorization"), notNullValue());
        assertThat(request.getFirstHeader("Authorization").getValue(), startsWith("Basic "));
    }

    @Test
    void testCreateGetRequestCopyingCurrentRequestHeaders() {
        java.util.Vector<String> headerNames = new java.util.Vector<>();
        headerNames.add("X-Custom");
        when(request.getHeaderNames()).thenReturn(headerNames.elements());
        when(request.getHeader("X-Custom")).thenReturn("custom-value");

        HttpGet result = restUtils.createGetRequestCopyingCurrentRequestHeaders(request, "https://example.com", "/rest", false, null,
                false, null, null);

        assertThat(result.getFirstHeader("X-Custom").getValue(), is("custom-value"));
    }

    @Test
    void testGetIpAddress() {
        when(request.getHeader("remote_addr")).thenReturn("127.0.0.1");
        assertThat(restUtils.getIpAddress(request), is("127.0.0.1"));
    }

    @Test
    void testGetInetAddress() throws Exception {
        assertThat(restUtils.getInetAddress(request), notNullValue());
    }

    @Test
    void testGetJsonStringFromRequest() throws Exception {
        when(request.getInputStream()).thenReturn(toServletInputStream("{\"a\":1}"));
        assertThat(restUtils.getJsonStringFromRequest(request), is("{\"a\":1}"));
    }

    @Test
    void testGetJsonStringFromRequestEmptyBody() throws Exception {
        when(request.getInputStream()).thenReturn(toServletInputStream(""));
        assertThat(restUtils.getJsonStringFromRequest(request), is(""));
    }
}
