package de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto.NodeDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto.OsmPrimitiveDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto.RelationDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto.WayDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.model.Node;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.model.OsmPrimitive;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.model.Relation;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.model.Way;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OsmPrimitiveMapper {

    /**
     * Map OsmPrimitive dto to domain.
     */
    public OsmPrimitive toDomain(OsmPrimitiveDto dto) {
        return switch (dto) {
            case NodeDto nodeDto -> NodeMapper.toDomain(nodeDto);
            case WayDto wayDto -> WayMapper.toDomain(wayDto);
            case RelationDto relationDto -> RelationMapper.toDomain(relationDto);
            case null, default -> null;
        };

    }

    /**
     * Map OsmPrimitive domain to dto.
     */
    public OsmPrimitiveDto toDto(OsmPrimitive domain) {
        return switch (domain) {
            case Node node -> NodeMapper.toDto(node);
            case Way way -> WayMapper.toDto(way);
            case Relation relation -> RelationMapper.toDto(relation);
            case null, default -> null;
        };

    }
}
