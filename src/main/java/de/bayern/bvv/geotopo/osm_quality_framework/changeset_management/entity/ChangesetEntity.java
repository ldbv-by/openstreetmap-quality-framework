package de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.entity;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.ChangesetState;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "changesets", schema = "changeset_data")
@Data
public class ChangesetEntity {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private ChangesetState state;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "closed_at")
    private Instant closedAt;
}
