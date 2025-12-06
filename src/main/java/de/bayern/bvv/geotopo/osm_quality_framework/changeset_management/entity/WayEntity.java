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
@Entity(name = "WayChangesetData")
@Table(name = "ways", schema = "changeset_data")
@Data
public class WayEntity extends OsmObjectEntity {

    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @Column(name = "geom", columnDefinition = "Geometry(Geometry, 4326)")
    private Geometry geom;
}
