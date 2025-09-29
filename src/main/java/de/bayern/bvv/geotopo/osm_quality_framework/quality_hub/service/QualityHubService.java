package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.service;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.spi.ChangesetPrepareService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.dto.QualityHubResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.mapper.QualityHubResultMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.model.QualityHubResult;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.component.Orchestrator;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Quality Hub Service.
 */
@Service
@RequiredArgsConstructor
public class QualityHubService {

    private final Orchestrator orchestrator;
    private final ChangesetPrepareService changesetPrepareService;

    /**
     * Persists the changeset and publishes it to the configured quality services.
     */
    public QualityHubResultDto checkChangesetQuality(Long changesetId, ChangesetDto changesetDto) {
        this.changesetPrepareService.prepareChangeset(changesetId, changesetDto);

        Changeset changeset = ChangesetMapper.toDomain(changesetId, changesetDto);
        QualityHubResult qualityHubResult = this.orchestrator.start(changeset);

        return QualityHubResultMapper.toDto(qualityHubResult);
    }
}