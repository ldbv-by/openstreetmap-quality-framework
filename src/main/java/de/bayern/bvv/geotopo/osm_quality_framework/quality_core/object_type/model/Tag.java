package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Object Type Tag.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    private String key;
    private Type type;
    private Multiplicity multiplicity;
    private Map<String, String> dictionary = new HashMap<>();
    private List<Tag> subTags = new ArrayList<>();

    public enum Type {
        PRIMITIVE,
        DICTIONARY,
        COMPLEX
    }
}
