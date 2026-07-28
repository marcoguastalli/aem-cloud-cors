package com.aem.cors.core.services;

/**
 * Service used to read the Environment Variables and expose them as info
 */
public interface EnvironmentInfoService {

    String getHost();

    String getHostname();

    String getEnvironmentShortName();

    String getEnvironmentAndRunMode();

    String getOrganizationId();

    boolean isAuthor();

    boolean isProdPublish();

    boolean isPublish();

    boolean isProd();

    boolean isStage();

    boolean isDev();

    boolean isRde();

    String getEnvironmentString();
}
