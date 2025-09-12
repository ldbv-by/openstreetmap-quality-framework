package de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChangesetQualityResult {
    private String qualityServiceId;
    private boolean isValid;
    private Changeset modifiedChangeset;
}
