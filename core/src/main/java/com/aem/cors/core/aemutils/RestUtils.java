package com.aem.cors.core.aemutils;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.sling.api.SlingHttpServletRequest;

import com.aem.cors.core.utils.rest.RestResponse;
import com.aem.cors.core.utils.rest.exception.RestRequestException;

public interface RestUtils {

    /** Given the input map creates a List of BasicNameValuePair
     *
     * @param map with the params
     * @return a List of BasicNameValuePair */
    List<BasicNameValuePair> createParamsFromMap(Map<String, String> map);

    /** Build a url concatenating the input parameters adding the slashes if missing
     *
     * @param baseUrl normally the host
     * @param endpoint normally the uri
     * @param params the GET parameters
     * @return a String with the url */
    String buildUrl(String baseUrl, String endpoint, List<BasicNameValuePair> params);

    /** Create a HttpGet (subclass of HttpRequestBase) Apache HttpClient GET Request object
     *
     * @param baseUrl something like 'http://localhost:8080'
     * @param endpoint something like '/rest/service/endpoint'
     * @param params the parameters that will be added at the request
     * @return a HttpGet object with the request */
    HttpGet createGetRequestNoHeaders(String baseUrl, String endpoint, List<BasicNameValuePair> params);

    /** Create a HttpGet (subclass of HttpRequestBase) Apache HttpClient GET Request object
     *
     * @param baseUrl something like 'http://localhost:8080'
     * @param endpoint something like '/rest/service/endpoint'
     * @param params the parameters that will be added at the request
     * @return a HttpGet object with the request */
    HttpGet createGetRequest(String baseUrl, String endpoint, boolean secure, List<BasicNameValuePair> params,
            boolean basicAuthEnabled, String basicAuthUsername, String basicAuthPassword);

    /** Create a HttpGet (subclass of HttpRequestBase) Apache HttpClient GET Request object
     *
     * This method will copy the currentRequest Headers to the new request, as required when
     * proxying a REST call on behalf of the original caller.
     *
     * @param currentRequest the currentRequest
     * @param baseUrl something like 'http://localhost:8080'
     * @param endpoint something like '/rest/service/endpoint'
     * @param secure true if the request will be secure, false instead
     * @param params the parameters that will be added at the request
     * @param basicAuthEnabled if the authentication is required
     * @param basicAuthUsername the user used for the authentication
     * @param basicAuthPassword the password used for the authentication
     * @return a HttpGet object with the request */
    HttpGet createGetRequestCopyingCurrentRequestHeaders(SlingHttpServletRequest currentRequest, String baseUrl,
            String endpoint, boolean secure, List<BasicNameValuePair> params, boolean basicAuthEnabled,
            String basicAuthUsername, String basicAuthPassword);

    /** Execute the Request
     *
     * @param slingRequest the original request
     * @param httpRequestBase the request to execute
     * @return a String with response, null instead */
    String sendRequest(SlingHttpServletRequest slingRequest, HttpRequestBase httpRequestBase);

    /** Execute the Request
     *
     * @param httpRequestBase the request to execute
     * @return a RestResponse instance object with response, null instead */
    RestResponse sendRequestReturnRestResponse(HttpRequestBase httpRequestBase);

    /** Execute the Request
     *
     * @param httpRequestBase the request to execute
     * @return a InputStream object with response, or null */
    InputStream sendRequestReturnInputStream(HttpRequestBase httpRequestBase);

    /** Get the Client IP address
     *
     * @param request the request of the client
     * @return a String with the IP address */
    String getIpAddress(SlingHttpServletRequest request);

    /** Get the Client IP address from the InetAddress object
     *
     * @param request the request of the client
     * @return a String with the IP address */
    String getInetAddress(SlingHttpServletRequest request) throws UnknownHostException;

    /** Given a request that contains a json, returns it as string
     *
     * @param request the json request
     * @return a string with the json content */
    String getJsonStringFromRequest(SlingHttpServletRequest request) throws RestRequestException;

    /** Create an HTTP client object
     *
     * @return a CloseableHttpClient */
    CloseableHttpClient createHttpClient();

}
