package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto;

import java.nio.file.Path;

/**
 * Data transfer object for appending a changeset.
 */
public record AppendChangesetDto(
        Path oscPath,
        Path luaPath,
        String database,
        String databaseSchema,
        String databaseHost,
        String databasePort,
        String databaseUsername,
        String databasePassword
) {}
