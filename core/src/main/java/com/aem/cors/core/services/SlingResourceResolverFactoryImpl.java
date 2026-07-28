package com.aem.cors.core.services;

import com.aem.cors.core.exceptions.AemRuntimeException;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Collections;

import static java.lang.String.format;

@Component(service = SlingResourceResolverFactory.class, immediate = true)
public class SlingResourceResolverFactoryImpl implements SlingResourceResolverFactory {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public ResourceResolver getResourceResolver(String userName) throws AemRuntimeException {
        try {
            return resourceResolverFactory.getServiceResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, userName));
        } catch (LoginException e) {
            final String errorMessage = format("Error get ResourceResolver for userName '%s'", userName);
            throw new AemRuntimeException(errorMessage, e);
        }
    }
}
