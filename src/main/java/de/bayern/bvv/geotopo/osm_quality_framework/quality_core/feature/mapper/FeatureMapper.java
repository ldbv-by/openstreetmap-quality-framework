package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto.FeatureDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto.GeometryNodeDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.model.Feature;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.model.GeometryNode;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping between {@link Feature} and {@link FeatureDto}.
 */
@UtilityClass
public class FeatureMapper {

    /**
     * Map feature to domain.
     */
    public Feature toDomain(FeatureDto featureDto) {
        if (featureDto == null) return null;

        Feature feature = new Feature();
        feature.setOsmId(featureDto.osmId());
        feature.setObjectType(featureDto.objectType());
        feature.setTags(featureDto.tags());
        feature.setMemberOf(featureDto.memberOf());
        feature.setGeometry(featureDto.geometry());
        feature.setGeometryTransformed(featureDto.geometryTransformed());

        for (GeometryNodeDto geometryNodeDto : featureDto.geometryNodes()) {
            feature.getGeometryNodes().add(GeometryNodeMapper.toDomain(geometryNodeDto));
        }

        return feature;
    }

    /**
     * Map feature to dto.
     */
    public FeatureDto toDto(Feature feature) {
        if (feature == null) return null;

        List<GeometryNodeDto> geometryNodesDto = new ArrayList<>();
        for (GeometryNode geometryNode : feature.getGeometryNodes()) {
            geometryNodesDto.add(GeometryNodeMapper.toDto(geometryNode));
        }

        return new FeatureDto(
                feature.getOsmId(),
                feature.getObjectType(),
                feature.getTags(),
                feature.getMemberOf(),
                feature.getGeometry(),
                feature.getGeometryTransformed(),
                geometryNodesDto);
    }
}
