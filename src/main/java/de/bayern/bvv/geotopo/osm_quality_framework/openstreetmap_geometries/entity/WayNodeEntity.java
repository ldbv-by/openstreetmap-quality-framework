package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.id.WayNodeId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity(name = "WayNodesOsmGeometry")
@Immutable
@Table(name = "way_nodes", schema = "openstreetmap_geometries")
@Subselect(value = """
    SELECT way.osm_id AS way_osm_id,
           node.id AS node_osm_id,
           node.lat::double precision / '10000000'::bigint::double precision AS lat,
           node.lon::double precision / '10000000'::bigint::double precision AS lon,
           node.seq
      FROM openstreetmap_geometries.ways way,
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
    WHERE planet_way.id = way.osm_id
""")
@Data
public class WayNodeEntity {

    @EmbeddedId
    private WayNodeId id;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;
}
