package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.id.TagId;
import jakarta.persistence.*;
import lombok.*;

/**
 * Representing an object type attribute.
 */
@Entity
@Table(name = "tags", schema = "openstreetmap_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity {

    @EmbeddedId
    private TagId id;

    @Column(name = "multiplicity")
    private String multiplicity;

    @Column(name = "is_system", nullable = false)
    private Boolean isSystem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("objectType")
    @JoinColumn(
            name = "object_type",
            referencedColumnName = "object_type",
            foreignKey = @ForeignKey(name = "FK_tags_object_types")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ObjectTypeEntity objectType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "tag_datatype_id",
            referencedColumnName = "datatype_id",
            foreignKey = @ForeignKey(name = "FK_tags_datatypes")
    )
    private DatatypeEntity tagDatatype;
}