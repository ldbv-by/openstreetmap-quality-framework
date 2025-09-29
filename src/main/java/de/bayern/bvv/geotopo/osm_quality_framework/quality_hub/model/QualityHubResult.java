package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.model;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Representing a quality result of a changeset.
 */
@Data
public class QualityHubResult {
    private Changeset changeset;
    private boolean isValid = true;
    private List<QualityServiceResultDto> qualityServiceResults = new CopyOnWriteArrayList<>();

    public QualityHubResult(Changeset changeset) {
        this.changeset = changeset;
    }

    public void addQualityServiceResult(QualityServiceResultDto qualityServiceResultDto) {
        this.qualityServiceResults.add(qualityServiceResultDto);
    }
}
