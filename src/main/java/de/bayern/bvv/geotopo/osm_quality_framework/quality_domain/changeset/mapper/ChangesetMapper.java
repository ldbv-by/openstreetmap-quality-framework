package de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.model.Changeset;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ChangesetMapper {

    /**
     * Map changeset dto to domain.
     */
    public Changeset toDomain(ChangesetDto changesetDto) {
        if (changesetDto == null) return null;
        return new Changeset(changesetDto.changesetXml());
    }

    /**
     * Map changeset domain to dto.
     */
    public ChangesetDto toDto(Changeset changeset) {
        if (changeset == null) return null;
        return new ChangesetDto(changeset.changesetXml);
    }
}
