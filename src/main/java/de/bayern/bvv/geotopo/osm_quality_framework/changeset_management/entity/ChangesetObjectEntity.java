package de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.entity;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.GeometryType;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.OperationType;
import jakarta.persistence.*;
import lombok.Data;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "changeset_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_changeset_id")
    )
    private ChangesetEntity changeset;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;
}
