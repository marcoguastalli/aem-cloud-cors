package com.aem.cors.core.services;

import com.aem.cors.core.exceptions.AemRuntimeException;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Service Factory that returns the ResourceResolver
 */
public interface SlingResourceResolverFactory {

    /**
     * @param userName defined in ui.config/src/main/content/jcr_root/apps/aemcors/osgiconfig/config/org.apache.sling.jcr.repoinit.RepositoryInitializere.config
     * @return the ResourceResolver
     * @throws AemRuntimeException
     */
    ResourceResolver getResourceResolver(String userName) throws AemRuntimeException;
}
