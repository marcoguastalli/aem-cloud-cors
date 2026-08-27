package com.aem.cors.core.aemutils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.aem.cors.core.aemutils.JsonJacksonUtils.createInstanceFromReader;
import static com.aem.cors.core.aemutils.JsonJacksonUtils.createJsonArrayNodeFromEntries;
import static com.aem.cors.core.aemutils.JsonJacksonUtils.createJsonNodeFromJsonString;
import static com.aem.cors.core.aemutils.JsonJacksonUtils.createJsonObjectNodeFromEntries;
import static com.aem.cors.core.aemutils.JsonJacksonUtils.createJsonStringFromList;
import static com.aem.cors.core.aemutils.JsonJacksonUtils.createJsonStringFromObject;
import static com.aem.cors.core.aemutils.JsonJacksonUtils.createJsonStringFromObjectIgnoreNull;
import static com.aem.cors.core.aemutils.JsonJacksonUtils.createListFromJsonString;
import static com.aem.cors.core.aemutils.JsonJacksonUtils.createObject;
import static com.aem.cors.core.aemutils.JsonJacksonUtils.getJsonNodeFromString;
import static com.aem.cors.core.aemutils.JsonJacksonUtils.getObjectFromInputStream;
import static com.aem.cors.core.aemutils.JsonJacksonUtils.getObjectFromRequest;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JsonJacksonUtilsTest {

    static class SampleBean implements java.io.Serializable {
        public String name;

        public SampleBean() {
        }

        public SampleBean(String name) {
            this.name = name;
        }
    }

    // JsonJacksonUtils uses a single shared static ObjectMapper; Jackson caches the serializer it
    // builds for a given class on first use, so a type already serialized with default (ALWAYS)
    // inclusion by another test can ignore a later setSerializationInclusion(NON_NULL) call. Using
    // a distinct, never-before-serialized type here keeps this test independent of method order.
    static class NullableFieldBean implements java.io.Serializable {
        public String name;

        public NullableFieldBean(String name) {
            this.name = name;
        }
    }

    @Mock
    SlingHttpServletRequest request;

    private static ServletInputStream toServletInputStream(String content) {
        final ByteArrayInputStream delegate = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
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
    void testCreateJsonStringFromObjectSerializable() {
        assertThat(createJsonStringFromObject((java.io.Serializable) new SampleBean("foo")), containsString("\"name\":\"foo\""));
    }

    @Test
    void testCreateJsonStringFromObjectPlainObject() {
        assertThat(createJsonStringFromObject((Object) new SampleBean("foo")), containsString("\"name\":\"foo\""));
    }

    @Test
    void testCreateJsonStringFromObjectIgnoreNull() {
        assertThat(createJsonStringFromObjectIgnoreNull(new NullableFieldBean(null)), is("{}"));
    }

    @Test
    void testCreateJsonStringFromList() {
        String json = createJsonStringFromList(List.of("a", "b"));
        assertThat(json, is("[\"a\",\"b\"]"));
    }

    @Test
    void testCreateObjectFromJsonStringString() {
        assertThat(com.aem.cors.core.aemutils.JsonJacksonUtils.createObjectFromJsonString("hello", String.class), is("hello"));
    }

    @Test
    void testCreateObjectFromJsonStringBean() {
        SampleBean bean = com.aem.cors.core.aemutils.JsonJacksonUtils.createObjectFromJsonString("{\"name\":\"foo\"}", SampleBean.class);
        assertThat(bean.name, is("foo"));
    }

    @Test
    void testCreateJsonObjectNodeFromEntries() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("a", "1");
        ObjectNode node = createJsonObjectNodeFromEntries(map.entrySet());
        assertThat(node.get("a").asText(), is("1"));
    }

    @Test
    void testCreateJsonArrayNodeFromEntries() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("a", "1");
        var arrayNode = createJsonArrayNodeFromEntries(map.entrySet());
        assertThat(arrayNode.size(), is(1));
        assertThat(arrayNode.get(0).get("a").asText(), is("1"));
    }

    @Test
    void testGetObjectFromRequestSuccess() throws IOException {
        when(request.getInputStream()).thenReturn(toServletInputStream("{\"name\":\"foo\"}"));

        SampleBean result = getObjectFromRequest(request, "tr-1", SampleBean.class);
        assertThat(result.name, is("foo"));
    }

    @Test
    void testGetObjectFromRequestInvalidJsonReturnsNull() throws IOException {
        when(request.getInputStream()).thenReturn(toServletInputStream("not json"));

        assertThat(getObjectFromRequest(request, "tr-1", SampleBean.class), nullValue());
    }

    @Test
    void testGetObjectFromInputStream() {
        InputStream inputStream = new ByteArrayInputStream("{\"name\":\"foo\"}".getBytes(StandardCharsets.UTF_8));
        SampleBean result = getObjectFromInputStream(inputStream, "tr-1", SampleBean.class);
        assertThat(result.name, is("foo"));
    }

    @Test
    void testGetJsonNodeFromString() {
        JsonNode node = getJsonNodeFromString("{\"a\":1}", "tr-1");
        assertThat(node.get("a").asInt(), is(1));
    }

    @Test
    void testGetJsonNodeFromStringInvalid() {
        assertThat(getJsonNodeFromString("not json", "tr-1"), nullValue());
    }

    @Test
    void testCreateListFromJsonString() {
        List<SampleBean> result = createListFromJsonString("[{\"name\":\"a\"},{\"name\":\"b\"}]", "tr-1", SampleBean.class);
        assertThat(result.size(), is(2));
        assertThat(result.get(0).name, is("a"));
    }

    @Test
    void testCreateListFromJsonStringInvalid() {
        assertThat(createListFromJsonString("not json", "tr-1", SampleBean.class), nullValue());
    }

    @Test
    void testCreateObject() {
        SampleBean result = createObject("tr-1", SampleBean.class, new SampleBean("foo"));
        assertThat(result.name, is("foo"));
    }

    @Test
    void testCreateJsonNodeFromJsonString() {
        JsonNode node = createJsonNodeFromJsonString("{\"a\":1}");
        assertThat(node, notNullValue());
        assertThat(node.get("a").asInt(), is(1));
    }

    @Test
    void testCreateJsonNodeFromJsonStringInvalid() {
        assertThat(createJsonNodeFromJsonString("not json"), nullValue());
    }

    @Test
    void testCreateInstanceFromReader() {
        SampleBean result = createInstanceFromReader(new StringReader("{\"name\":\"foo\"}"), SampleBean.class);
        assertThat(result.name, is("foo"));
    }
}
