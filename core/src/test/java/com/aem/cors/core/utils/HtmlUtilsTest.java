package com.aem.cors.core.utils;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.aem.cors.core.utils.HtmlUtils.replaceHyphenMinus;
import static com.aem.cors.core.utils.HtmlUtils.replaceInvalidApostrophes;
import static com.aem.cors.core.utils.HtmlUtils.replaceLineBreakWithHtml;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class HtmlUtilsTest {

    @Test
    void testReplaceHyphenMinus() {
        assertThat(replaceHyphenMinus("well-known"), is("well‐known"));
    }

    @Test
    void testReplaceInvalidApostrophes() {
        assertThat(replaceInvalidApostrophes("it’s a `test´"), is("it's a 'test'"));
    }

    @Test
    void testReplaceLineBreakWithHtmlCrlf() {
        Optional<String> result = replaceLineBreakWithHtml("line1\r\nline2");
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is("line1<br/>line2"));
    }

    @Test
    void testReplaceLineBreakWithHtmlLineFeed() {
        Optional<String> result = replaceLineBreakWithHtml("line1\nline2");
        assertThat(result.get(), is("line1<br/>line2"));
    }

    @Test
    void testReplaceLineBreakWithHtmlNull() {
        assertThat(replaceLineBreakWithHtml(null).isPresent(), is(false));
    }

    @Test
    void testReplaceLineBreakWithHtmlBlank() {
        assertThat(replaceLineBreakWithHtml("   ").isPresent(), is(false));
    }
}
