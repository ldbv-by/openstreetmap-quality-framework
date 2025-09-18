package de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Node extends OsmPrimitive {
    private String lat;
    private String lon;
}
