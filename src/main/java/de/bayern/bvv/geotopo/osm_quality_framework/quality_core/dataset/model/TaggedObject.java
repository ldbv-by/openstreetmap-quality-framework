package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * Base object representing both geometric features and relations.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class TaggedObject {
    public static final String TAG_VALUE_SEPARATOR = ";";

    private Long osmId;
    private String objectType;
    private Map<String, String> tags = new HashMap<>();
    private List<Relation> relations = new ArrayList<>();
}
