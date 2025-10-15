package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.api;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.AppendChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.CommandResponseDto;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.CreateSchemaDto;

/**
 * Service Provider Interface (SPI) for Osm2PgSql.
 */
public interface Osm2PgSqlService {

    /**
     * Creates a new schema with Osm2PgSql.
     */
    CommandResponseDto createSchema(CreateSchemaDto createSchemaDto);

    /**
     * Appends a changeset to existing database schema.
     * Changeset will be automatically sorted.
     */
    CommandResponseDto appendChangeset(AppendChangesetDto appendChangesetDto);
}
