package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for the changeset_data schema. (implementation)
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ChangesetDataRepositoryImpl implements ChangesetDataRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Deletes all osm objects for the given changeset id in the osm data tables.
     */
    @Override
    @Transactional
    public void deleteChangesetData(Long changesetId) {
        for (String tableName : List.of("relation_members", "relations", "nodes",
                                        "ways", "areas", "changeset_objects")) {

            this.jdbcTemplate.update("DELETE FROM changeset_data." + tableName +
                                         " WHERE changeset_id = ?", changesetId);
        }
    }

    /**
     * Copies prepared osm objects from the changeset_prepare schema.
     */
    @Override
    @Transactional
    public void copyPreparedData(Long changesetId) {
        String prepareSchema = "changeset_prepare_" + changesetId;

        // nodes, ways, areas, relations, relation_members
        List<PrepareTable> preparedTables = List.of(
                new PrepareTable("nodes", "osm_id, version, object_type, tags, geom"),
                new PrepareTable("ways", "osm_id, version, object_type, tags, geom"),
                new PrepareTable("areas", "osm_id, version, object_type, tags, geom, osm_geometry_type"),
                new PrepareTable("relations", "osm_id, version, object_type, members, tags"),
                new PrepareTable("relation_members", "relation_osm_id, member_type, member_osm_id")
        );

        for (PrepareTable tab : preparedTables) {
            String selectColumns = Arrays.stream(tab.columns.split("\\s*,\\s*"))
                    .map(col -> switch (col) {
                        case "relation_osm_id" ->
                                "CASE WHEN relation_osm_id > 1e17 THEN -(relation_osm_id - 1e17)::bigint ELSE relation_osm_id END";
                        case "member_osm_id" ->
                                "CASE WHEN member_osm_id > 1e17 THEN -(member_osm_id - 1e17)::bigint ELSE member_osm_id END";
                        case "osm_id" ->
                                "CASE WHEN osm_id > 1e17 THEN -(osm_id - 1e17)::bigint ELSE osm_id END";
                        default -> col;
                    })
                    .collect(Collectors.joining(", "));

            String sql = """
                INSERT INTO changeset_data.%s (changeset_id, %s)
                SELECT ?, %s
                FROM %s.%s
                """.formatted(
                            tab.name,
                            tab.columns,
                            selectColumns,
                            prepareSchema,
                            tab.name
                    );

            jdbcTemplate.update(sql, ps -> ps.setLong(1, changesetId));
        }

        // planet tables
        String upsertPlanetOsmNodes = """
        INSERT INTO changeset_data.planet_osm_nodes (id, lat, lon, tags)
        SELECT CASE WHEN id > 1e17 THEN -(id - 1e17)::bigint ELSE id END, lat, lon, tags
        FROM %s.planet_osm_nodes
        ON CONFLICT (id) DO UPDATE
        SET lat  = EXCLUDED.lat,
            lon  = EXCLUDED.lon,
            tags = EXCLUDED.tags
        WHERE EXCLUDED.id >= 0
        """.formatted(prepareSchema);

        this.jdbcTemplate.update(upsertPlanetOsmNodes);

        String upsertPlanetOsmWays = """
        INSERT INTO changeset_data.planet_osm_ways (id, nodes, tags)
        SELECT CASE WHEN id > 1e17 THEN -(id - 1e17)::bigint ELSE id END, nodes, tags
        FROM %s.planet_osm_ways
        ON CONFLICT (id) DO UPDATE
        SET nodes = EXCLUDED.nodes,
            tags  = EXCLUDED.tags
        WHERE EXCLUDED.id >= 0
        """.formatted(prepareSchema);

        this.jdbcTemplate.update(upsertPlanetOsmWays);

        String upsertPlanetOsmRels = """
        INSERT INTO changeset_data.planet_osm_rels (id, members, tags)
        SELECT CASE WHEN id > 1e17 THEN -(id - 1e17)::bigint ELSE id END, members, tags
        FROM %s.planet_osm_rels
        ON CONFLICT (id) DO UPDATE
        SET members = EXCLUDED.members,
            tags  = EXCLUDED.tags
        WHERE EXCLUDED.id >= 0
        """.formatted(prepareSchema);

        this.jdbcTemplate.update(upsertPlanetOsmRels);
    }

    private record PrepareTable(
            String name,
            String columns
    ){}

    /**
     * Inserts all changeset objects with operation type (CREATE, MODIFY, DELETE).
     */
    @Override
    @Transactional
    public void insertChangesetObjects(Changeset changeset) {

        // Created and modified changeset objects
        String createdAndModifiedChangesetObjects = """
                WITH v_changeset AS (SELECT ?::bigint AS changeset_id)
        
                INSERT INTO changeset_data.changeset_objects
                    (osm_id, geometry_type, changeset_id, operation_type)
        
                -- Nodes
                SELECT c_node.osm_id, 'NODE', cs.changeset_id,
                       CASE WHEN o_node.osm_id IS NULL THEN 'CREATE' ELSE 'MODIFY' END
                  FROM changeset_data.nodes c_node
                  CROSS JOIN v_changeset cs
                  LEFT JOIN openstreetmap_geometries.nodes o_node
                    ON o_node.osm_id = c_node.osm_id
                  WHERE c_node.changeset_id = cs.changeset_id

                UNION ALL

                -- Ways
                SELECT c_way.osm_id, 'WAY', cs.changeset_id,
                       CASE WHEN o_way.osm_id IS NULL THEN 'CREATE' ELSE 'MODIFY' END
                  FROM changeset_data.ways c_way
                  CROSS JOIN v_changeset cs
                  LEFT JOIN openstreetmap_geometries.ways o_way
                    ON o_way.osm_id = c_way.osm_id
                WHERE c_way.changeset_id = cs.changeset_id

                UNION ALL

                -- Areas
                SELECT c_area.osm_id, case when c_area.osm_geometry_type = 'W' then 'AREA' else 'MULTIPOLYGON' end, cs.changeset_id,
                       CASE WHEN o_area.osm_id IS NULL THEN 'CREATE' ELSE 'MODIFY' END
                  FROM changeset_data.areas c_area
                  CROSS JOIN v_changeset cs
                  LEFT JOIN openstreetmap_geometries.areas o_area
                    ON o_area.osm_id = c_area.osm_id
                 WHERE c_area.changeset_id = cs.changeset_id

                UNION ALL

                -- Relations
                SELECT c_rel.osm_id, 'RELATION', cs.changeset_id,
                       CASE WHEN o_rel.osm_id IS NULL THEN 'CREATE' ELSE 'MODIFY' END
                  FROM changeset_data.relations c_rel
                  CROSS JOIN v_changeset cs
                  LEFT JOIN openstreetmap_geometries.relations o_rel
                    ON o_rel.osm_id = c_rel.osm_id
                 WHERE c_rel.changeset_id = cs.changeset_id;
        """;

        this.jdbcTemplate.update(createdAndModifiedChangesetObjects, ps -> ps.setLong(1, changeset.getId()));

        // Deleted changeset objects
        Set<Long> nodeIds = changeset.getDeletePrimitives().stream().filter(Node.class::isInstance).map(OsmPrimitive::getId).collect(Collectors.toSet());
        Set<Long> wayIds = changeset.getDeletePrimitives().stream().filter(Way.class::isInstance).map(OsmPrimitive::getId).collect(Collectors.toSet());
        Set<Long> relationIds = changeset.getDeletePrimitives().stream().filter(Relation.class::isInstance).map(OsmPrimitive::getId).collect(Collectors.toSet());

        String deletedChangesetObjects = """
                WITH v_changeset AS (SELECT ?::bigint AS changeset_id),
                     v_nodes(id) AS (SELECT unnest(coalesce(?::bigint[], '{}'))),
                     v_ways(id) AS (SELECT unnest(coalesce(?::bigint[], '{}'))),
                     v_rels(id) AS (SELECT unnest(coalesce(?::bigint[], '{}')))
        
                INSERT INTO changeset_data.changeset_objects
                    (osm_id, geometry_type, changeset_id, operation_type)

                -- Nodes
                SELECT o_node.osm_id, 'NODE', cs.changeset_id, 'DELETE'
                  FROM openstreetmap_geometries.nodes o_node
                  JOIN v_nodes ON v_nodes.id = o_node.osm_id
                 CROSS JOIN v_changeset cs
    
                UNION ALL
                
                -- Ways
                SELECT o_way.osm_id, 'WAY', cs.changeset_id, 'DELETE'
                  FROM openstreetmap_geometries.ways o_way
                  JOIN v_ways ON v_ways.id = o_way.osm_id
                 CROSS JOIN v_changeset cs
    
                 UNION ALL

                -- Areas
                SELECT o_area.osm_id,  case when o_area.osm_geometry_type = 'W' then 'AREA' else 'MULTIPOLYGON' end, cs.changeset_id, 'DELETE'
                  FROM openstreetmap_geometries.areas o_area
                  JOIN v_ways ON v_ways.id = o_area.osm_id AND o_area.osm_geometry_type = 'W'
                 CROSS JOIN v_changeset cs
    
                UNION ALL
                
                SELECT o_area.osm_id, case when o_area.osm_geometry_type = 'W' then 'AREA' else 'MULTIPOLYGON' end, cs.changeset_id, 'DELETE'
                  FROM openstreetmap_geometries.areas o_area
                  JOIN v_rels ON v_rels.id = o_area.osm_id AND o_area.osm_geometry_type = 'R'
                 CROSS JOIN v_changeset cs

                UNION ALL
                
                -- Relations
                SELECT o_rel.osm_id, 'RELATION', cs.changeset_id, 'DELETE'
                  FROM openstreetmap_geometries.relations o_rel
                  JOIN v_rels ON v_rels.id = o_rel.osm_id
                 CROSS JOIN v_changeset cs
                """;

        this.jdbcTemplate.update(deletedChangesetObjects, ps -> {
            ps.setLong(1, changeset.getId());

            // v_nodes
            if (nodeIds.isEmpty()) {
                ps.setNull(2, java.sql.Types.ARRAY);
            } else {
                ps.setArray(2, ps.getConnection().createArrayOf("bigint", nodeIds.toArray(new Long[0])));
            }

            // v_ways
            if (wayIds.isEmpty()) {
                ps.setNull(3, java.sql.Types.ARRAY);
            } else {
                ps.setArray(3, ps.getConnection().createArrayOf("bigint", wayIds.toArray(new Long[0])));
            }

            // v_rels
            if (relationIds.isEmpty()) {
                ps.setNull(4, java.sql.Types.ARRAY);
            } else {
                ps.setArray(4, ps.getConnection().createArrayOf("bigint", relationIds.toArray(new Long[0])));
            }
        });
    }
}
