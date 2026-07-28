package com.aem.cors.core.utils;

import com.aem.cors.core.commonbeans.RestOperationResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static com.aem.cors.core.CoreConstantsTest.JSON_A_STRING;
import static com.aem.cors.core.CoreConstantsTest.JUST_A_STRING;
import static com.aem.cors.core.utils.JsonUtils.createJsonStringFromObject;
import static com.aem.cors.core.utils.JsonUtils.createObjectFromJsonString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

class JsonUtilsTest {

    @Test
    void testCreateJsonStringFromObject() {
        assertThat(createJsonStringFromObject(createRestOperationResult()), is(JSON_A_STRING));
    }

    Serializable createRestOperationResult() {
        return new RestOperationResult(JUST_A_STRING);
    }

    @Test
    void testCreateObjectFromJsonString() {
        JsonUtilsTestBean jsonUtilsTestBean = createObjectFromJsonString(JSON_A_STRING, JsonUtilsTestBean.class);
        assertThat(jsonUtilsTestBean, notNullValue());
        assertThat(jsonUtilsTestBean.result, notNullValue());
        assertThat(jsonUtilsTestBean.result, is(JUST_A_STRING));
    }

    static final class JsonUtilsTestBean implements Serializable {
        @JsonProperty
        String result;
    }
}
