package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.service;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.spi.ChangesetPrepareService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.ChangesetQualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.component.Orchestrator;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.mapper.ChangesetQualityServiceResultMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.model.ChangesetQualityServiceResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QualityHubService {

    private final Orchestrator orchestrator;
    private final ChangesetPrepareService changesetPrepareService;

    /**
     * Persists the changeset and publishes it to the configured quality services.
     */
    public List<ChangesetQualityServiceResultDto> checkChangesetQuality(Long changesetId, ChangesetDto changesetDto) {
        this.changesetPrepareService.prepareChangeset(changesetId, changesetDto);

        Changeset changeset = ChangesetMapper.toDomain(changesetId, changesetDto);
        List<ChangesetQualityServiceResult> changesetQualityServiceResults = this.orchestrator.start(changeset);

        return changesetQualityServiceResults.stream().map(ChangesetQualityServiceResultMapper::toDto).toList();
    }
}
