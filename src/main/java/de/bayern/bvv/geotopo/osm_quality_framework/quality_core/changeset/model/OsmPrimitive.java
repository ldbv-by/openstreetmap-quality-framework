package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OsmPrimitive {
    private Long id;
    private Long version;
    private Long changesetId;
    private List<Tag> tags;
}
