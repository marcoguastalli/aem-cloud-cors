package com.aem.cors.core.services;

import com.aem.cors.core.exceptions.AemRuntimeException;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;

import static com.aem.cors.core.CoreConstants.SERVICE_USER_CONTENT_ALL_SERVICE_USER_NAME;
import static java.lang.String.format;

@Slf4j
@Component(service = ReplicationService.class)
public class ReplicationServiceImpl implements ReplicationService {

    @Reference
    private transient SlingResourceResolverFactory slingResourceResolverFactory;
    @Reference
    private transient Replicator replicator;

    @Override
    public void publishPath(@NotNull String path) throws AemRuntimeException {
        try (final ResourceResolver resourceResolver = this.getResourceResolver()) {
            final Session session = resourceResolver.adaptTo(Session.class);
            replicator.replicate(session, ReplicationActionType.ACTIVATE, path, new ReplicationOptions());
            log.debug("Publish path: {}", path);
        } catch (ReplicationException e) {
            final String errorMessage = format("Error publish path: '%s'", path);
            log.error(errorMessage, e);
            throw new AemRuntimeException(errorMessage, e);
        }
    }

    @Override
    public void unPublishPath(@NotNull String path) throws AemRuntimeException {
        try (final ResourceResolver resourceResolver = this.getResourceResolver()) {
            final Session session = resourceResolver.adaptTo(Session.class);
            replicator.replicate(session, ReplicationActionType.DEACTIVATE, path, new ReplicationOptions());
            log.debug("Unpublish path: {}", path);
        } catch (ReplicationException e) {
            final String errorMessage = format("Error unpublish path: '%s'", path);
            log.error(errorMessage, e);
            throw new AemRuntimeException(errorMessage, e);
        }
    }

    private ResourceResolver getResourceResolver() {
        return slingResourceResolverFactory.getResourceResolver(SERVICE_USER_CONTENT_ALL_SERVICE_USER_NAME);
    }
}
