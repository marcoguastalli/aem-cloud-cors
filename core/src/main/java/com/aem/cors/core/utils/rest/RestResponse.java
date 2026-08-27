package com.aem.cors.core.utils.rest;

/** Model class representing a Rest Response */
public class RestResponse {
    private int responseCode;
    private String response;

    public RestResponse(final int responseCode, final String response) {
        this.responseCode = responseCode;
        this.response = response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponse() {
        return response;
    }
}
