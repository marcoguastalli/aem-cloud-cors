package com.aem.cors.core.services;


import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import java.util.Set;

import static com.aem.cors.core.CoreConstants.OSGI_CONFIG_PREFIX;

/**
 * Service providing sling properties
 */
@Component(service = SlingInfoService.class)
@ServiceDescription(OSGI_CONFIG_PREFIX + "Sling Info Service")
public class SlingInfoServiceImpl implements SlingInfoService {

    @Reference
    private SlingSettingsService slingSettingsService;

    @Override
    public String getSlingId() {
        return slingSettingsService.getSlingId();
    }

    @Override
    public String getSlingHome() {
        return slingSettingsService.getSlingHome().toString();
    }

    @Override
    public String getSlingUrl() {
        return slingSettingsService.getSlingHomePath();
    }

    @Override
    public String getSlingName() {
        return slingSettingsService.getSlingName();
    }

    @Override
    public String getSlingDescription() {
        return slingSettingsService.getSlingDescription();
    }

    @Override
    public Set<String> getRunModes() {
        return slingSettingsService.getRunModes();
    }
}
