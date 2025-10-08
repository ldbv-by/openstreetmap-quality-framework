package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto.FeatureDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto.RelationDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto.TaggedObjectsDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.model.Feature;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.model.Relation;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.model.TaggedObjects;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping between {@link TaggedObjects} and {@link TaggedObjectsDto}.
 */
@UtilityClass
public class TaggedObjectsMapper {

    /**
     * Map tagged objects to domain.
     */
    public TaggedObjects toDomain(TaggedObjectsDto taggedObjectsDto) {
        if (taggedObjectsDto == null) return null;

        TaggedObjects taggedObjects = new TaggedObjects();

        for (FeatureDto node : taggedObjectsDto.nodes()) {
            taggedObjects.getNodes().add(FeatureMapper.toDomain(node));
        }

        for (FeatureDto way : taggedObjectsDto.ways()) {
            taggedObjects.getWays().add(FeatureMapper.toDomain(way));
        }

        for (FeatureDto area : taggedObjectsDto.areas()) {
            taggedObjects.getAreas().add(FeatureMapper.toDomain(area));
        }

        for (RelationDto relation : taggedObjectsDto.relations()) {
            taggedObjects.getRelations().add(RelationMapper.toDomain(relation));
        }

        return taggedObjects;
    }

    /**
     * Map tagged objects to dto.
     */
    public TaggedObjectsDto toDto(TaggedObjects taggedObjects) {
        if (taggedObjects == null) return null;

        List<FeatureDto> nodesDto = new ArrayList<>();
        for (Feature node : taggedObjects.getNodes()) {
            nodesDto.add(FeatureMapper.toDto(node));
        }

        List<FeatureDto> waysDto = new ArrayList<>();
        for (Feature way : taggedObjects.getWays()) {
            waysDto.add(FeatureMapper.toDto(way));
        }

        List<FeatureDto> areasDto = new ArrayList<>();
        for (Feature area : taggedObjects.getAreas()) {
            areasDto.add(FeatureMapper.toDto(area));
        }

        List<RelationDto> relationsDto = new ArrayList<>();
        for (Relation relation : taggedObjects.getRelations()) {
            relationsDto.add(RelationMapper.toDto(relation));
        }

        return new TaggedObjectsDto(
                nodesDto, waysDto, areasDto, relationsDto
        );
    }
}
