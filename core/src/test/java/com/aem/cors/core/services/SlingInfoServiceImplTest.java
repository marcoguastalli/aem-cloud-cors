package com.aem.cors.core.services;

import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SlingInfoServiceImplTest {

    static final String SLING_ID = "0416b3d5-99ba-4971-b88e-0e12af981d09";
    static final String SLING_URL = "/Users/aemcors/opt/aem-servers/publish/crx-quickstart";
    static final String SLING_NAME = "Instance 0416b3d5-99ba-4971-b88e-0e12af981d09";
    static final String SLING_DESCRIPTION = "Instance with id 0416b3d5-99ba-4971-b88e-0e12af981d09 and run modes [s7connect, crx3, author, sdk, live, crx3tar]";
    static final Set<String> SLING_RUNMODES = Set.of("[s7connect, crx3, author, sdk, live, crx3tar]");

    @Mock
    SlingSettingsService slingSettingsService;
    @InjectMocks
    SlingInfoServiceImpl slingInfoService;

    @Test
    void testGetters() {
        //when
        when(slingInfoService.getSlingId()).thenReturn(SLING_ID);
        when(slingInfoService.getSlingUrl()).thenReturn(SLING_URL);
        when(slingInfoService.getSlingName()).thenReturn(SLING_NAME);
        when(slingInfoService.getSlingDescription()).thenReturn(SLING_DESCRIPTION);
        when(slingInfoService.getRunModes()).thenReturn(SLING_RUNMODES);
        //then
        assertThat(slingSettingsService, notNullValue());
        assertThat(slingInfoService.getSlingId(), is(SLING_ID));
        assertThat(slingInfoService.getSlingUrl(), is(SLING_URL));
        assertThat(slingInfoService.getSlingName(), is(SLING_NAME));
        assertThat(slingInfoService.getSlingDescription(), is(SLING_DESCRIPTION));
        assertThat(slingInfoService.getRunModes(), is(SLING_RUNMODES));
    }

    @Test
    void testNullBecauseURLisFinalClass() {
        assertThrows(NullPointerException.class, () -> {
            slingInfoService.getSlingHome();
        });
    }
}
