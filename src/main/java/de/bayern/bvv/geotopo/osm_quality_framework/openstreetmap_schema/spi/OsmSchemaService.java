package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.spi;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.ObjectTypeDto;

/**
 * Service Provider Interface (SPI) for OpenStreetMap schema.
 */
public interface OsmSchemaService {

    /**
     * Get Object Type Info.
     */
    ObjectTypeDto getObjectTypeInfo(String objectType);
}
