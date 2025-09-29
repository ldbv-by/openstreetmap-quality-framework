package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.model;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceErrorDto;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Representing a quality result of a changeset.
 */
@Getter
public class QualityServiceResult {

    private final String qualityServiceId;
    private final Long changesetId;
    private final List<QualityServiceError> errors = new ArrayList<>();
    private Changeset modifiedChangeset;

    public QualityServiceResult(String qualityServiceId, Long changesetId) {
        this.qualityServiceId = qualityServiceId;
        this.changesetId = changesetId;
    }

    public void addError(QualityServiceError error) {
        this.errors.add(error);
    }
}
