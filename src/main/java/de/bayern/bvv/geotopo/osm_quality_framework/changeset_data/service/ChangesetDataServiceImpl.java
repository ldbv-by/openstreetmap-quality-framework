package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.service;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.ChangesetObjectEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository.*;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.api.ChangesetDataService;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.AreaEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.NodeEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.RelationEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.WayEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.mapper.AreaEntityMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.mapper.NodeEntityMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.mapper.RelationEntityMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.mapper.WayEntityMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.api.OsmGeometriesService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.ChangesetDataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.ChangesetDataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.DataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Service for the changeset_data schema. (implementation)
 */
@Service
@RequiredArgsConstructor
public class ChangesetDataServiceImpl implements ChangesetDataService {

    private final ChangesetDataRepository changesetDataRepository;
    private final ChangesetObjectRepository changesetObjectRepository;

    private final ChangesetNodeRepository nodeRepository;
    private final ChangesetWayRepository wayRepository;
    private final ChangesetAreaRepository areaRepository;
    private final ChangesetRelationRepository relationRepository;

    private final OsmGeometriesService osmGeometriesService;

    /**
     * Moves the prepared data for a given changeset into the changeset_data schema.
     */
    @Override
    public void movePreparedChangeset(Long changesetId, ChangesetDto changesetDto) {
        Changeset changeset = ChangesetMapper.toDomain(changesetId, changesetDto);

        this.changesetDataRepository.deleteChangesetData(changesetId);
        this.changesetDataRepository.copyPreparedData(changesetId);
        this.changesetDataRepository.insertChangesetObjects(changeset);
    }

    /**
     * Returns the current objects of a changeset matching the given filter.
     */
    @Override
    public ChangesetDataSetDto getDataSet(Long changesetId, FeatureFilter featureFilter, String coordinateReferenceSystem) {
        ChangesetDataSet result = new ChangesetDataSet();

        // --- Load all changeset objects for the given id.
        List<ChangesetObjectEntity> changesetObjects =
                this.changesetObjectRepository.findByChangesetId(changesetId);

        // --- Helper function: Filter osm ids from the changeset objects by operation and geometry types.
        BiFunction<Set<GeometryType>, OperationType, Set<Long>> osmIds =
                (geometryTypes, operationType) -> changesetObjects.stream()
                        .filter(o -> geometryTypes.contains(o.getGeometryType()) && o.getOperationType() == operationType)
                        .map(ChangesetObjectEntity::getOsmId)
                        .collect(Collectors.toSet());

        // --- Helper function: Creates a derived feature filter that using the given osm ids.
        BiFunction<Set<Long>, int[], FeatureFilter> ff = (osmIdsSet, slot) -> new FeatureFilter(
                new OsmIds(
                        slot[0] == 1 ? osmIdsSet : null, // nodes
                        slot[1] == 1 ? osmIdsSet : null, // ways
                        slot[2] == 1 ? osmIdsSet : null, // areas
                        slot[3] == 1 ? osmIdsSet : null  // relations
                ),
                featureFilter == null ? null : featureFilter.tags(),
                featureFilter == null ? null : featureFilter.boundingBox()
        );

        final Set<GeometryType> N = Set.of(GeometryType.NODE);
        final Set<GeometryType> W = Set.of(GeometryType.WAY);
        final Set<GeometryType> A = Set.of(GeometryType.AREA, GeometryType.MULTIPOLYGON);
        final Set<GeometryType> R = Set.of(GeometryType.RELATION);

        // CREATE
        result.getCreate().getNodes().addAll(
                getNodesByFeatureFilter(changesetId, ff.apply(osmIds.apply(N, OperationType.CREATE), new int[]{1,0,0,0}), coordinateReferenceSystem));
        result.getCreate().getWays().addAll(
                getWaysByFeatureFilter(changesetId, ff.apply(osmIds.apply(W, OperationType.CREATE), new int[]{0,1,0,0}), coordinateReferenceSystem));
        result.getCreate().getAreas().addAll(
                getAreasByFeatureFilter(changesetId, ff.apply(osmIds.apply(A, OperationType.CREATE), new int[]{0,0,1,0}), coordinateReferenceSystem));
        result.getCreate().getRelations().addAll(
                getRelationsByFeatureFilter(changesetId, ff.apply(osmIds.apply(R, OperationType.CREATE), new int[]{0,0,0,1})));

        // MODIFY
        result.getModify().getNodes().addAll(
                getNodesByFeatureFilter(changesetId, ff.apply(osmIds.apply(N, OperationType.MODIFY), new int[]{1,0,0,0}), coordinateReferenceSystem));
        result.getModify().getWays().addAll(
                getWaysByFeatureFilter(changesetId, ff.apply(osmIds.apply(W, OperationType.MODIFY), new int[]{0,1,0,0}), coordinateReferenceSystem));
        result.getModify().getAreas().addAll(
                getAreasByFeatureFilter(changesetId, ff.apply(osmIds.apply(A, OperationType.MODIFY), new int[]{0,0,1,0}), coordinateReferenceSystem));
        result.getModify().getRelations().addAll(
                getRelationsByFeatureFilter(changesetId, ff.apply(osmIds.apply(R, OperationType.MODIFY), new int[]{0,0,0,1})));

        // DELETE (from openstreetmap_geometries)
        FeatureFilter deleteFeatureFilter = new FeatureFilter(
                new OsmIds(
                        osmIds.apply(N, OperationType.DELETE),
                        osmIds.apply(W, OperationType.DELETE),
                        osmIds.apply(A, OperationType.DELETE),
                        osmIds.apply(R, OperationType.DELETE)
                ),
                featureFilter == null ? null : featureFilter.tags(),
                featureFilter == null ? null : featureFilter.boundingBox()
        );

        result.setDelete(Optional.ofNullable(this.osmGeometriesService.getDataSet(deleteFeatureFilter, coordinateReferenceSystem))
                .map(DataSetMapper::toDomain).orElse(null));

        return ChangesetDataSetMapper.toDto(result);
    }

