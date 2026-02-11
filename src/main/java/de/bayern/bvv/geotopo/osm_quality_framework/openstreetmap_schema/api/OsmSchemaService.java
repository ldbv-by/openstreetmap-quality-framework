package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.api;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.ObjectTypeDto;

import java.util.List;

/**
 * Public API of the OpenStreetMap-Schema bounded context.
 * <p>
 * Represents the institutional target schema (e.g. GeoInfoDok of the AdV) that defines
 * how OSM features must be mapped and represented in the OSM database according to
 * officially mandated modelling rules.
 * <p>
 * The schema acts as the authoritative specification for classification
 * of OSM object types and provides the semantic validation rules required by the Rule Engine.
 */
public interface OsmSchemaService {

    /**
     * Retrieves the schema metadata and rule set for a specific OSM object type.
     */
    ObjectTypeDto getObjectTypeInfo(String objectType);

    /**
     * Retrieves all schema metadata and rule set.
     */
    List<ObjectTypeDto> getObjectTypes(boolean flattingTags, boolean withRules);

}
