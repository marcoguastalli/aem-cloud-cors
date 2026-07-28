package com.aem.cors.core.services;

import com.aem.cors.core.AppAemContext;
import com.day.cq.replication.Replicator;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.aem.cors.core.CoreConstants.SERVICE_USER_CONTENT_ALL_SERVICE_USER_NAME;
import static com.aem.cors.core.CoreConstantsTest.PATH_CONTENT_EN_HOME;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ReplicationServiceImplTest {

    AemContext aemContext = AppAemContext.newAemContext();
    @Mock
    SlingResourceResolverFactory slingResourceResolverFactory;
    @Mock
    Replicator replicator;
    @InjectMocks
    ReplicationServiceImpl replicationService;

    @BeforeEach
    void init() {
        when(slingResourceResolverFactory.getResourceResolver(SERVICE_USER_CONTENT_ALL_SERVICE_USER_NAME)).thenReturn(aemContext.resourceResolver());
    }

    @Test
    void testPublishPath() {
        assertThat(replicationService, notNullValue());
        replicationService.unPublishPath(PATH_CONTENT_EN_HOME);
    }

    @Test
    void testUnPublishPath() {
        assertThat(replicationService, notNullValue());
        replicationService.unPublishPath(PATH_CONTENT_EN_HOME);
    }
}
