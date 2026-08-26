package com.aem.cors.core.servlets;


import com.aem.cors.core.commonbeans.RestOperationResult;
import com.aem.cors.core.services.EnvironmentInfoService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.aem.cors.core.CoreConstants.EQUALS;
import static com.aem.cors.core.CoreConstants.HTTP_HEADER_NO_CACHE;
import static com.aem.cors.core.utils.JsonUtils.createJsonStringFromObject;
import static java.lang.String.format;
import static org.apache.sling.api.servlets.HttpConstants.METHOD_GET;
import static org.apache.sling.api.servlets.HttpConstants.METHOD_POST;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 * Check the OSGi config "Apache Sling Servlet/Script Resolver and Error Handler": org.apache.sling.servlets.resolver.SlingServletResolver for paths
 */
@Component(service = {Servlet.class}, property = {
        SLING_SERVLET_PATHS + EQUALS + "/bin/aemcors/core-bundle-health-check",
        SLING_SERVLET_METHODS + EQUALS + METHOD_GET,
        SLING_SERVLET_METHODS + EQUALS + METHOD_POST
})
public class CoreBundleServlet extends AbstractSlingServlet {

    private static final long serialVersionUID = 3852741802400046405L;

    @Reference
    private transient EnvironmentInfoService environmentInfoService;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest slingHttpServletRequest,
                         @NotNull SlingHttpServletResponse slingHttpServletResponse) throws IOException {
        doService(slingHttpServletRequest, slingHttpServletResponse);
    }

    @Override
    protected void doPost(@NotNull SlingHttpServletRequest slingHttpServletRequest,
                          @NotNull SlingHttpServletResponse slingHttpServletResponse) throws IOException {
        doService(slingHttpServletRequest, slingHttpServletResponse);
    }

    private void doService(@NotNull SlingHttpServletRequest slingHttpServletRequest, @NotNull SlingHttpServletResponse slingHttpServletResponse) throws IOException {
        // get the trackingId from the request
        final String trackingId = getIdFromSession(slingHttpServletRequest);
        // prepare result
        final String result = format("The bundle 'aem-cloud-cors.core' is up and active in '%s' with ORGANIZATION_ID: '%s'",
                environmentInfoService.getEnvironmentAndRunMode(),
                environmentInfoService.getOrganizationId());
        // prepare output
        final RestOperationResult restOperationResult = new RestOperationResult(result);
        final String json = createJsonStringFromObject(restOperationResult);
        if (null != json) {
            writeJsonObject(slingHttpServletResponse, trackingId, HttpServletResponse.SC_OK, json, HTTP_HEADER_NO_CACHE);
        } else {
            writeErrorJsonObject(slingHttpServletResponse, trackingId, HttpServletResponse.SC_BAD_REQUEST);
        }

    }
}
