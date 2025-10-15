package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.ChangesetDataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.ChangesetDataSet;
import lombok.experimental.UtilityClass;

/**
 * Mapping between {@link ChangesetDataSet} and {@link ChangesetDataSetDto}.
 */
@UtilityClass
public class ChangesetDataSetMapper {

    /**
     * Map tagged objects to domain.
     */
    public ChangesetDataSet toDomain(ChangesetDataSetDto changesetDataSetDto) {
        if (changesetDataSetDto == null) return null;

        ChangesetDataSet changesetDataSet = new ChangesetDataSet();
        changesetDataSet.setCreate(DataSetMapper.toDomain(changesetDataSetDto.create()));
        changesetDataSet.setModify(DataSetMapper.toDomain(changesetDataSetDto.modify()));
        changesetDataSet.setDelete(DataSetMapper.toDomain(changesetDataSetDto.delete()));

        return changesetDataSet;
    }

    /**
     * Map tagged objects to dto.
     */
    public ChangesetDataSetDto toDto(ChangesetDataSet changesetDataSet) {
        if (changesetDataSet == null) return null;

        return new ChangesetDataSetDto(
                DataSetMapper.toDto(changesetDataSet.getCreate()),
                DataSetMapper.toDto(changesetDataSet.getModify()),
                DataSetMapper.toDto(changesetDataSet.getDelete())
        );
    }
}
