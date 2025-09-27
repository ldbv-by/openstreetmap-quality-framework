package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto;

import java.nio.file.Path;
import java.util.List;

/**
 * Data transfer object for sorting an osm change file with osm2pgsql.
 */
public record SortOscDto(
        List<Path> oscPaths
) {}
