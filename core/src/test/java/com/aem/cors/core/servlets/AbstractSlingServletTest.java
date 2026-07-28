package com.aem.cors.core.servlets;

import com.aem.cors.core.exceptions.AemRuntimeException;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(AemContextExtension.class)
class AbstractSlingServletTest {

    private static class TestSlingServlet extends AbstractSlingServlet {
    }

    @Test
    void testWriteJsonObject(AemContext context) throws Exception {
        TestSlingServlet servlet = new TestSlingServlet();
        MockSlingHttpServletRequest request = context.request();
        MockSlingHttpServletResponse response = context.response();

        String json = "{\"result\":\"success\"}";
        servlet.writeJsonObject(response, "tracking123", HttpServletResponse.SC_OK, json, "max-age=3600");

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentType(), containsString("application/json"));
        assertThat(response.getOutputAsString(), is(json));
    }

    @Test
    void testWriteJsonObjectWithNoCacheHeader(AemContext context) throws Exception {
        TestSlingServlet servlet = new TestSlingServlet();
        MockSlingHttpServletRequest request = context.request();
        MockSlingHttpServletResponse response = context.response();

        String json = "{\"error\":\"bad request\"}";
        servlet.writeJsonObject(response, "tracking456", HttpServletResponse.SC_BAD_REQUEST, json, "no-cache");

        assertThat(response.getStatus(), is(400));
        assertThat(response.getContentType(), containsString("application/json"));
        assertThat(response.getOutputAsString(), is(json));
    }

    @Test
    void testWriteErrorJsonObject(AemContext context) throws Exception {
        TestSlingServlet servlet = new TestSlingServlet();
        MockSlingHttpServletRequest request = context.request();
        MockSlingHttpServletResponse response = context.response();

        servlet.writeErrorJsonObject(response, "tracking789", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        assertThat(response.getStatus(), is(500));
        assertThat(response.getContentType(), containsString("application/json"));
        assertThat(response.getOutputAsString(), containsString("error"));
    }

    @Test
    void testWriteJsonObjectThrowsException(AemContext context) {
        TestSlingServlet servlet = new TestSlingServlet();
        MockSlingHttpServletRequest request = context.request();
        MockSlingHttpServletResponse response = context.response();

        String json = null;

        assertThrows(AemRuntimeException.class, () -> {
            servlet.writeJsonObject(response, "tracking", HttpServletResponse.SC_OK, json, "no-cache");
        });
    }

    @Test
    void testGetIdFromSessionEmpty(AemContext context) {
        MockSlingHttpServletRequest request = context.request();
        String sessionId = AbstractSlingServlet.getIdFromSession(request);

        assertThat(sessionId, is(""));
    }

    @Test
    void testGetIdFromSessionWithValidSession(AemContext context) {
        MockSlingHttpServletRequest request = context.request();
        request.getSession();

        String sessionId = AbstractSlingServlet.getIdFromSession(request);

        assertThat(sessionId, is(request.getSession().getId()));
    }
}
