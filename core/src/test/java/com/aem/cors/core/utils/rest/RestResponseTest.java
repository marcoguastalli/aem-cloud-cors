package com.aem.cors.core.utils.rest;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class RestResponseTest {

    @Test
    void testGetters() {
        RestResponse response = new RestResponse(200, "{\"ok\":true}");
        assertThat(response.getResponseCode(), is(200));
        assertThat(response.getResponse(), is("{\"ok\":true}"));
    }
}
