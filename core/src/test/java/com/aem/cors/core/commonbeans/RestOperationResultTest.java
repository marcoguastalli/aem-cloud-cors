package com.aem.cors.core.commonbeans;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

class RestOperationResultTest {

    @Test
    void testConstructorWithString() {
        String result = "Success";
        RestOperationResult restOperationResult = new RestOperationResult(result);

        assertThat(restOperationResult, notNullValue());
        assertThat(restOperationResult.getResult(), is(result));
    }

    @Test
    void testConstructorWithObject() {
        Object result = new Object();
        RestOperationResult restOperationResult = new RestOperationResult(result);

        assertThat(restOperationResult, notNullValue());
        assertThat(restOperationResult.getResult(), is(result));
    }

    @Test
    void testSetResult() {
        RestOperationResult restOperationResult = new RestOperationResult("Initial");
        String newResult = "Updated";

        restOperationResult.setResult(newResult);

        assertThat(restOperationResult.getResult(), is(newResult));
    }

    @Test
    void testEqualsAndHashCode() {
        RestOperationResult result1 = new RestOperationResult("test");
        RestOperationResult result2 = new RestOperationResult("test");

        assertThat(result1.equals(result2), is(true));
        assertThat(result1.hashCode() == result2.hashCode(), is(true));
    }

    @Test
    void testSerialVersionUID() throws Exception {
        java.lang.reflect.Field field = RestOperationResult.class.getDeclaredField("serialVersionUID");
        field.setAccessible(true);
        long serialVersionUID = (long) field.get(null);
        assertThat(serialVersionUID, is(2048489816933242953L));
    }
}
