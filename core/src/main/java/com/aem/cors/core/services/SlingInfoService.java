package com.aem.cors.core.services;

import java.util.Set;

/**
 * Service providing sling properties
 */
public interface SlingInfoService {

    String getSlingId();

    String getSlingHome();

    String getSlingUrl();

    String getSlingName();

    String getSlingDescription();

    Set<String> getRunModes();
}
