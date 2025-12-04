package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.component;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.api.Osm2PgSqlService;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.AppendChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.util.ChangesetXml;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Client to call osm2pgsql from openstreetmap_tools module.
 */
@Component("qualityHubOsm2PgSqlClient")
@RequiredArgsConstructor
public class Osm2PgSqlClient {

    private final Osm2PgSqlService osm2PgSqlService;

    @Value("${OSM_LUA_FILE:classpath:lua/openstreetmap_geometries.lua}")
    private Resource osmLuaFile;

    @Value("${OSM_QUALITY_FRAMEWORK_DATABASE}")          private String database;
    @Value("${OSM_QUALITY_FRAMEWORK_DATABASE_HOST}")     private String databaseHost;
    @Value("${OSM_QUALITY_FRAMEWORK_DATABASE_PORT}")     private String databasePort;
    @Value("${OSM_QUALITY_FRAMEWORK_DATABASE_USERNAME}") private String databaseUsername;
    @Value("${OSM_QUALITY_FRAMEWORK_DATABASE_PASSWORD}") private String databasePassword;

    /**
     * Append changeset to changeset_prepare schema.
     */
    public void appendChangeset(Changeset changeset) {

        AppendChangesetDto appendChangesetDto = new AppendChangesetDto(
                ChangesetXml.toXmlFile(changeset),
                this.getLuaPath(),
                this.database,
                "openstreetmap_geometries",
                this.databaseHost,
                this.databasePort,
                this.databaseUsername,
                this.databasePassword
        );

        this.osm2PgSqlService.appendChangeset(appendChangesetDto);
    }

    /**
     * Get Lua Path from resources.
     */
    private Path getLuaPath() {
        try {
            if (this.osmLuaFile.isFile()) {
                return this.osmLuaFile.getFile().toPath();
            }

            Path tmp = java.nio.file.Files.createTempFile("openstreetmap_geometries", ".lua");
            try (var in = this.osmLuaFile.getInputStream()) {
                java.nio.file.Files.copy(in, tmp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            tmp.toFile().deleteOnExit();
            return tmp;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot resolve Lua style file from " + this.osmLuaFile + ".", e);
        }
    }
}
