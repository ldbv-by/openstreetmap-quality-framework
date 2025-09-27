package de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.service;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.spi.ChangesetDataService;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.components.Osm2PgSqlClient;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.repository.ChangesetPrepareRepository;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.spi.ChangesetPrepareService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for the changeset_prepare_X schema. (implementation)
 */
@Service
@RequiredArgsConstructor
public class ChangesetPrepareServiceImpl implements ChangesetPrepareService {

    private final ChangesetPrepareRepository changesetPrepareRepository;
    private final Osm2PgSqlClient osm2PgSqlClient;
    private final ChangesetDataService changesetDataService;

    /**
     * Prepare the changeset and persist its data to the database.
     */
    @Override
    public void prepareChangeset(Long changesetId, ChangesetDto changesetDto) {
        Changeset changeset = ChangesetMapper.toDomain(changesetId, changesetDto);

        try {
            this.changesetPrepareRepository.createSchemaByChangesetId(changesetId);
            this.changesetPrepareRepository.insertDependingOsmObjects(changeset);
            this.osm2PgSqlClient.appendChangeset(changeset);
            this.changesetDataService.movePreparedChangeset(changesetId, ChangesetMapper.toDto(changeset));
        } finally {
            this.changesetPrepareRepository.dropSchemaByChangeset(changesetId);
        }
    }
}
