package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OSM tag.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Tag {

    /**
     * Tag key.
     */
    private String k;

    /**
     * Tag value.
     */
    private String v;
}
