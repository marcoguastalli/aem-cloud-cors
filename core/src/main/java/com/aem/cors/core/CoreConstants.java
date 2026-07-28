package com.aem.cors.core;

public class CoreConstants {

    private CoreConstants() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    // environments
    public static final String LOCALHOST = "localhost";
    public static final String ENVIRONMENT_PROD = "prod";
    public static final String ENVIRONMENT_STAGE = "stage";
    public static final String ENVIRONMENT_DEV = "dev";
    public static final String ENVIRONMENT_RDE = "rde";

    public static final String OSGI_CONFIG_PREFIX = "AEM CORS ::";
    public static final String SERVICE_USER_CONTENT_READONLY_SERVICE_USER_NAME = "content-readonly";
    public static final String SERVICE_USER_CONTENT_DAM_READONLY_SERVICE_USER_NAME = "content-dam-readonly";
    public static final String SERVICE_USER_CONTENT_ALL_SERVICE_USER_NAME = "content-all-readwrite";

    public static final String HTTP_HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HTTP_HEADER_DISPATCHER = "Dispatcher";
    public static final String HTTP_HEADER_NO_CACHE = "no-cache";
    public static final String HTTP_HEADER_NO_CACHE_NO_STORE_MAX_AGE_ZERO = "no-cache, no-store, max-age=0";
    public static final String HTTP_HEADER_UUID = "X-UUID";

    // json responses
    public static final String JSON_ERROR_OBJECT_DEFAULT = "{\"error\": \"an error occurs, check the logs for more details\"}";
    public static final String JSON_ERROR_OBJECT = "{\"error\": \"%s\"}";

    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

    public static final String DOT = ".";
    public static final String EQUALS = "=";
    public static final String H1 = "h1";
    public static final String HASH = "#";
    public static final String HTML_EXTENSION = "html";
    public static final String JSON_EXTENSION = "json";
    public static final String QUESTION = "?";
    public static final String SLASH = "/";


}
