package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.GeometryNodeDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.GeometryNode;
import lombok.experimental.UtilityClass;

/**
 * Mapping between {@link GeometryNode} and {@link GeometryNodeDto}.
 */
@UtilityClass
public class GeometryNodeMapper {

    /**
     * Map geometry node to domain.
     */
    public GeometryNode toDomain(GeometryNodeDto geometryNodeDto) {
        if (geometryNodeDto == null) return null;

        GeometryNode geometryNode = new GeometryNode();
        geometryNode.setOsmId(geometryNodeDto.osmId());
        geometryNode.setMemberOsmId(geometryNodeDto.memberOsmId());
        geometryNode.setGeometry(geometryNodeDto.geometry());
        geometryNode.setGeometryTransformed(geometryNodeDto.geometryTransformed());
        geometryNode.setSequence(geometryNodeDto.sequence());

        return geometryNode;
    }

    /**
     * Map geometry node to dto.
     */
    public GeometryNodeDto toDto(GeometryNode geometryNode) {
        if (geometryNode == null) return null;

        return new GeometryNodeDto(
                geometryNode.getOsmId(),
                geometryNode.getMemberOsmId(),
                geometryNode.getGeometry(),
                geometryNode.getGeometryTransformed(),
                geometryNode.getSequence());
    }
}
