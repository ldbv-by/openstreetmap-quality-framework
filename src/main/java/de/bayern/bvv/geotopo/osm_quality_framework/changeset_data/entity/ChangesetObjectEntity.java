package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.GeometryType;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.OperationType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "changeset_objects", schema = "changeset_data")
@Data
public class ChangesetObjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "osm_id", nullable = false)
    private Long osmId;

    @Enumerated(EnumType.STRING)
    @Column(name = "geometry_type", nullable = false)
    private GeometryType geometryType;

    @Column(name = "changeset_id", nullable = false)
    private Long changesetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;
}
