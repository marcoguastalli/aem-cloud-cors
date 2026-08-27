package com.aem.cors.core.aemutils;

import org.apache.commons.lang3.StringUtils;

import com.day.cq.commons.jcr.JcrConstants;

public final class DialogValidationUtils {

    private static final String SUBMITTED_JCR_CONTENT = "_jcr_content";

    private DialogValidationUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /** When a component dialog form is submitted for validation, the jcr:content is escaped
     *
     * This method replace '_jcr_content' with 'jcr:content'
     *
     * @param dialogAction something like:
     *            http://localhost:7000/content/mysite/en/_jcr_content/parsys/teasercolumncontrol_/parsys/teaserrow/parsys/referencecomponent
     *
     * @return something like:
     *         http://localhost:7000/content/mysite/en/jcr:content/parsys/teasercolumncontrol_/parsys/teaserrow/parsys/referencecomponent
     *
     *         No exception are treated */
    public static String unescapeDialogAction(final String dialogAction) {
        return StringUtils.replaceFirst(dialogAction, SUBMITTED_JCR_CONTENT, JcrConstants.JCR_CONTENT);
    }

}
