package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.id.RelationId;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.id.RelationMemberId;
import jakarta.persistence.*;
import lombok.*;

/**
 * Representing an object type relation.
 */
@Entity(name = "SchemaRelationMember")
@Table(name = "relation_members", schema = "openstreetmap_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationMemberEntity {

    @EmbeddedId
    private RelationMemberId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("relationId")
    @JoinColumns({
            @JoinColumn(name = "object_type", referencedColumnName = "object_type"),
            @JoinColumn(name = "relation_object_type", referencedColumnName = "relation_object_type")
    })
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private RelationEntity relation;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "multiplicity")
    private String multiplicity;
}
