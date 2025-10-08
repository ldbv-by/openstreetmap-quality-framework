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
    public static final String SUBTYPE_SEPARATOR = ":";

    private String key;
    private Type type;
    private Multiplicity multiplicity;
    private Map<String, String> dictionary = new HashMap<>();
    private List<Tag> subTags = new ArrayList<>();

    /**
     * Check if tag is required.
     */
    public boolean isRequired() {
        return this.multiplicity != null && this.multiplicity.min() > 0;
    }

    public enum Type {
        PRIMITIVE,
        DICTIONARY,
        COMPLEX
    }
}