    /**
     * Returns a data set of all relation members.
     */
    @Override
    public DataSetDto getRelationMembers(Long changesetId, Long relationId, String role, String coordinateReferenceSystem) {
        DataSet resultDataSet = new DataSet();

        resultDataSet.getNodes().addAll(this.getRelationMemberNodes(changesetId, relationId, role, coordinateReferenceSystem));
        resultDataSet.getWays().addAll(this.getRelationMemberWays(changesetId, relationId, role, coordinateReferenceSystem));
        resultDataSet.getAreas().addAll(this.getRelationMemberAreas(changesetId, relationId, role, coordinateReferenceSystem));
        resultDataSet.getRelations().addAll(this.getRelationMemberRelations(changesetId, relationId, role));

        return DataSetMapper.toDto(resultDataSet);
    }

    /**
     * Returns the current nodes by feature filter.
     */
    private List<Feature> getNodesByFeatureFilter(Long changesetId, FeatureFilter featureFilter, String coordinateReferenceSystem) {
        List<Feature> nodes = new ArrayList<>();
        List<NodeEntity> nodeEntities = this.nodeRepository.fetchByFeatureFilter(changesetId, featureFilter);

        if (nodeEntities != null) {
            for (NodeEntity nodeEntity : nodeEntities) {
                List<Relation> relations = this.getRelationsForOsmObject(changesetId,"n", nodeEntity.getOsmId());
                nodes.add(NodeEntityMapper.toFeature(nodeEntity, relations, coordinateReferenceSystem));
            }
        }

        return nodes;
    }

    /**
     * Returns the current relation member areas for a relation id.
     */
    private List<Feature> getRelationMemberNodes(Long changesetId, Long relationId, String role, String coordinateReferenceSystem) {
        List<Feature> nodes = new ArrayList<>();
        List<NodeEntity> nodeEntities = this.nodeRepository.fetchByRelationIdAndRole(changesetId, relationId, role);

        if (nodeEntities != null) {
            for (NodeEntity nodeEntity : nodeEntities) {
                List<Relation> relations = this.getRelationsForOsmObject(changesetId,"n", nodeEntity.getOsmId());
                nodes.add(NodeEntityMapper.toFeature(nodeEntity, relations, coordinateReferenceSystem));
            }
        }

        return nodes;
    }

    /**
     * Returns the current ways by feature filter.
     */
    private List<Feature> getWaysByFeatureFilter(Long changesetId, FeatureFilter featureFilter, String coordinateReferenceSystem) {
        List<Feature> ways = new ArrayList<>();
        List<WayEntity> wayEntities = this.wayRepository.fetchByFeatureFilter(changesetId, featureFilter);

        if (wayEntities != null) {
            for (WayEntity wayEntity : wayEntities) {
                List<Relation> relations = this.getRelationsForOsmObject(changesetId,"w", wayEntity.getOsmId());
                ways.add(WayEntityMapper.toFeature(wayEntity, relations, coordinateReferenceSystem));
            }
        }

        return ways;
    }

