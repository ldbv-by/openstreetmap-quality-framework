package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto;

import java.nio.file.Path;

/**
 * Data transfer object for creating a new database schema with osm2pgsql.
 */
public record CreateSchemaDto(
        Path pbfPath,
        Path luaPath,
        String database,
        String databaseSchema,
        String databaseHost,
        String databasePort,
        String databaseUsername,
        String databasePassword
) {}
