package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.id.AreaNodeId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "AreaNodesChangesetData")
@Table(name = "area_nodes", schema = "changeset_data")
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
