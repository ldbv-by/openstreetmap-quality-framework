package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;

@MappedSuperclass
@Data
public abstract class OsmObjectEntity {
    @Id
    @Column(name = "osm_id")
    private Long osmId;

    @Column(name = "version")
    private Integer version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "changeset_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_changeset_id")
    )
    private ChangesetEntity changeset;

    @Column(name = "object_type")
    private String objectType;

    @Column(name = "tags")
    @JdbcTypeCode(SqlTypes.JSON)
    private HashMap<String, String> tags;
}
