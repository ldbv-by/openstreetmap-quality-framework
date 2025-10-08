package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.id.ComplexTagId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representing a complex schema datatype.
 */
@Entity
@Table(name = "datatypes_complex", schema = "openstreetmap_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatatypeComplexEntity {

    @EmbeddedId
    private ComplexTagId id;

    @Column(name = "multiplicity")
    private String multiplicity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("datatypeId")
    @JoinColumn(
            name = "datatype_id",
            referencedColumnName = "datatype_id",
            foreignKey = @ForeignKey(name = "FK_datatypes")
    )
    private DatatypeEntity datatype;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "tag_datatype_id",
            referencedColumnName = "datatype_id",
            foreignKey = @ForeignKey(name = "FK_tags_datatypes")
    )
    private DatatypeEntity tagDatatype;
}
