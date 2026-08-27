package com.aem.cors.core.utils.xml;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.aem.cors.core.utils.xml.XmlUtils.parseXml;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.aem.cors.core.exceptions.AemRuntimeException;

class XmlUtilsTest {

    @Test
    void testParseXml() {
        InputStream inputStream = new ByteArrayInputStream(
                "<root><child>value</child></root>".getBytes(StandardCharsets.UTF_8));

        Document document = parseXml(inputStream);

        assertThat(document, notNullValue());
        assertThat(document.getDocumentElement().getTagName(), is("root"));
    }

    @Test
    void testParseXmlMalformedThrows() {
        InputStream inputStream = new ByteArrayInputStream(
                "<root><child></root>".getBytes(StandardCharsets.UTF_8));

        assertThrows(AemRuntimeException.class, () -> parseXml(inputStream));
    }
}
