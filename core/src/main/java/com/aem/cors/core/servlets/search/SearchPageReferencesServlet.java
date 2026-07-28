package com.aem.cors.core.servlets.search;

import com.aem.cors.core.commonbeans.PathsBean;
import com.aem.cors.core.commonbeans.search.SearchPageReferencesBean;
import com.aem.cors.core.commonbeans.search.SearchResultPage;
import com.aem.cors.core.servlets.AbstractSlingServlet;
import com.day.cq.wcm.commons.ReferenceSearch;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aem.cors.core.CoreConstants.HTTP_HEADER_NO_CACHE_NO_STORE_MAX_AGE_ZERO;
import static com.aem.cors.core.CoreConstants.JSON_EXTENSION;
import static com.aem.cors.core.utils.HttpUtils.getTrackingId;
import static com.aem.cors.core.utils.JsonUtils.createJsonStringFromObject;
import static com.aem.cors.core.utils.JsonUtils.getObjectFromRequest;
import static org.apache.sling.api.servlets.HttpConstants.METHOD_POST;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_EXTENSIONS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 * This Servlet read an input json like:
 * <p>
 * {"paths":["/content/core-components-examples"]}
 * <p>
 * The create a new instance of com.day.cq.wcm.commons.ReferenceSearch calling the search() method providing the input paths
 * <p>
 * The search returns a Map<String, ReferenceSearch.Info> searchResult containing the input path corresponding page references
 * <p>
 * The Map returnes is parsed and a new filtered json of SearchResultPage objects is returned
 */
@Slf4j
@Component(service = Servlet.class, property = {
        SLING_SERVLET_PATHS + "=" + "/bin/aemcors/search/pagereferences",
        SLING_SERVLET_METHODS + "=" + METHOD_POST,
        SLING_SERVLET_EXTENSIONS + "=" + JSON_EXTENSION}
)
public class SearchPageReferencesServlet extends AbstractSlingServlet {
    private static final long serialVersionUID = 4134399900103300868L;

    private static final int LIMIT = 1000;
    private static final int OFFSET = 1;

    @Override
    protected void doPost(@NotNull SlingHttpServletRequest slingHttpServletRequest,
                          @NotNull SlingHttpServletResponse slingHttpServletResponse) throws IOException {
        // get the trackingId from the request
        final String trackingId = getTrackingId(slingHttpServletRequest);
        // check input json
        final PathsBean pathsBean = getObjectFromRequest(slingHttpServletRequest, trackingId, PathsBean.class);
        if (null != pathsBean) {
            // prepare result
            Map<String, SearchResultPage> references = new HashMap<>();
            // prepare search
            final ReferenceSearch referenceSearch = new ReferenceSearch();
            // search
            final List<String> paths = pathsBean.getPaths();
            for (final String path : paths) {
                final Map<String, ReferenceSearch.Info> searchResult = referenceSearch.search(slingHttpServletRequest.getResourceResolver(), path, LIMIT, OFFSET);
                for (final Map.Entry<String, ReferenceSearch.Info> searchResultItem : searchResult.entrySet()) {
                    // the ReferenceSearch.Info cannot be directly mapped to a json
                    final ReferenceSearch.Info info = searchResultItem.getValue();
                    // the SearchResultPage is a simplification of ReferenceSearch.Info
                    final SearchResultPage searchResultPage = SearchResultPage.builder()
                            .pagePath(info.getPagePath())
                            .pageTitle(info.getPageTitle())
                            .properties(info.getProperties())
                            .build();
                    references.put(searchResultItem.getKey(), searchResultPage);
                }
            }
            // return result
            final String json = createJsonStringFromObject(new SearchPageReferencesBean(references));
            if (null != json) {
                writeJsonObject(slingHttpServletResponse, trackingId, HttpServletResponse.SC_OK, json, HTTP_HEADER_NO_CACHE_NO_STORE_MAX_AGE_ZERO);
                return;
            }
        }
        writeErrorJsonObject(slingHttpServletResponse, trackingId, HttpServletResponse.SC_BAD_REQUEST);
    }
}
