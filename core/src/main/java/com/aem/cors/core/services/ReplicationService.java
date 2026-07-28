package com.aem.cors.core.services;

import com.day.cq.replication.ReplicationException;

/**
 * Service class for Replicator
 */
public interface ReplicationService {

    /**
     * Publish the input path using Replicator
     *
     * @param path to publish
     * @throws ReplicationException is the operation fails
     */
    void publishPath(String path) throws ReplicationException;

    /**
     * Unpublish the input path using Replicator
     *
     * @param path to unpublish
     * @throws ReplicationException is the operation fails
     */
    void unPublishPath(String path) throws ReplicationException;
}
