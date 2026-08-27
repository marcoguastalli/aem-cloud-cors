package com.aem.cors.core.aemutils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static com.aem.cors.core.aemutils.QueryManagerUtils.getOffset;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QueryManagerUtilsTest {

    @Mock
    SlingHttpServletRequest request;

    @Test
    void testGetOffsetNullRequest() {
        assertThat(getOffset(null, 10), is(0));
    }

    @Test
    void testGetOffsetNoPageParam() {
        when(request.getParameter("page")).thenReturn(null);
        assertThat(getOffset(request, 10), is(0));
    }

    @Test
    void testGetOffsetWithPageParam() {
        when(request.getParameter("page")).thenReturn("3");
        assertThat(getOffset(request, 10), is(20));
    }

    @Test
    void testGetOffsetFirstPage() {
        when(request.getParameter("page")).thenReturn("1");
        assertThat(getOffset(request, 10), is(0));
    }
}
