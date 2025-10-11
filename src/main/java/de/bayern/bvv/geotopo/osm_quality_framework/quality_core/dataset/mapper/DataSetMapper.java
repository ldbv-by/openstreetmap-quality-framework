package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.FeatureDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.RelationDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Feature;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Relation;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.DataSet;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping between {@link DataSet} and {@link DataSetDto}.
 */
@UtilityClass
public class DataSetMapper {

    /**
     * Map tagged objects to domain.
     */
    public DataSet toDomain(DataSetDto dataSetDto) {
        if (dataSetDto == null) return null;

        DataSet dataSet = new DataSet();

        for (FeatureDto node : dataSetDto.nodes()) {
            dataSet.getNodes().add(FeatureMapper.toDomain(node));
        }

        for (FeatureDto way : dataSetDto.ways()) {
            dataSet.getWays().add(FeatureMapper.toDomain(way));
        }

        for (FeatureDto area : dataSetDto.areas()) {
            dataSet.getAreas().add(FeatureMapper.toDomain(area));
        }

        for (RelationDto relation : dataSetDto.relations()) {
            dataSet.getRelations().add(RelationMapper.toDomain(relation));
        }

        return dataSet;
    }

    /**
     * Map tagged objects to dto.
     */
    public DataSetDto toDto(DataSet dataSet) {
        if (dataSet == null) return null;

        List<FeatureDto> nodesDto = new ArrayList<>();
        for (Feature node : dataSet.getNodes()) {
            nodesDto.add(FeatureMapper.toDto(node));
        }

        List<FeatureDto> waysDto = new ArrayList<>();
        for (Feature way : dataSet.getWays()) {
            waysDto.add(FeatureMapper.toDto(way));
        }

        List<FeatureDto> areasDto = new ArrayList<>();
        for (Feature area : dataSet.getAreas()) {
            areasDto.add(FeatureMapper.toDto(area));
        }

        List<RelationDto> relationsDto = new ArrayList<>();
        for (Relation relation : dataSet.getRelations()) {
            relationsDto.add(RelationMapper.toDto(relation));
        }

        return new DataSetDto(
                nodesDto, waysDto, areasDto, relationsDto
        );
    }
}
