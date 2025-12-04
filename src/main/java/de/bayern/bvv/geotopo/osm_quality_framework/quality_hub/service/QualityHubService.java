package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.service;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.api.ChangesetManagementService;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.api.OsmGeometriesService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.ChangesetState;
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
 * Central service component of the Quality-Hub bounded context.
 * <p>
 * Receives incoming changesets, prepares them for validation and coordinates
 * the execution of the configured quality modules via the Orchestrator.
 * It also manages changeset lifecycle transitions in collaboration with the
 * Changeset-Management and OpenStreetMap-Geometries contexts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QualityHubService {

    private final Orchestrator orchestrator;
    private final ChangesetManagementService changesetManagementService;
    private final OsmGeometriesService osmGeometriesService;

    /**
     * Prepares a changeset for validation and triggers the domain-specific quality checks.
     */
    public QualityHubResultDto checkChangesetQuality(Long changesetId, ChangesetDto changesetDto) {

        // ----- Persist and normalize changeset for internal processing.
        final long startTime = System.currentTimeMillis();
        this.changesetManagementService.persistChangeset(changesetId, changesetDto);
        final long prepareTime = System.currentTimeMillis() - startTime;

        // ----- Invoke the Orchestrator to distribute the changeset to all registered quality modules.
        Changeset changeset = ChangesetMapper.toDomain(changesetId, changesetDto);
        QualityHubResult qualityHubResult = this.orchestrator.start(changeset);

        // ----- Update lifecycle status based on validation outcome.
        if (qualityHubResult.isValid()) {
            this.changesetManagementService.setChangesetState(changesetId, ChangesetState.CHECKED);
        } else {
            this.changesetManagementService.setChangesetState(changesetId, ChangesetState.CANCELLED);
        }

        // ----- Log processing durations for transparency and performance analysis.
        final long totalTime = System.currentTimeMillis() - startTime;
        log.info("checkChangesetQuality({}): prepareTime={} ms, orchestratorTime={} ms, totalTime={} ms",
                changesetId, prepareTime, (totalTime - prepareTime), totalTime);

        // ----- Return aggregated validation results back to the calling workflow.
        return QualityHubResultMapper.toDto(qualityHubResult);
    }

    /**
     * Finalizes a validated changeset and persists the changeset in the OpenStreetMap-Geometries schema.
     */
    public void finishChangeset(Long changesetId) {

        // ----- Persist the changeset in the OpenStreetMap-Geometries schema and mark its lifecycle as completed.
        this.osmGeometriesService.appendChangeset(changesetId);
        this.changesetManagementService.setChangesetState(changesetId, ChangesetState.FINISHED);
    }
}