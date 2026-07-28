package com.aem.cors.core.commonbeans.search;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * Bean used to return the result of a Search operation
 */
@Data
@Builder
public class SearchResultPage implements Serializable {

    private static final long serialVersionUID = -4149456733299899699L;

    private String pagePath;
    private String pageTitle;
    private Set<String> properties;
}
