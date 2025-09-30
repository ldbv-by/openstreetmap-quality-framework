package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Primary key of a tag.
 */
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class TagId implements Serializable {

    @Column(name = "object_type")
    private String objectType;

    @Column(name = "tag_key")
    private String tagKey;
}
