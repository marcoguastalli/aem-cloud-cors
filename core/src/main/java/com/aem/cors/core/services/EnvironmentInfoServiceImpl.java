package com.aem.cors.core.services;

import com.aem.cors.core.commonbeans.EnvironmentType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static com.aem.cors.core.CoreConstants.ENVIRONMENT_DEV;
import static com.aem.cors.core.CoreConstants.ENVIRONMENT_PROD;
import static com.aem.cors.core.CoreConstants.ENVIRONMENT_RDE;
import static com.aem.cors.core.CoreConstants.ENVIRONMENT_STAGE;
import static com.aem.cors.core.CoreConstants.LOCALHOST;
import static com.aem.cors.core.CoreConstants.OSGI_CONFIG_PREFIX;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Getter
@Slf4j
@Component(service = EnvironmentInfoService.class, immediate = true)
@Designate(ocd = EnvironmentInfoServiceImpl.Configuration.class)
public class EnvironmentInfoServiceImpl implements EnvironmentInfoService {

    @ObjectClassDefinition(name = OSGI_CONFIG_PREFIX + "Environment Info Service")
    public @interface Configuration {
        @AttributeDefinition(name = "Host", description = "Environment Host with protocol")
        String host() default "";

        @AttributeDefinition(name = "Hostname", description = "Environment Variable: HOSTNAME")
        String hostname() default "";

        @AttributeDefinition(name = "Environment Short Name", description = "Environment Variable: ENVIRONMENT_SHORT_NAME")
        String environmentShortName() default "";

        @AttributeDefinition(name = "Environment and Run Mode", description = "Environment and Run Mode as String")
        String environmentAndRunMode() default "";

        @AttributeDefinition(name = "Organization Id", description = "Environment Variable: ORGANIZATION_ID")
        String organizationId() default "";

        @AttributeDefinition(name = "Organization Secret", description = "Environment Variable: ORGANIZATION_SECRET (unused)")
        String organizationSecret() default "";

    }

    private String host;
    private String hostname;
    private String environmentShortName;
    private String environmentAndRunMode;
    private String organizationId;
    private String organizationSecret;

    @Activate
    protected void activate(final Configuration config) {
        this.host = config.host();
        this.hostname = config.hostname();
        this.environmentShortName = config.environmentShortName();
        this.environmentAndRunMode = config.environmentAndRunMode();
        this.organizationId = config.organizationId();
        this.organizationSecret = config.organizationSecret();
        log.info("For the current HOST: '{}'", getHost());
        log.info("Environment variable HOSTNAME is set with value: '{}'", getHostname());
        log.info("Environment variable ENVIRONMENT_SHORT_NAME is set with value: '{}'", getEnvironmentShortName());
        log.info("Environment variable ENVIRONMENT_AND_RUN_MODE is set with value: '{}'", getEnvironmentAndRunMode());
        log.info("Environment variable ORGANIZATION_ID is set with value: '{}'", getOrganizationId());
        log.info("Environment variable ORGANIZATION_SECRET is set with value: '{}'", getOrganizationSecret());
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public String getEnvironmentShortName() {
        return environmentShortName;
    }

    @Override
    public boolean isAuthor() {
        return environmentAndRunMode.contains(EnvironmentType.AUTHOR.name());
    }

    @Override
    public boolean isPublish() {
        return environmentAndRunMode.contains(EnvironmentType.PUBLISH.name());
    }

    @Override
    public boolean isProdPublish() {
        return EnvironmentType.PROD_PUBLISH.name().equals(environmentAndRunMode);
    }

    @Override
    public boolean isProd() {
        return EnvironmentType.PROD_AUTHOR.name().equals(environmentAndRunMode) || EnvironmentType.PROD_PUBLISH.name().equals(environmentAndRunMode);
    }

    @Override
    public boolean isStage() {
        return EnvironmentType.STAGE_AUTHOR.name().equals(environmentAndRunMode) || EnvironmentType.STAGE_PUBLISH.name().equals(environmentAndRunMode);
    }

    @Override
    public boolean isDev() {
        return EnvironmentType.DEV_AUTHOR.name().equals(environmentAndRunMode) || EnvironmentType.DEV_PUBLISH.name().equals(environmentAndRunMode);
    }

    @Override
    public boolean isRde() {
        return EnvironmentType.RDE_AUTHOR.name().equals(environmentAndRunMode) || EnvironmentType.RDE_PUBLISH.name().equals(environmentAndRunMode);
    }

    @Override
    public String getEnvironmentString() {
        if (this.isProd()) {
            return ENVIRONMENT_PROD;
        } else if (this.isStage()) {
            return ENVIRONMENT_STAGE;
        } else if (this.isDev()) {
            return ENVIRONMENT_DEV;
        } else if (this.isRde()) {
            return ENVIRONMENT_RDE;
        } else if (this.hostname.equals(LOCALHOST)) {
            return LOCALHOST;
        }
        return EMPTY;
    }
}
