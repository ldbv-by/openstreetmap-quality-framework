package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto.GeometryNodeDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.model.GeometryNode;
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
                geometryNode.getGeometry(),
                geometryNode.getGeometryTransformed(),
                geometryNode.getSequence());
    }
}
