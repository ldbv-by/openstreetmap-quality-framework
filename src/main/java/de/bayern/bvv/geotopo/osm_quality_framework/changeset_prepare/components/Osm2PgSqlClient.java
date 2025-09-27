package de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.components;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.AppendChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.spi.Osm2PgSqlService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.OsmPrimitive;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Relation;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Way;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.util.ChangesetXml;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Client to call osm2pgsql from openstreetmap_tools module.
 */
@Component
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
                this.getNormalizedOscPath(changeset),
                this.getLuaPath(),
                this.database,
                "changeset_prepare_" + changeset.getId(),
                this.databaseHost,
                this.databasePort,
                this.databaseUsername,
                this.databasePassword
        );

        this.osm2PgSqlService.appendChangeset(appendChangesetDto);
    }

    /**
     * Get normalized Osc Path.
     */
    private Path getNormalizedOscPath(Changeset changeset) {
        this.normalizeChangeset(changeset);
        return ChangesetXml.toXmlFile(changeset);
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

    /**
     * Normalize changeset for tool osm2pgsql.
     */
    private void normalizeChangeset(Changeset changesetPrepare) {

        // Use only positive Ids in osm2pgsql.
        for (OsmPrimitive osmPrimitive : changesetPrepare.getAllPrimitives()) {
            // Set Ids to positive number.
            if (osmPrimitive.getId() < 0) {
                osmPrimitive.setId((long) Math.pow(10, 17) + Math.abs(osmPrimitive.getId()));
            }

            // Set Nd to positive number.
            if (osmPrimitive instanceof Way way) {
                for (Way.Nd wayNode : way.getNodeRefs()) {
                    if (wayNode.getRef() < 0) {
                        wayNode.setRef((long) Math.pow(10, 17) + Math.abs(wayNode.getRef()));
                    }
                }
            }

            // Set Relation Members to positive number.
            if (osmPrimitive instanceof Relation relation) {
                for (Relation.Member member : relation.getMembers()) {
                    if (member.getRef() < 0) {
                        member.setRef((long) Math.pow(10, 17) + Math.abs(member.getRef()));
                    }
                }
            }
        }

        // Version is necessary.
        changesetPrepare.getCreatePrimitives().forEach(p -> p.setVersion(0L));

        // Increase Version on all objects.
        changesetPrepare.getAllPrimitives().forEach(p -> p.setVersion(p.getVersion() + 1L));
    }
}
