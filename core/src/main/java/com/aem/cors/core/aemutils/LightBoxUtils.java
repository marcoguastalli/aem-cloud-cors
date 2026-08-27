package com.aem.cors.core.aemutils;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;

public class LightBoxUtils {

    private static final String DOT_SEPARATOR = ".";
    private static final String HTML_SERVLET_EXTENSION = "html";
    private static final String SELECTOR_LIGHTBOX = "lightbox";
    private static final String SELECTOR_TITLE_LIGHTBOX = "lightboxtitle";
    private static final String TRUE = "true";

    // values of the 'target' property in the dialog/crx
    private static final String TARGET_LIGHTBOX_FULLWIDTH = "lightbox-fullwidth";
    private static final String TARGET_LIGHTBOX_FULLWIDTH_TITLE = "lightbox-fullwidth-title";
    private static final String TARGET_LIGHTBOX_SMALL = "lightbox-small";
    private static final String TARGET_LIGHTBOX_SMALL_TITLE = "lightbox-small-title";
    // html data-attributes
    private static final String DATA_LIGHTBOX_URL = "data-lightbox-url";
    private static final String DATA_LIGHTBOX = "data-lightbox";

    private static final String DATA_LIGHTBOX_VARIANT = "data-lightbox-variant";

    private static final String WIDTH_SMALL = "small";
    private static final String WIDTH_FULL_WIDTH = "fullwidth";

    private LightBoxUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** Create a Map with the keys:
     *
     * - 'data-lightbox-url' attribute is used as href - 'data-lightbox-variant' attribute is used to define the width - 'data-lightbox'
     * attributes tell to the js that the link its a lightbox
     *
     * @param resourceResolver the ResourceResolver
     * @param link field value selected in the dialog
     * @param target option selected in the dialog
     * @return a Map<String, String> */
    public static Map<String, String> createDataLightboxMap(ResourceResolver resourceResolver, final String link, final String target) {
        Map<String, String> result = new HashMap<>();
        switch (target) {
        case TARGET_LIGHTBOX_FULLWIDTH:
            result.put(DATA_LIGHTBOX_URL,
                    resourceResolver.map(link) + DOT_SEPARATOR + SELECTOR_LIGHTBOX + DOT_SEPARATOR + HTML_SERVLET_EXTENSION);
            result.put(DATA_LIGHTBOX_VARIANT, WIDTH_FULL_WIDTH);
            break;
        case TARGET_LIGHTBOX_SMALL:
            result.put(DATA_LIGHTBOX_URL,
                    resourceResolver.map(link) + DOT_SEPARATOR + SELECTOR_LIGHTBOX + DOT_SEPARATOR + HTML_SERVLET_EXTENSION);
            result.put(DATA_LIGHTBOX_VARIANT, WIDTH_SMALL);
            break;
        case TARGET_LIGHTBOX_FULLWIDTH_TITLE:
            result.put(DATA_LIGHTBOX_URL,
                    resourceResolver.map(link) + DOT_SEPARATOR + SELECTOR_TITLE_LIGHTBOX + DOT_SEPARATOR + HTML_SERVLET_EXTENSION);
            result.put(DATA_LIGHTBOX_VARIANT, WIDTH_FULL_WIDTH);
            break;
        case TARGET_LIGHTBOX_SMALL_TITLE:
            result.put(DATA_LIGHTBOX_URL,
                    resourceResolver.map(link) + DOT_SEPARATOR + SELECTOR_TITLE_LIGHTBOX + DOT_SEPARATOR + HTML_SERVLET_EXTENSION);
            result.put(DATA_LIGHTBOX_VARIANT, WIDTH_SMALL);
            break;
        }

        // the 'data-lightbox' attributes tell to the js that the link its a lightbox
        result.put(DATA_LIGHTBOX, TRUE);

        return result;
    }

}
