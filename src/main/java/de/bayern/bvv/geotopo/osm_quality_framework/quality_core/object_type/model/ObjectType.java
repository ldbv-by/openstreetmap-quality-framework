package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Object Type.
 */
@Data
public class ObjectType {
    private String name;
    private List<Tag> tags = new ArrayList<>();
}
