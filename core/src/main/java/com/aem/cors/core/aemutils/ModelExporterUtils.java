package com.aem.cors.core.aemutils;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.SlingModelFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.aem.cors.core.aemutils.JsonJacksonUtils.createJsonStringFromObject;
import static com.aem.cors.core.aemutils.JsonJacksonUtils.createJsonStringFromObjectIgnoreNull;

public class ModelExporterUtils {

    private ModelExporterUtils() {
        throw new UnsupportedOperationException("Do not instantiate Util class");
    }

    /**
     * @param slingModelFilter used to filter the resources
     * @param modelFactory     used to adaptTo
     * @param resource         the resource
     * @param request          the request
     * @param modelClass       model class to adapt the Resource
     * @param <T>              Java generics T
     * @return Map of String, T object
     */
    public static <T> Map<String, T> getModelExporterJsonMap(@NotNull SlingModelFilter slingModelFilter, @NotNull ModelFactory modelFactory, @NotNull Resource resource, @NotNull SlingHttpServletRequest request, @NotNull Class<T> modelClass) {
        Map<String, T> models = new LinkedHashMap<>();
        for (Resource child : slingModelFilter.filterChildResources(resource.getChildren())) {
            T model = modelFactory.getModelFromWrappedRequest(request, child, modelClass);
            if (model != null) {
                models.put(child.getName(), model);
            }
        }
        return models;
    }

    /**
     * @param modelFactory used to adaptTo
     * @param resource     the resource
     * @param request      the request
     * @param modelClass   model class to adapt the Resource
     * @param <T>          Java generics T
     * @return Modal adapted from modelClass
     */
    public static <T> T getModelFromRequest(@NotNull ModelFactory modelFactory, @NotNull Resource resource, @NotNull SlingHttpServletRequest request, @NotNull Class<T> modelClass) {
        return modelFactory.getModelFromWrappedRequest(request, resource, modelClass);
    }

    /**
     * @param resource   the resource
     * @param modelClass model class to adapt the Resource
     * @param <T>        Java generics T
     * @return Map of String, T object
     */
    public static <T> Map<String, T> getResourceModelExporterJsonMap(@NotNull Resource resource, @NotNull Class<T> modelClass) {
        Map<String, T> models = new LinkedHashMap<>();
        T model = resource.adaptTo(modelClass);
        if (model != null) {
            models.put(resource.getName(), model);
        }
        return models;
    }

    /**
     * @param slingModelFilter used to filter the resources
     * @param modelFactory     used to adaptTo
     * @param resource         the resource
     * @param request          the request
     * @return String with a json
     */
    public static String getJsonFromResourceWithRequest(@NotNull SlingModelFilter slingModelFilter, @NotNull ModelFactory modelFactory, @NotNull Resource resource, @NotNull SlingHttpServletRequest request) throws JsonProcessingException {
        Map<String, ComponentExporter> modelExporterJsonMap = getModelExporterJsonMap(slingModelFilter, modelFactory, resource.getParent(), request, ComponentExporter.class);
        return createJsonStringFromObjectIgnoreNull((Serializable) modelExporterJsonMap);
    }

    /**
     * @param modelFactory used to adaptTo
     * @param resource     the resource
     * @param request      the request
     * @return ComponentExporter as String
     * @throws JsonProcessingException when transform object to json fails
     */
    public static String getJsonFromSingleResource(@NotNull ModelFactory modelFactory, @NotNull Resource resource, @NotNull SlingHttpServletRequest request) throws JsonProcessingException {
        ComponentExporter model = getModelFromRequest(modelFactory, resource, request, ComponentExporter.class);
        return createJsonStringFromObject(model);
    }

    /**
     * @param resource the resource
     * @return String with a json
     */
    public static String getJsonFromResource(@NotNull Resource resource) throws JsonProcessingException {
        Map<String, ComponentExporter> modelExporterJsonMap = getResourceModelExporterJsonMap(resource, ComponentExporter.class);
        return createJsonStringFromObjectIgnoreNull((Serializable) modelExporterJsonMap);
    }

}
