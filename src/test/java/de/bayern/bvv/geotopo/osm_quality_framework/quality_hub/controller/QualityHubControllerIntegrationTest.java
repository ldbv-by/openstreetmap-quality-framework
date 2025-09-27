package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.controller;

import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QualityHubControllerIntegrationTest extends DatabaseIntegrationTest {

    @Test
    void testIntegrationTestEnvironment() throws Exception {
        Boolean openstreetmapGeometriesSchemaExists = this.jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM information_schema.schemata WHERE schema_name = ?)",
                Boolean.class, "openstreetmap_geometries"
        );

        assertEquals(Boolean.TRUE, openstreetmapGeometriesSchemaExists, "Schema openstreetmap_geometries not exists.");
    }
}