package de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.entity;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.entity.id.WayNodeId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity(name = "WayNodesChangesetData")
@Immutable
@Table(name = "way_nodes", schema = "changeset_data")
@Subselect(value = """
    SELECT way.osm_id AS way_osm_id,
           node.id AS node_osm_id,
           way.changeset_id AS changeset_id,
           node.lat::double precision / '10000000'::bigint::double precision AS lat,
           node.lon::double precision / '10000000'::bigint::double precision AS lon,
           node.seq
      FROM changeset_data.ways way,
           changeset_data.planet_osm_ways planet_way,
           LATERAL ( SELECT nd.node_id,
                            nd.seq,
                            planet_node.id,
                            planet_node.lat,
                            planet_node.lon,
                            planet_node.tags
                       FROM unnest(planet_way.nodes) WITH ORDINALITY nd(node_id, seq),
                            changeset_data.planet_osm_nodes planet_node
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
