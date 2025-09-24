package de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ChangesetPrepareRepositoryImpl implements ChangesetPrepareRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createSchemaByChangesetId(Long changesetId) {
        String srcSchemaName = "openstreetmap_geometries";
        String dstSchemaName = "changeset_prepare_" + changesetId;

        try {
            this.jdbcTemplate.execute("CREATE SCHEMA " + dstSchemaName);

            // copy openstreetmap_geometries tables
            List<String> tables = this.jdbcTemplate.queryForList(
                    "SELECT table_name FROM information_schema.tables " +
                            "WHERE table_schema = ? AND table_type = 'BASE TABLE'",
                    String.class, srcSchemaName);

            for (String table : tables) {
                String sql = "CREATE TABLE " + dstSchemaName + "." + table +
                        " (LIKE " + srcSchemaName + "." + table + " INCLUDING ALL)";

                this.jdbcTemplate.execute(sql);
            }

            log.info("Schema {} created.", dstSchemaName);
        } catch (Exception e) {
            throw new IllegalStateException("Schema " + dstSchemaName + " creation failed:\n" + e.getMessage());
        }
    }

    @Override
    public void dropSchemaByChangeset(Long changesetId) {
        String dstSchemaName = "changeset_prepare_" + changesetId;
        this.jdbcTemplate.execute("DROP SCHEMA IF EXISTS " + dstSchemaName + " CASCADE");
        log.info("Schema {} dropped.", dstSchemaName);
    }

    @Override
    public void insertDependingOsmObjects(Changeset changeset) {
        String dstSchemaName = "changeset_prepare_" + changeset.getId();

        Set<Long> nodeIds = changeset.getAllPrimitives().stream().filter(Node.class::isInstance).map(OsmPrimitive::getId).collect(Collectors.toSet());
        Set<Long> wayIds = changeset.getAllPrimitives().stream().filter(Way.class::isInstance).map(OsmPrimitive::getId).collect(Collectors.toSet());
        Set<Long> relationIds = changeset.getAllPrimitives().stream().filter(Relation.class::isInstance).map(OsmPrimitive::getId).collect(Collectors.toSet());

        try (InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream("changeset_prepare/sql/insertDependingOsmObjects.sql")) {

            if (inputStream == null) throw new IllegalStateException("changeset_prepare/sql/insertDependingOsmObjects.sql is null.");

            String query = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("___CHANGESET_PREPARE___", dstSchemaName)
                    .replace("___NODE_IDS___", toBigIntArray(nodeIds))
                    .replace("___WAY_IDS___", toBigIntArray(wayIds))
                    .replace("___RELATION_IDS___", toBigIntArray(relationIds));

            this.jdbcTemplate.update(query);
            log.info("Depending Osm Objects in {} inserted.", dstSchemaName);

        } catch (Exception e) {
            throw new IllegalStateException("Error in script \"changeset_prepare/sql/insertDependingOsmObjects.sql\":\n" + e.getMessage());
        }
    }

    private static String toBigIntArray(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return "NULL::bigint[]";
        String joined = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        return "ARRAY[" + joined + "]::bigint[]";
    }
}
