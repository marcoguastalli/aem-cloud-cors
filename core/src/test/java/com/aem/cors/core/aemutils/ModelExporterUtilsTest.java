package com.aem.cors.core.aemutils;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.SlingModelFilter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;

import static com.aem.cors.core.aemutils.ModelExporterUtils.getModelExporterJsonMap;
import static com.aem.cors.core.aemutils.ModelExporterUtils.getModelFromRequest;
import static com.aem.cors.core.aemutils.ModelExporterUtils.getResourceModelExporterJsonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ModelExporterUtilsTest {

    interface FakeModel {
    }

    @Mock
    SlingModelFilter slingModelFilter;
    @Mock
    ModelFactory modelFactory;
    @Mock
    Resource resource;
    @Mock
    Resource child;
    @Mock
    SlingHttpServletRequest request;
    @Mock
    FakeModel model;

    @Test
    void testGetModelExporterJsonMap() {
        when(resource.getChildren()).thenReturn(List.of(child));
        when(slingModelFilter.filterChildResources(List.of(child))).thenReturn(List.of(child));
        when(child.getName()).thenReturn("childName");
        when(modelFactory.getModelFromWrappedRequest(request, child, FakeModel.class)).thenReturn(model);

        Map<String, FakeModel> result = getModelExporterJsonMap(slingModelFilter, modelFactory, resource, request, FakeModel.class);

        assertThat(result.get("childName"), is(model));
    }

    @Test
    void testGetModelExporterJsonMapSkipsNullModels() {
        when(resource.getChildren()).thenReturn(List.of(child));
        when(slingModelFilter.filterChildResources(List.of(child))).thenReturn(List.of(child));
        when(modelFactory.getModelFromWrappedRequest(request, child, FakeModel.class)).thenReturn(null);

        Map<String, FakeModel> result = getModelExporterJsonMap(slingModelFilter, modelFactory, resource, request, FakeModel.class);

        assertThat(result.isEmpty(), is(true));
    }

    @Test
    void testGetModelFromRequest() {
        when(modelFactory.getModelFromWrappedRequest(request, resource, FakeModel.class)).thenReturn(model);
        assertThat(getModelFromRequest(modelFactory, resource, request, FakeModel.class), is(model));
    }

    @Test
    void testGetResourceModelExporterJsonMap() {
        when(resource.adaptTo(FakeModel.class)).thenReturn(model);
        when(resource.getName()).thenReturn("resourceName");

        Map<String, FakeModel> result = getResourceModelExporterJsonMap(resource, FakeModel.class);

        assertThat(result.get("resourceName"), is(model));
    }

    @Test
    void testGetResourceModelExporterJsonMapNullAdapt() {
        when(resource.adaptTo(FakeModel.class)).thenReturn(null);

        Map<String, FakeModel> result = getResourceModelExporterJsonMap(resource, FakeModel.class);

        assertThat(result.isEmpty(), is(true));
    }
}
