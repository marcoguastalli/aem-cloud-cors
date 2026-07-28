package com.aem.cors.core.models.components.slinginfo;

import com.aem.cors.core.commonbeans.EnvironmentType;
import com.aem.cors.core.services.EnvironmentInfoService;
import com.aem.cors.core.services.SlingInfoService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.EMPTY;


/**
 * Model Class for Sling Info Component
 */
@Model(
        adaptables = {Resource.class, SlingHttpServletRequest.class},
        resourceType = SlingInfoModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class SlingInfoModel {

    public static final String RESOURCE_TYPE = "aemcors/components/slinginfo";
    public static final String REQ_PARAMETER = "param";

    @SlingObject
    private Resource resource;
    @SlingObject
    private SlingHttpServletRequest slingHttpServletRequest;
    @OSGiService
    private SlingInfoService slingInfoService;
    @OSGiService
    private EnvironmentInfoService environmentInfoService;

    private String param;

    @PostConstruct
    void init() {
        param = slingHttpServletRequest.getParameter(REQ_PARAMETER) != null ? slingHttpServletRequest.getParameter(REQ_PARAMETER)
                : EMPTY;
    }

    public String getParam() {
        return param;
    }

    public String getSlingId() {
        return slingInfoService.getSlingId();
    }

    public String getSlingHome() {
        return slingInfoService.getSlingHome();
    }

    public String getSlingUrl() {
        return slingInfoService.getSlingUrl();
    }

    public String getSlingName() {
        return slingInfoService.getSlingName();
    }

    public String getSlingDescription() {
        return slingInfoService.getSlingDescription();
    }

    public Set<String> getRunModes() {
        return slingInfoService.getRunModes();
    }

    public boolean isAuthor() {
        return environmentInfoService.getEnvironmentAndRunMode().contains(EnvironmentType.AUTHOR.name());
    }

    public boolean isPublish() {
        return environmentInfoService.getEnvironmentAndRunMode().contains(EnvironmentType.PUBLISH.name());
    }
}
