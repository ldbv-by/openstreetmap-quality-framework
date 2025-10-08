package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Data Transfer Object of a rule for an object type.
 */
public record RuleDto(
        String id,
        String type,
        JsonNode expression,
        String errorText
) {}