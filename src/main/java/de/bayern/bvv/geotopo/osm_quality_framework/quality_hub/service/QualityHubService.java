package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.service;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto.ChangesetQualityResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.component.Orchestrator;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.mapper.ChangesetQualityResultMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.model.ChangesetQualityResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QualityHubService {

    private final Orchestrator orchestrator;

    /**
     * Persists the changeset and publishes it to the configured quality services.
     */
    public List<ChangesetQualityResultDto> checkChangesetQuality(ChangesetDto changesetDto) {
        Changeset changeset = ChangesetMapper.toDomain(changesetDto);

        List<ChangesetQualityResult> changesetQualityResults = this.orchestrator.start(changeset);

        return changesetQualityResults.stream().map(ChangesetQualityResultMapper::toDto).toList();
    }
}
