package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.id.DictionaryId;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Representing a dictionary schema datatype.
 */
@Entity
@Table(name = "datatypes_dictionary", schema = "openstreetmap_schema")
@Data
public class DatatypeDictionaryEntity {

    @EmbeddedId
    private DictionaryId id;

    @Column(name = "dictionary_description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("datatypeId")
    @JoinColumn(
            name = "datatype_id",
            referencedColumnName = "datatype_id",
            foreignKey = @ForeignKey(name = "FK_dictionary_datatypes")
    )
    private DatatypeEntity datatype;
}
