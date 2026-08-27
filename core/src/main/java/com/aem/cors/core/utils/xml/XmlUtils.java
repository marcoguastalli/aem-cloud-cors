package com.aem.cors.core.utils.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.aem.cors.core.exceptions.AemRuntimeException;

/** Util class for XML */
public class XmlUtils {

    private XmlUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** Given an inputStream parse its content as xml, returning an org.w3c.dom.Document
     *
     * @param inputStream the xml inputStream
     * @return the xml Document */
    public static Document parseXml(final InputStream inputStream) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            return documentBuilder.parse(inputStream);
        } catch (ParserConfigurationException e) {
            throw new AemRuntimeException("The XML parser is not well configured", e);
        } catch (IOException e) {
            throw new AemRuntimeException("The XML file cannot be read", e);
        } catch (SAXException e) {
            throw new AemRuntimeException("The XML is not well-formed", e);
        }
    }

}
