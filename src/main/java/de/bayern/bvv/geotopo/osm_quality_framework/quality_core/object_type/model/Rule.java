package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model;

import tools.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representing a rule for an object type.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Rule {
    private String id;
    private String type;
    private JsonNode expression;
    private String errorText;
}