    /**
     * Returns the current relation member areas for a relation id.
     */
    private List<Feature> getRelationMemberWays(Long changesetId, Long relationId, String role, String coordinateReferenceSystem) {
        List<Feature> ways = new ArrayList<>();
        List<WayEntity> wayEntities = this.wayRepository.fetchByRelationIdAndRole(changesetId, relationId, role);

        if (wayEntities != null) {
            for (WayEntity wayEntity : wayEntities) {
                List<Relation> relations = this.getRelationsForOsmObject(changesetId,"w", wayEntity.getOsmId());
                ways.add(WayEntityMapper.toFeature(wayEntity, relations, coordinateReferenceSystem));
            }
        }

        return ways;
    }

    /**
     * Returns the current areas by feature filter.
     */
    private List<Feature> getAreasByFeatureFilter(Long changesetId, FeatureFilter featureFilter, String coordinateReferenceSystem) {
        List<Feature> areas = new ArrayList<>();
        List<AreaEntity> areaEntities = this.areaRepository.fetchByFeatureFilter(changesetId, featureFilter);

        if (areaEntities != null) {
            for (AreaEntity areaEntity : areaEntities) {
                List<Relation> relations = this.getRelationsForOsmObject(changesetId, areaEntity.getOsmGeometryType().toString(), areaEntity.getOsmId());
                areas.add(AreaEntityMapper.toFeature(areaEntity, relations, coordinateReferenceSystem));
            }
        }

        return areas;
    }

    /**
     * Returns the current relation member areas for a relation id.
     */
    private List<Feature> getRelationMemberAreas(Long changesetId, Long relationId, String role, String coordinateReferenceSystem) {
        List<Feature> areas = new ArrayList<>();
        List<AreaEntity> areaEntities = this.areaRepository.fetchByRelationIdAndRole(changesetId, relationId, role);

        if (areaEntities != null) {
            for (AreaEntity areaEntity : areaEntities) {
                List<Relation> relations = this.getRelationsForOsmObject(changesetId, areaEntity.getOsmGeometryType().toString(), areaEntity.getOsmId());
                areas.add(AreaEntityMapper.toFeature(areaEntity, relations, coordinateReferenceSystem));
            }
        }

        return areas;
    }

    /**
     * Returns the current relations by feature filter.
     */
    private List<Relation> getRelationsByFeatureFilter(Long changesetId, FeatureFilter featureFilter) {
        List<Relation> relations = new ArrayList<>();
        List<RelationEntity> relationEntities = this.relationRepository.fetchByFeatureFilter(changesetId, featureFilter);

        if (relationEntities != null) {
            for (RelationEntity relationEntity : relationEntities) {
                List<Relation> rels = this.getRelationsForOsmObject(changesetId,"r", relationEntity.getOsmId());
                relations.add(RelationEntityMapper.toRelation(relationEntity, rels));
            }
        }

        return relations;
    }

    /**
     * Returns the current relation member areas for a relation id.
     */
    private List<Relation> getRelationMemberRelations(Long changesetId, Long relationId, String role) {
        List<Relation> relations = new ArrayList<>();
        List<RelationEntity> relationEntities = this.relationRepository.fetchByRelationIdAndRole(changesetId, relationId, role);

        if (relationEntities != null) {
            for (RelationEntity relationEntity : relationEntities) {
                List<Relation> rels = this.getRelationsForOsmObject(changesetId,"r", relationEntity.getOsmId());
                relations.add(RelationEntityMapper.toRelation(relationEntity, rels));
            }
        }

        return relations;
    }

    /**
     * Returns the current relations for an osm object.
     */
    private List<Relation> getRelationsForOsmObject(Long changesetId, String memberType, Long memberOsmId) {
        List<Relation> relations = new ArrayList<>();
        List<RelationEntity> relationEntities = this.relationRepository.findAllByMember(changesetId, memberType, memberOsmId);

        for (RelationEntity relationEntity : relationEntities) {
            List<Relation> rels = this.getRelationsForOsmObject(changesetId,"r", relationEntity.getOsmId());
            relations.add(RelationEntityMapper.toRelation(relationEntity, rels));
        }

        return relations;
    }
}
