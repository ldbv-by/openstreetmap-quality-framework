package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Representing an object type entity.
 */
@Entity
@Table(name = "object_types", schema = "openstreetmap_schema")
@Data
public class ObjectTypeEntity {

    @Id
    @Column(name = "object_type")
    private String objectType;

    @Column(name = "is_abstract", nullable = false)
    private Boolean isAbstract;

    @OneToMany(mappedBy = "objectType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TagEntity> tags = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "object_types_inheritance",
            schema = "openstreetmap_schema",
            joinColumns = @JoinColumn(name = "object_type", referencedColumnName = "object_type", foreignKey = @ForeignKey(name = "FK_object_type")),
            inverseJoinColumns = @JoinColumn(name = "extends_object_type", referencedColumnName = "object_type", foreignKey = @ForeignKey(name = "FK_extends_object_type"))
    )
    private Set<ObjectTypeEntity> parents = new HashSet<>();
}
