package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.id.RelationId;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Representing an object type relation.
 */
@Entity(name = "SchemaRelation")
@Table(name = "relations", schema = "openstreetmap_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationEntity {

    @EmbeddedId
    private RelationId id;

    @Column(name = "multiplicity")
    private String multiplicity;

    @OneToMany(mappedBy = "relation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RelationMemberEntity> members = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("objectType")
    @JoinColumn(
            name = "object_type",
            referencedColumnName = "object_type",
            foreignKey = @ForeignKey(name = "FK_relation_object_types")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ObjectTypeEntity objectType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("relationObjectType")
    @JoinColumn(
            name = "relation_object_type",
            referencedColumnName = "object_type",
            foreignKey = @ForeignKey(name = "FK_relation_relation_object_types")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ObjectTypeEntity relationObjectType;

}
