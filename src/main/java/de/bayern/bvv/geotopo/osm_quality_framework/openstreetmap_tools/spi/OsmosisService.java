package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.spi;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.CommandResponseDto;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.SortOscDto;

/**
 * Service Provider Interface (SPI) for Osmosis.
 */
public interface OsmosisService {

    /**
     * Sorts OSM change files.
     */
    CommandResponseDto sortOscFiles(SortOscDto sortOscDto);
}
