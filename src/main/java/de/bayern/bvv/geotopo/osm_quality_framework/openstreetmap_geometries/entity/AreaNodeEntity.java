package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.id.AreaNodeId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "AreaNodesOsmGeometry")
@Table(name = "area_nodes", schema = "openstreetmap_geometries")
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
