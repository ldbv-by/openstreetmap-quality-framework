package de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Geometry;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "NodesChangesetData")
@Table(name = "nodes", schema = "changeset_data")
@Data
public class NodeEntity extends OsmObjectEntity {

    @Column(name = "geom", columnDefinition = "Geometry(Geometry, 4326)")
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    private Geometry geom;
}
