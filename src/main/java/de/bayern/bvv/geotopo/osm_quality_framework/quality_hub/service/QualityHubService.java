package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.service;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.api.ChangesetPrepareService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.dto.QualityHubResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.mapper.QualityHubResultMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.model.QualityHubResult;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.component.Orchestrator;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to manage quality check requests.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QualityHubService {

    private final Orchestrator orchestrator;
    private final ChangesetPrepareService changesetPrepareService;

    /**
     * Persists the changeset and publishes it to the configured quality services.
     */
    public QualityHubResultDto checkChangesetQuality(Long changesetId, ChangesetDto changesetDto) {
        long startTime = System.currentTimeMillis();
        this.changesetPrepareService.prepareChangeset(changesetId, changesetDto);
        long prepareTime = System.currentTimeMillis() - startTime;

        Changeset changeset = ChangesetMapper.toDomain(changesetId, changesetDto);
        QualityHubResult qualityHubResult = this.orchestrator.start(changeset);

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("checkChangesetQuality({}): prepareTime={} ms, orchestratorTime={} ms, totalTime={} ms",
                changesetId, prepareTime, (totalTime - prepareTime), totalTime);

        return QualityHubResultMapper.toDto(qualityHubResult);
    }
}