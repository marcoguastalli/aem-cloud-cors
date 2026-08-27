package com.aem.cors.core.aemutils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

@Slf4j
public class QueryManagerUtils {

    private static final String REQ_PARAM_PAGE = "page";

    private QueryManagerUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /**
     * @param request  request to get parameter
     * @param pageSize limit of number to display
     * @return offset
     */
    public static int getOffset(SlingHttpServletRequest request, int pageSize) {
        String pageParam = request != null ? request.getParameter(REQ_PARAM_PAGE) : null;
        if (StringUtils.isNotEmpty(pageParam)) {
            return pageSize * (Integer.parseInt(pageParam) - 1);
        }
        return 0;
    }

}
