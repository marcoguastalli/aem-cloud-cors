package com.aem.cors.core.utils.rest.domain;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class MapRestResponse implements Serializable {

    private static final long serialVersionUID = -5006759129080416558L;
    private final Map<String, String> map;

    public MapRestResponse(Map<String, String> map) {
        this.map = map;
    }

    public Map<String, String> getMap() {
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MapRestResponse that = (MapRestResponse) o;
        return map.equals(that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }

    @Override
    public String toString() {
        return "MapRestResponse{" +
            "map=" + map +
            '}';
    }
}
