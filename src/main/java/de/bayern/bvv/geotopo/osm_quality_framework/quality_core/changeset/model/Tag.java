package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Tag {
    private String k;
    private String v;
}
