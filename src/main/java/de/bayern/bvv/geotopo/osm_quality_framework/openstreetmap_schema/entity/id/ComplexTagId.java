package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Primary key of a datatype tag.
 */
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class ComplexTagId implements Serializable {

    @Column(name = "datatype_id")
    private String datatypeId;

    @Column(name = "tag_key")
    private String tagKey;
}
