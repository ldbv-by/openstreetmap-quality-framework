package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.id.AreaNodeId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity(name = "AreaNodesOsmGeometry")
@Table(name = "way_nodes", schema = "openstreetmap_geometries")
@Immutable
@Subselect(value = """
            SELECT area.osm_id AS area_osm_id,
                   node.id AS node_osm_id,
                   NULL::bigint AS member_osm_id,
                   node.lat::double precision / '10000000'::bigint::double precision AS lat,
                   node.lon::double precision / '10000000'::bigint::double precision AS lon,
                   node.seq
              FROM openstreetmap_geometries.areas area,
                   openstreetmap_geometries.planet_osm_ways planet_way,
            LATERAL ( SELECT nd.node_id,
                             nd.seq,
                             planet_node.id,
                             planet_node.lat,
                             planet_node.lon,
                             planet_node.tags
                        FROM unnest(planet_way.nodes) WITH ORDINALITY nd(node_id, seq),
                             openstreetmap_geometries.planet_osm_nodes planet_node
                       WHERE planet_node.id = nd.node_id) node
            WHERE area.osm_geometry_type = 'W'::bpchar AND planet_way.id = area.osm_id
            UNION ALL
            SELECT area.osm_id AS area_osm_id,
                   node.id AS node_osm_id,
                   planet_way.id AS member_osm_id,
                   node.lat::double precision / '10000000'::bigint::double precision AS lat,
                   node.lon::double precision / '10000000'::bigint::double precision AS lon,
                   node.seq
              FROM openstreetmap_geometries.areas area,
                   openstreetmap_geometries.planet_osm_rels planet_rel,
                   openstreetmap_geometries.planet_osm_ways planet_way,
            LATERAL ( SELECT nd.node_id,
                             nd.seq,
                             planet_node.id,
                             planet_node.lat,
                             planet_node.lon,
                             planet_node.tags
                        FROM unnest(planet_way.nodes) WITH ORDINALITY nd(node_id, seq),
                             openstreetmap_geometries.planet_osm_nodes planet_node
                       WHERE planet_node.id = nd.node_id) node
            WHERE area.osm_geometry_type = 'R'::bpchar AND planet_rel.id = area.osm_id
              AND (planet_way.id = ANY (openstreetmap_geometries.planet_osm_member_ids(planet_rel.members, 'W'::bpchar)))
""")
@Data
public class AreaNodeEntity {

    @EmbeddedId
    private AreaNodeId id;

    @Column(name = "member_osm_id")
    private Long memberOsmId;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;
}
