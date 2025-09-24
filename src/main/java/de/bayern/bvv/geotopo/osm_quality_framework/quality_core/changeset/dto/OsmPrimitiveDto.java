package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OsmPrimitiveDto {
    private Long id;
    private Long version;
    private Long changesetId;
    private List<TagDto> tags;
}
