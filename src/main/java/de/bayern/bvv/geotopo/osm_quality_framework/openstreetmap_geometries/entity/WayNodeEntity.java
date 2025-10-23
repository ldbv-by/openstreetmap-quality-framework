package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.id.WayNodeId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "WayNodesOsmGeometry")
@Table(name = "way_nodes", schema = "openstreetmap_geometries")
@Data
public class WayNodeEntity {

    @EmbeddedId
    private WayNodeId id;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lon")
    private Double lon;
}
