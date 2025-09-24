package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.model;


import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChangesetQualityServiceResult {
    private String qualityServiceId;
    private boolean isValid;
    private Changeset modifiedChangeset;
}
