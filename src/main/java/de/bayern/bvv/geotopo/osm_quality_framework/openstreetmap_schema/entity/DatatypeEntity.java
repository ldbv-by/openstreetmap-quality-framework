package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Representing a datatype in schema.
 */
@Entity
@Table(name = "datatypes", schema = "openstreetmap_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatatypeEntity {

    @Id
    @Column(name = "datatype_id")
    private String datatypeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "datatype_type")
    private DatatypeType datatypeType;

    @OneToMany(mappedBy = "datatype", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DatatypeComplexEntity> complexTags = new ArrayList<>();

    @OneToMany(mappedBy = "datatype", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DatatypeDictionaryEntity> dictionaries = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "datatypes_inheritance",
            schema = "openstreetmap_schema",
            joinColumns = @JoinColumn(name = "datatype_id", referencedColumnName = "datatype_id", foreignKey = @ForeignKey(name = "FK_datatype")),
            inverseJoinColumns = @JoinColumn(name = "extends_datatype_id", referencedColumnName = "datatype_id", foreignKey = @ForeignKey(name = "FK_extends_datatype"))
    )
    private Set<DatatypeEntity> parents = new HashSet<>();

    public enum DatatypeType {
        PRIMITIVE,
        DICTIONARY,
        COMPLEX
    }
}
