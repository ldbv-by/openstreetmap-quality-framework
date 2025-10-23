package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.service;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.api.ChangesetDataService;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.api.ChangesetPrepareService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.ChangesetState;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.component.Osm2PgSqlClient;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.component.OsmApiClient;
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

import java.util.Optional;

/**
 * Service to manage quality check requests.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QualityHubService {

    private final Orchestrator orchestrator;
    private final ChangesetPrepareService changesetPrepareService;
    private final ChangesetDataService changesetDataService;

    private final OsmApiClient osmApiClient;
    private final Osm2PgSqlClient osm2PgSqlClient;


    /**
     * Persists the changeset and publishes it to the configured quality services.
     */
    public QualityHubResultDto checkChangesetQuality(Long changesetId, ChangesetDto changesetDto) {
        long startTime = System.currentTimeMillis();
        this.changesetPrepareService.prepareChangeset(changesetId, changesetDto);
        long prepareTime = System.currentTimeMillis() - startTime;

        Changeset changeset = ChangesetMapper.toDomain(changesetId, changesetDto);
        QualityHubResult qualityHubResult = this.orchestrator.start(changeset);

        if (qualityHubResult.isValid()) {
            this.changesetDataService.setChangesetState(changesetId, ChangesetState.CHECKED);
        } else {
            this.changesetDataService.setChangesetState(changesetId, ChangesetState.CANCELLED);
        }

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("checkChangesetQuality({}): prepareTime={} ms, orchestratorTime={} ms, totalTime={} ms",
                changesetId, prepareTime, (totalTime - prepareTime), totalTime);

        return QualityHubResultMapper.toDto(qualityHubResult);
    }


    /**
     * Updates OpenStreetMap geometries with the finalized changeset.
     * Sets the changeset state to "finished".
     */
    public void finishChangeset(Long changesetId) {

        // Read changeset from OSM API.
        Changeset changeset = Optional.ofNullable(this.osmApiClient.getChangesetById(changesetId))
                .map(cs -> ChangesetMapper.toDomain(changesetId, cs))
                .orElse(null);

        if (changesetId != null) {
            // Append changeset to OpenStreetMap Geometries.
            this.osm2PgSqlClient.appendChangeset(changeset);

            // Sets the changeset state to "finished".
            this.changesetDataService.setChangesetState(changesetId, ChangesetState.FINISHED);
        }
    }
}