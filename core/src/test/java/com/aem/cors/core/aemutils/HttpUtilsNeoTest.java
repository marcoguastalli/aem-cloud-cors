package com.aem.cors.core.aemutils;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import static com.aem.cors.core.aemutils.HttpUtilsNeo.convertCookie;
import static com.aem.cors.core.aemutils.HttpUtilsNeo.convertParameters;
import static com.aem.cors.core.aemutils.HttpUtilsNeo.convertParametersArray;
import static com.aem.cors.core.aemutils.HttpUtilsNeo.copyHeaders;
import static com.aem.cors.core.aemutils.HttpUtilsNeo.copyRequestHeaders;
import static com.aem.cors.core.aemutils.HttpUtilsNeo.copyResponseHeaders;
import static com.aem.cors.core.aemutils.HttpUtilsNeo.createCookieStore;
import static com.aem.cors.core.aemutils.HttpUtilsNeo.getTransactionId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HttpUtilsNeoTest {

    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    HttpSession httpSession;
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    HttpRequestBase httpRequestBase;
    @Mock
    HttpResponse httpResponse;
    @Mock
    SlingHttpServletResponse slingHttpServletResponse;

    @Test
    void testConvertCookieNull() {
        assertThat(convertCookie(null), nullValue());
    }

    @Test
    void testConvertCookieNoDomain() {
        Cookie cookie = new Cookie("name", "value");
        assertThat(convertCookie(cookie), nullValue());
    }

    @Test
    void testConvertCookieWithDomain() {
        Cookie cookie = new Cookie("session", "abc");
        cookie.setDomain("example.com");
        cookie.setPath("/app");
        cookie.setSecure(true);
        cookie.setMaxAge(3600);

        ClientCookie result = convertCookie(cookie);

        assertThat(result, notNullValue());
        assertThat(result.getName(), is("session"));
        assertThat(result.getValue(), is("abc"));
        assertThat(result.getDomain(), is("example.com"));
        assertThat(result.getPath(), is("/app"));
        assertThat(result.isSecure(), is(true));
        assertThat(result.getExpiryDate(), notNullValue());
    }

    @Test
    void testCreateCookieStoreNullCookies() {
        CookieStore store = createCookieStore(null, "tr-1");
        assertThat(store.getCookies().isEmpty(), is(true));
    }

    @Test
    void testCreateCookieStoreWithCookies() {
        Cookie cookie = new Cookie("session", "abc");
        cookie.setDomain("example.com");
        Cookie noDomainCookie = new Cookie("other", "xyz");

        CookieStore store = createCookieStore(new Cookie[] {cookie, noDomainCookie}, "tr-1");

        assertThat(store.getCookies().size(), is(1));
        assertThat(store.getCookies().get(0).getName(), is("session"));
    }

    @Test
    void testGetTransactionIdNullRequest() {
        assertThat(getTransactionId(null), is(""));
    }

    @Test
    void testGetTransactionIdFromHeader() {
        when(httpServletRequest.getHeader("X-Request-Id")).thenReturn("header-id");
        assertThat(getTransactionId(httpServletRequest), is("header-id"));
    }

    @Test
    void testGetTransactionIdFallsBackToSession() {
        when(httpServletRequest.getHeader("X-Request-Id")).thenReturn(null);
        when(httpServletRequest.getSession()).thenReturn(httpSession);
        when(httpSession.getId()).thenReturn("session-id");

        assertThat(getTransactionId(httpServletRequest), is("session-id"));
    }

    @Test
    void testConvertParametersArraySingleValue() {
        Map<String, String[]> params = new HashMap<>();
        params.put("a", new String[] {"1"});

        Map<String, String> result = convertParametersArray(params);

        assertThat(result.get("a"), is("1"));
    }

    @Test
    void testConvertParametersArrayMultiValue() {
        Map<String, String[]> params = new HashMap<>();
        params.put("a", new String[] {"1", "2"});

        Map<String, String> result = convertParametersArray(params);

        assertThat(result.get("a"), is("1,2"));
    }

    @Test
    void testConvertParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("a", "1");

        var result = convertParameters(params);

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getName(), is("a"));
        assertThat(result.get(0).getValue(), is("1"));
    }

    @Test
    void testCopyHeadersNullRequest() {
        copyHeaders(null, httpRequestBase, Collections.emptyList());
        verify(httpRequestBase, never()).addHeader(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void testCopyHeadersFiltersBlacklisted() {
        Vector<String> headerNames = new Vector<>();
        headerNames.add("Content-Type");
        headerNames.add("X-Custom");
        when(slingHttpServletRequest.getHeaderNames()).thenReturn(headerNames.elements());
        when(slingHttpServletRequest.getHeader("X-Custom")).thenReturn("custom-value");

        copyHeaders(slingHttpServletRequest, httpRequestBase, java.util.List.of("Content-Type"));

        verify(httpRequestBase, times(1)).addHeader("X-Custom", "custom-value");
        verify(httpRequestBase, never()).addHeader(org.mockito.ArgumentMatchers.eq("Content-Type"), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void testCopyRequestHeadersSkipsBlacklisted() {
        HttpGet httpGet = new HttpGet();
        Vector<String> headerNames = new Vector<>();
        headerNames.add("Authorization");
        headerNames.add("X-Custom");
        when(slingHttpServletRequest.getHeaderNames()).thenReturn(headerNames.elements());
        when(slingHttpServletRequest.getHeader("X-Custom")).thenReturn("custom-value");

        copyRequestHeaders(slingHttpServletRequest, httpGet, Set.of("Authorization"));

        assertThat(httpGet.getFirstHeader("X-Custom").getValue(), is("custom-value"));
        assertThat(httpGet.getFirstHeader("Authorization"), nullValue());
    }

    @Test
    void testCopyResponseHeadersSkipsBlacklisted() {
        when(httpResponse.getAllHeaders()).thenReturn(new org.apache.http.Header[] {
                new BasicHeader("Set-Cookie", "a=b"),
                new BasicHeader("X-Custom", "value")
        });

        copyResponseHeaders(slingHttpServletResponse, httpResponse, new HashSet<>(java.util.List.of("Set-Cookie")));

        verify(slingHttpServletResponse, times(1)).addHeader("X-Custom", "value");
        verify(slingHttpServletResponse, never()).addHeader(org.mockito.ArgumentMatchers.eq("Set-Cookie"), org.mockito.ArgumentMatchers.anyString());
    }
}
