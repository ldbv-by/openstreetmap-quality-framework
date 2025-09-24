package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity;

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

    @Column(name = "changeset_id")
    private Long changesetId;

    @Column(name = "object_type")
    private String objectType;

    @Column(name = "tags")
    @JdbcTypeCode(SqlTypes.JSON)
    private HashMap<String, String> tags;
}
