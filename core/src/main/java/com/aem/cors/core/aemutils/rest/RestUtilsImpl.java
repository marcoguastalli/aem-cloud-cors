package com.aem.cors.core.aemutils.rest;

import static com.aem.cors.core.aemutils.HttpUtilsNeo.getTransactionId;
import static com.aem.cors.core.aemutils.HttpUtilsNeo.logRequest;
import static com.aem.cors.core.aemutils.HttpUtilsNeo.logRequestHeader;
import static com.aem.cors.core.utils.LoggerUtilsNeo.logErrorTrId;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.cors.core.aemutils.HttpUtilsNeo;
import com.aem.cors.core.aemutils.RestUtils;
import com.aem.cors.core.utils.rest.RestResponse;
import com.aem.cors.core.utils.rest.exception.RestRequestException;

@Component(service = RestUtils.class, immediate = true)
public class RestUtilsImpl implements RestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestUtilsImpl.class);

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String FILE_SEPARATOR = "/";
    private static final String HTTP_HEADER_AUTHORIZATION = "Authorization";
    private static final String QUESTION = "?";
    private static final String UTF8_ENCODING = "UTF-8";

    public static final String REQ_HEADER_REMOTE_ADDR = "remote_addr";
    public static final String RES_HEADER_LOCATION = "location";

    /** The timeout used by the HTTP client */
    private static final int HTTP_CLIENT_TIMEOUT = (int) TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);

    @Reference
    private HttpClientBuilderFactory httpClientBuilderFactory;

    @Override
    public List<BasicNameValuePair> createParamsFromMap(Map<String, String> map) {
        List<BasicNameValuePair> result = new LinkedList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            result.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    /** Build a url concatenating the input parameters adding the slashes if missing
     *
     * @param baseUrl normally the host
     * @param endpoint normally the uri
     * @param params the GET parameters
     * @return a String with the url */
    public String buildUrl(final String baseUrl, final String endpoint, final List<BasicNameValuePair> params) {
        if (StringUtils.isBlank(baseUrl)) {
            return null;
        }
        if (StringUtils.isBlank(endpoint)) {
            if (CollectionUtils.isNotEmpty(params)) {
                return baseUrl.concat(QUESTION).concat(URLEncodedUtils.format(params, UTF8_ENCODING));
            }
            return baseUrl;
        }

        String url;
        if (baseUrl.endsWith(FILE_SEPARATOR) && endpoint.startsWith(FILE_SEPARATOR)) {
            url = StringUtils.removeEnd(baseUrl, FILE_SEPARATOR).concat(endpoint);
        } else if (!baseUrl.endsWith(FILE_SEPARATOR) && !endpoint.startsWith(FILE_SEPARATOR)) {
            url = StringUtils.join(Arrays.asList(baseUrl, endpoint), FILE_SEPARATOR);
        } else {
            url = baseUrl.concat(endpoint);
        }

        if (CollectionUtils.isNotEmpty(params)) {
            url = url.concat(QUESTION).concat(URLEncodedUtils.format(params, UTF8_ENCODING));
        }
        return url;
    }

    /** Create a HttpGet (subclass of HttpRequestBase) Apache HttpClient GET Request object
     *
     * @param baseUrl something like 'http://localhost:8080'
     * @param endpoint something like '/rest/service/endpoint'
     * @param params the parameters that will be added at the request
     * @return a HttpGet object with the request */
    public HttpGet createGetRequestNoHeaders(final String baseUrl, final String endpoint, final List<BasicNameValuePair> params) {
        return new HttpGet(buildUrl(baseUrl, endpoint, params));
    }

    /** Create a HttpGet (subclass of HttpRequestBase) Apache HttpClient GET Request object
     *
     * @param baseUrl something like 'http://localhost:8080'
     * @param endpoint something like '/rest/service/endpoint'
     * @param secure true if the request will be secure, false instead
     * @param params the parameters that will be added at the request
     * @param basicAuthEnabled if the authentication is required
     * @param basicAuthUsername the user used for the authentication
     * @param basicAuthPassword the password used for the authentication
     * @return a HttpGet object with the request */
    public HttpGet createGetRequest(final String baseUrl, final String endpoint, boolean secure, final List<BasicNameValuePair> params,
            boolean basicAuthEnabled, final String basicAuthUsername, final String basicAuthPassword) {
        final String url = buildUrl(baseUrl, endpoint, params);
        HttpGet request = new HttpGet(url);
        setHeaders(request, secure, basicAuthEnabled, basicAuthUsername, basicAuthPassword);
        return request;
    }

    /** Create a HttpGet (subclass of HttpRequestBase) Apache HttpClient GET Request object
     *
     * This method will copy the currentRequest Headers to the new request
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
    public HttpGet createGetRequestCopyingCurrentRequestHeaders(final SlingHttpServletRequest currentRequest, final String baseUrl,
            final String endpoint, final boolean secure, List<BasicNameValuePair> params, final boolean basicAuthEnabled,
            final String basicAuthUsername, final String basicAuthPassword) {
        HttpGet newRequest = createGetRequest(baseUrl, endpoint, secure, params, basicAuthEnabled, basicAuthUsername, basicAuthPassword);
        HttpUtilsNeo.copyHeaders(currentRequest, newRequest, HttpUtilsNeo.FILTERED_HEADERS);
        return newRequest;
    }

    /** Set the JSON headers at the input request
     *
     * @param request the request
     * @param secure true or false
     * @param basicAuthEnabled true or false
     * @param basicAuthUsername user
     * @param basicAuthPassword password */
    private void setHeaders(final HttpRequestBase request, boolean secure, boolean basicAuthEnabled,
            final String basicAuthUsername, final String basicAuthPassword) {
        request.setHeader(HttpHeaders.ACCEPT, CONTENT_TYPE_JSON);
        request.setHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON);
        if (secure && basicAuthEnabled) {
            addAuthentication(request, basicAuthUsername, basicAuthPassword);
        }
    }

    /** Add authentication at the input request
     *
     * @param request the request
     * @param basicAuthUsername user
     * @param basicAuthPassword password */
    private void addAuthentication(final HttpRequestBase request, final String basicAuthUsername, final String basicAuthPassword) {
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(basicAuthUsername, basicAuthPassword);
        try {
            LOGGER.debug("Add basic authentication to request [url:{}, user:'{}'].", request.getURI(), basicAuthUsername);
            request.addHeader(new BasicScheme().authenticate(credentials, request, null));
        } catch (final AuthenticationException e) {
            LOGGER.error("Unable to do Basic authentication [url: " + request.getURI() + ", username: " + basicAuthUsername + "]", e);
        }
    }

    /** Execute the Request
     *
     * @param slingRequest the original request
     * @param httpRequestBase the request to execute
     * @return a String with response, null instead */
    public String sendRequest(final SlingHttpServletRequest slingRequest, final HttpRequestBase httpRequestBase) {
        try (final CloseableHttpClient httpClient = getClient(slingRequest);
                final CloseableHttpResponse response = httpClient.execute(httpRequestBase)) {
            final RestResponse restResponse = sendRequestReturnRestResponse(httpRequestBase);
            logNotFound(restResponse, httpRequestBase.getURI().toString());
            return restResponse != null ? restResponse.getResponse() : null;
        } catch (final Exception e) {
            final String transactionId = getTransactionId(slingRequest);
            logErrorTrId(LOGGER, transactionId, "Error create HTTP connection", e);
            logRequest(LOGGER, transactionId, slingRequest);
            logRequestHeader(LOGGER, transactionId, slingRequest, HTTP_HEADER_AUTHORIZATION);
        }
        return null;
    }

    /** Execute the Request
     *
     * @param httpRequestBase the request to execute
     * @return a RestResponse instance object with response,, or null */
    public RestResponse sendRequestReturnRestResponse(final HttpRequestBase httpRequestBase) {
        final HttpClientBuilder httpClientBuilder = httpClientBuilderFactory.newBuilder();
        httpClientBuilder.setDefaultRequestConfig(createRequestConfiguration());
        try (final CloseableHttpClient closeableHttpClient = httpClientBuilder.build()) {
            return processResponse(closeableHttpClient.execute(httpRequestBase));
        } catch (Exception e) {
            LOGGER.error("Error sendRequest", e);
        }
        return null;
    }

    /** Execute the Request
     *
     * @param httpRequestBase the request to execute
     * @return a InputStream object with response, or null */
    @Override
    public InputStream sendRequestReturnInputStream(final HttpRequestBase httpRequestBase) {
        final HttpClientBuilder httpClientBuilder = httpClientBuilderFactory.newBuilder();
        httpClientBuilder.setDefaultRequestConfig(createRequestConfiguration());
        CloseableHttpResponse response = null;
        try (final CloseableHttpClient closeableHttpClient = httpClientBuilder.build()) {
            response = closeableHttpClient.execute(httpRequestBase);
            final StatusLine statusLine = response.getStatusLine();
            if (statusLine == null) {
                LOGGER.error("Status line is 'null'. Unable to process response from Page Rating REST service");
                return null;
            }

            final int statusCode = statusLine.getStatusCode();
            LOGGER.debug("Status code: '{}'", statusCode);

            switch (statusCode) {
            case SlingHttpServletResponse.SC_OK:
                final HttpEntity httpEntity = response.getEntity();
                return httpEntity.getContent();
            case SlingHttpServletResponse.SC_UNAUTHORIZED:
                LOGGER.error("Unable to parse response, user could not be authenticated (401).");
                break;
            case SlingHttpServletResponse.SC_NOT_FOUND:
                LOGGER.error("Unable to parse response, not found (404).");
                break;
            case SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR:
                LOGGER.error("Unable to parse response, internal server error (500).");
                break;
            default:
                LOGGER.error("Unable to parse response, unknown error. HTTP status: {}", statusCode);
                break;
            }
        } catch (Exception e) {
            LOGGER.error("Error send Request", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOGGER.error("Error close Response ", e);
            }
        }
        return null;
    }

    /** Get the Client IP address
     *
     * @param request the request of the client
     * @return a String with the IP address */
    public String getIpAddress(final SlingHttpServletRequest request) {
        return request.getHeader(REQ_HEADER_REMOTE_ADDR);
    }

    /** Get the Client IP address from the InetAddress object
     *
     * @param request the request of the client
     * @return a String with the IP address */
    public String getInetAddress(final SlingHttpServletRequest request) throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /** Given a request that contains a json, returns it as string
     *
     * @param request the json request
     * @return a string with the json content */
    public String getJsonStringFromRequest(final SlingHttpServletRequest request) throws RestRequestException {
        try (Scanner scanner = new Scanner(request.getInputStream()).useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : EMPTY;
        } catch (Exception e) {
            throw new RestRequestException("Error get json from request", e);
        }
    }

    /** Create an HTTP client object
     *
     * @return a CloseableHttpClient */
    public CloseableHttpClient createHttpClient() {
        RequestConfig.Builder requestConfig = RequestConfig.custom();
        requestConfig.setConnectTimeout(HTTP_CLIENT_TIMEOUT);
        requestConfig.setConnectionRequestTimeout(HTTP_CLIENT_TIMEOUT);
        HttpClientBuilder httpClientBuilder = this.httpClientBuilderFactory.newBuilder();
        httpClientBuilder.setDefaultRequestConfig(requestConfig.build());
        return httpClientBuilder.build();
    }

    private CloseableHttpClient getClient(final SlingHttpServletRequest slingRequest) {
        final String transactionId = getTransactionId(slingRequest);

        LOGGER.debug("Started creating HttpClient. trID: '{}'.", transactionId);

        final HttpClientBuilder builder = this.httpClientBuilderFactory.newBuilder();
        builder.setDefaultRequestConfig(this.createRequestConfiguration());

        // Add all of the cookies of the current request to the http client that is used for the REST service request.
        final Cookie[] currentRequestCookies = slingRequest.getCookies();
        final CookieStore cookieStore = HttpUtilsNeo.createCookieStore(currentRequestCookies, transactionId);
        builder.setDefaultCookieStore(cookieStore);

        LOGGER.debug("Finished creating HttpClient. trID: '{}'.", transactionId);

        return builder.build();
    }

    private RequestConfig createRequestConfiguration() {
        return RequestConfig.custom()
                .setConnectTimeout(HTTP_CLIENT_TIMEOUT)
                .setConnectionRequestTimeout(HTTP_CLIENT_TIMEOUT)
                .build();
    }

    private RestResponse processResponse(final CloseableHttpResponse response) throws IOException {
        final StatusLine statusLine = response.getStatusLine();
        if (statusLine == null) {
            LOGGER.error("Status line is 'null'. Unable to process response from Page Rating REST service");
            return null;
        }

        final int statusCode = statusLine.getStatusCode();
        LOGGER.info("Status code: '{}'", statusCode);

        switch (statusCode) {
        case SlingHttpServletResponse.SC_OK:
            return new RestResponse(statusCode, readResponse(response));
        case SlingHttpServletResponse.SC_MOVED_PERMANENTLY:
        case SlingHttpServletResponse.SC_MOVED_TEMPORARILY:
        case SlingHttpServletResponse.SC_SEE_OTHER:
        case SlingHttpServletResponse.SC_NOT_MODIFIED:
        case SlingHttpServletResponse.SC_USE_PROXY:
        case SlingHttpServletResponse.SC_TEMPORARY_REDIRECT:
            final Header header = response.getFirstHeader(RES_HEADER_LOCATION);
            final String location = header != null ? header.getValue() : EMPTY;
            LOGGER.info("Redirect (301 to 307) to location '{}'", location);
            return new RestResponse(statusCode, location);
        case SlingHttpServletResponse.SC_BAD_REQUEST:
            LOGGER.error("Bad Request (400).");
            break;
        case SlingHttpServletResponse.SC_UNAUTHORIZED:
            LOGGER.error("Unable to parse response, user could not be authenticated (401).");
            break;
        case SlingHttpServletResponse.SC_NOT_FOUND:
            LOGGER.error("Unable to parse response, not found (404).");
            break;
        case SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR:
            LOGGER.error("Unable to parse response, internal server error (500).");
            break;
        default:
            LOGGER.error("Unable to parse response, unknown error. HTTP status: {}", statusCode);
            break;
        }

        return new RestResponse(statusCode, null);
    }

    private String readResponse(final CloseableHttpResponse response) throws IOException {
        final HttpEntity entity = response.getEntity();
        if (entity == null) {
            LOGGER.error("Response does not contain an entity");
            return null;
        }

        final String responseAsString = EntityUtils.toString(entity);
        LOGGER.debug("Response as String: '{}'", responseAsString);

        if (StringUtils.isBlank(responseAsString)) {
            LOGGER.error("The Response is empty");
            return null;
        }

        return responseAsString;
    }

    private void logNotFound(final RestResponse restResponse, final String uri) {
        if (restResponse != null && restResponse.getResponseCode() == SlingHttpServletResponse.SC_NOT_FOUND) {
            LOGGER.error(String.format("URL 404: %s", uri));
        }
    }

}
