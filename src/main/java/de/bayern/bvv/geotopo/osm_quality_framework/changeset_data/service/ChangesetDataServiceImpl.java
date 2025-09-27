package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.service;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository.ChangesetDataRepository;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.spi.ChangesetDataService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for the changeset_data schema. (implementation)
 */
@Service
@RequiredArgsConstructor
public class ChangesetDataServiceImpl implements ChangesetDataService {

    private final ChangesetDataRepository changesetDataRepository;

    /**
     * Moves the prepared data for a given changeset into the changeset_data schema.
     */
    @Override
    public void movePreparedChangeset(Long changesetId, ChangesetDto changesetDto) {
        Changeset changeset = ChangesetMapper.toDomain(changesetId, changesetDto);

        this.changesetDataRepository.deleteChangesetData(changesetId);
        this.changesetDataRepository.copyPreparedData(changesetId);
        this.changesetDataRepository.insertChangesetObjects(changeset);
    }
}
