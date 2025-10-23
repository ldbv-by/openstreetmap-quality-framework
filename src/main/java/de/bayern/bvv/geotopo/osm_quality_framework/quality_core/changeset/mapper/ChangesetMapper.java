package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.OsmPrimitiveDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.OsmPrimitive;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mapping between {@link Changeset} and {@link ChangesetDto}.
 */
@UtilityClass
public class ChangesetMapper {

    /**
     * Map changeset dto to domain.
     */
    public Changeset toDomain(Long changesetId, ChangesetDto changesetDto) {
        if (changesetDto == null) return null;

        Changeset domain = new Changeset();
        domain.setId(changesetId);
        domain.setVersion(changesetDto.getVersion());
        domain.setGenerator(changesetDto.getGenerator());

        domain.setCreatePrimitives(mapOsmPrimitivesToDomain(changesetDto.getCreateBlocks()));
        domain.setModifyPrimitives(mapOsmPrimitivesToDomain(changesetDto.getModifyBlocks()));
        domain.setDeletePrimitives(mapOsmPrimitivesToDomain(changesetDto.getDeleteBlocks()));

        return domain;
    }

    /**
     * Map changeset domain to dto.
     */
    public ChangesetDto toDto(Changeset changeset) {
        if (changeset == null) return null;

        ChangesetDto dto = new ChangesetDto();
        dto.setVersion(changeset.getVersion());
        dto.setGenerator(changeset.getGenerator());

        dto.setCreateBlocks(mapOsmPrimitivesToDto(changeset.getCreatePrimitives()));
        dto.setModifyBlocks(mapOsmPrimitivesToDto(changeset.getModifyPrimitives()));
        dto.setDeleteBlocks(mapOsmPrimitivesToDto(changeset.getDeletePrimitives()));

        return dto;
    }

    /**
     * Map OsmPrimitive List domain to dto.
     */
    private List<ChangesetDto.OsmPrimitiveBlockDto> mapOsmPrimitivesToDto(List<OsmPrimitive> primitives) {
        if (primitives == null || primitives.isEmpty()) return Collections.emptyList();

        List<ChangesetDto.OsmPrimitiveBlockDto> block = new ArrayList<>();

        block.add(new ChangesetDto.OsmPrimitiveBlockDto(
                primitives.stream()
                        .filter(Objects::nonNull)
                        .map(OsmPrimitiveMapper::toDto)
                        .toList()
        ));

        return block;
    }

    /**
     * Map OsmPrimitive List dto to domain.
     */
    private List<OsmPrimitive> mapOsmPrimitivesToDomain(List<ChangesetDto.OsmPrimitiveBlockDto> blocks) {
        if (blocks == null || blocks.isEmpty()) return Collections.emptyList();

        List<OsmPrimitive> primitives = new ArrayList<>();
        for (ChangesetDto.OsmPrimitiveBlockDto block : blocks) {
            primitives.addAll(
                    block.getPrimitives().stream()
                            .filter(Objects::nonNull)
                            .map(OsmPrimitiveMapper::toDomain)
                            .toList()
            );
        }

        return primitives;
    }
}
