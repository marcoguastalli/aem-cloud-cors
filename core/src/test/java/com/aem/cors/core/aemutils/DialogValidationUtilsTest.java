package com.aem.cors.core.aemutils;

import org.junit.jupiter.api.Test;

import static com.aem.cors.core.aemutils.DialogValidationUtils.unescapeDialogAction;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class DialogValidationUtilsTest {

    @Test
    void testUnescapeDialogAction() {
        String action = "http://localhost:7000/content/mysite/en/_jcr_content/parsys/teaser";
        assertThat(unescapeDialogAction(action), is("http://localhost:7000/content/mysite/en/jcr:content/parsys/teaser"));
    }

    @Test
    void testUnescapeDialogActionNoMatch() {
        String action = "http://localhost:7000/content/mysite/en/jcr:content/parsys/teaser";
        assertThat(unescapeDialogAction(action), is(action));
    }
}
