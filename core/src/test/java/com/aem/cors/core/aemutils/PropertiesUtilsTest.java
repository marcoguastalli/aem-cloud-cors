package com.aem.cors.core.aemutils;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static com.aem.cors.core.aemutils.PropertiesUtils.getPropertiesValueIfExist;
import static com.aem.cors.core.aemutils.PropertiesUtils.getPropertyOrFallback;
import static com.aem.cors.core.aemutils.PropertiesUtils.resourceHasProperty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PropertiesUtilsTest {

    @Mock
    Resource resource;
    @Mock
    ValueMap valueMap;

    @Test
    void testResourceHasPropertyTrue() {
        when(resource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("title", String.class)).thenReturn("hello");

        assertThat(resourceHasProperty(resource, "title"), is(true));
    }

    @Test
    void testResourceHasPropertyFalse() {
        when(resource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("title", String.class)).thenReturn(null);

        assertThat(resourceHasProperty(resource, "title"), is(false));
    }

    @Test
    void testGetPropertyOrFallbackReturnsPrimary() {
        when(resource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("primary", String.class)).thenReturn("primaryValue");

        assertThat(getPropertyOrFallback(resource, "primary", "fallback"), is("primaryValue"));
    }

    @Test
    void testGetPropertyOrFallbackReturnsFallback() {
        when(resource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("primary", String.class)).thenReturn(null);
        when(valueMap.get("fallback", String.class)).thenReturn("fallbackValue");

        assertThat(getPropertyOrFallback(resource, "primary", "fallback"), is("fallbackValue"));
    }

    @Test
    void testGetPropertiesValueIfExistPresent() {
        when(valueMap.get("count", Integer.class)).thenReturn(5);
        assertThat(getPropertiesValueIfExist(valueMap, "count", Integer.class), is(Optional.of(5)));
    }

    @Test
    void testGetPropertiesValueIfExistMissing() {
        when(valueMap.get("count", Integer.class)).thenReturn(null);
        assertThat(getPropertiesValueIfExist(valueMap, "count", Integer.class), is(Optional.empty()));
    }
}
