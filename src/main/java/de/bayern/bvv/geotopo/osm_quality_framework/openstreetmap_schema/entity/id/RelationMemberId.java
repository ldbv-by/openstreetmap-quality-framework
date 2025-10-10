package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.id;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Primary key of a relation member.
 */
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class RelationMemberId implements Serializable {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "objectType", column = @Column(name = "object_type")),
            @AttributeOverride(name = "relationObjectType", column = @Column(name = "relation_object_type"))
    })
    private RelationId relationId;

    @Column(name = "role")
    private String role;
}
