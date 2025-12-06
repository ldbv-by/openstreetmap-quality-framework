package de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.service;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.component.Osm2PgSqlClient;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.entity.*;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.mapper.*;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.repository.*;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.api.ChangesetManagementService;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.repository.ChangesetPrepareRepository;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.api.OsmGeometriesService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.ChangesetState;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.ChangesetDataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.ChangesetDataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.DataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the Changeset-Management service.
 * <p>
 * This service is responsible for persisting and preparing changeset data in the
 * changeset_data schema and for providing filtered OSM features of the changeset to other
 * bounded contexts during the validation workflow.
 */
@Service
@RequiredArgsConstructor
public class ChangesetManagementServiceImpl implements ChangesetManagementService {

    private final ChangesetDataRepository changesetDataRepository;
    private final ChangesetPrepareRepository changesetPrepareRepository;
    private final ChangesetRepository changesetRepository;
    private final ChangesetObjectRepository changesetObjectRepository;

    private final ChangesetNodeRepository nodeRepository;
    private final ChangesetWayRepository wayRepository;
    private final ChangesetAreaRepository areaRepository;
    private final ChangesetRelationRepository relationRepository;
    private final ChangesetAreaNodeRepository areaNodeRepository;
    private final ChangesetWayNodeRepository wayNodeRepository;

    private final OsmGeometriesService osmGeometriesService;

    private final Osm2PgSqlClient osm2PgSqlClient;

    /**
     * Prepares the given changeset for validation by normalizing and
     * persisting its data in the changeset_data schema.
     */
    @Override
    public void persistChangeset(Long changesetId, ChangesetDto changesetDto) {
        Changeset changeset = ChangesetMapper.toDomain(changesetId, changesetDto);

        try {
            // ----- Persist changeset and dependent OSM objects in the temporary prepare schema.
            this.changesetPrepareRepository.createSchemaByChangesetId(changesetId);
            this.changesetPrepareRepository.insertDependingOsmObjects(changeset);
            this.osm2PgSqlClient.appendChangeset(changeset);

            // ----- Initialize changeset in the main changeset_data schema and copy prepared data.
            this.changesetDataRepository.deleteChangesetData(changesetId);
            this.changesetDataRepository.initChangeset(changeset);
            this.changesetDataRepository.copyPreparedData(changesetId);
            this.changesetDataRepository.insertChangesetObjects(changeset);
        } finally {
            // ----- Clean up temporary prepare schema, independent of success or failure.
            this.changesetPrepareRepository.dropSchemaByChangeset(changesetId);
        }
    }

    /**
     * Retrieves the current OSM objects of the given changeset, filtered according
     * to the supplied dataset filter.
     */
    @Override
    public ChangesetDataSetDto getDataSet(Long changesetId, DataSetFilter dataSetFilter) {
        ChangesetDataSet result = new ChangesetDataSet();

        Criteria criteria = (dataSetFilter == null) ? null : dataSetFilter.criteria();
        String coordinateReferenceSystem = (dataSetFilter == null) ? null : dataSetFilter.coordinateReferenceSystem();

        Set<Long> nodeOsmIds = (dataSetFilter == null || dataSetFilter.osmIds() == null) ? null : dataSetFilter.osmIds().nodeIds();
        Set<Long> wayOsmIds = (dataSetFilter == null || dataSetFilter.osmIds() == null) ? null : dataSetFilter.osmIds().wayIds();
        Set<Long> areaOsmIds = (dataSetFilter == null || dataSetFilter.osmIds() == null) ? null : dataSetFilter.osmIds().areaIds();
        Set<Long> relationOsmIds = (dataSetFilter == null || dataSetFilter.osmIds() == null) ? null : dataSetFilter.osmIds().relationIds();

        // ----- Load all changeset objects for the given id.
        List<ChangesetObjectEntity> changesetObjects =
                this.changesetObjectRepository.findByChangesetId(changesetId);

        final Set<GeometryType> N = Set.of(GeometryType.NODE);
        final Set<GeometryType> W = Set.of(GeometryType.WAY);
        final Set<GeometryType> A = Set.of(GeometryType.AREA, GeometryType.MULTIPOLYGON);
        final Set<GeometryType> R = Set.of(GeometryType.RELATION);

        // ----- CREATE operations
        List<Relation> relations = new ArrayList<>();
        OsmIds osmIds = this.getOsmIds(changesetObjects, N, OperationType.CREATE, new int[]{1,0,0,0}, nodeOsmIds);
        result.getCreate().getNodes().addAll(getNodesByFeatureFilter(changesetId, osmIds, criteria, coordinateReferenceSystem, relations));

        osmIds = this.getOsmIds(changesetObjects, W, OperationType.CREATE, new int[]{0,1,0,0}, wayOsmIds);
        result.getCreate().getWays().addAll(getWaysByFeatureFilter(changesetId, osmIds, criteria, coordinateReferenceSystem, relations));

        osmIds = this.getOsmIds(changesetObjects, A, OperationType.CREATE, new int[]{0,0,1,0}, areaOsmIds);
        result.getCreate().getAreas().addAll(getAreasByFeatureFilter(changesetId, osmIds, criteria, coordinateReferenceSystem, relations));

        osmIds = this.getOsmIds(changesetObjects, R, OperationType.CREATE, new int[]{0,0,0,1}, relationOsmIds);
        if (osmIds.relationIds() != null) {
            result.getCreate().getRelations().addAll(getRelationsByFeatureFilter(changesetId, osmIds, criteria));
        } else {
            result.getCreate().getRelations().addAll(relations);
        }

        // ----- MODIFY operations
        relations = new ArrayList<>();
        osmIds = this.getOsmIds(changesetObjects, N, OperationType.MODIFY, new int[]{1,0,0,0}, nodeOsmIds);
        result.getModify().getNodes().addAll(getNodesByFeatureFilter(changesetId, osmIds, criteria, coordinateReferenceSystem, relations));

        osmIds = this.getOsmIds(changesetObjects, W, OperationType.MODIFY, new int[]{0,1,0,0}, wayOsmIds);
        result.getModify().getWays().addAll(getWaysByFeatureFilter(changesetId, osmIds, criteria, coordinateReferenceSystem, relations));

        osmIds = this.getOsmIds(changesetObjects, A, OperationType.MODIFY, new int[]{0,0,1,0}, areaOsmIds);
        result.getModify().getAreas().addAll(getAreasByFeatureFilter(changesetId, osmIds, criteria, coordinateReferenceSystem, relations));

        osmIds = this.getOsmIds(changesetObjects, R, OperationType.MODIFY, new int[]{0,0,0,1}, relationOsmIds);
        if (osmIds.relationIds() != null) {
            result.getModify().getRelations().addAll(getRelationsByFeatureFilter(changesetId, osmIds, criteria));
        } else {
            result.getModify().getRelations().addAll(relations);
        }

        // ----- DELETE operations (loaded from openstreetmap_geometries)
        DataSetFilter deleteDataSetFilter = new DataSetFilter(
             dataSetFilter == null ? null : dataSetFilter.ignoreChangesetData(),
             dataSetFilter == null ? null : dataSetFilter.coordinateReferenceSystem(),
             dataSetFilter == null ? null : dataSetFilter.aggregator(),
             new OsmIds(
                     this.getOsmIds(changesetObjects, N, OperationType.DELETE, nodeOsmIds),
                     this.getOsmIds(changesetObjects, W, OperationType.DELETE, wayOsmIds),
                     this.getOsmIds(changesetObjects, A, OperationType.DELETE, areaOsmIds),
                     this.getOsmIds(changesetObjects, R, OperationType.DELETE, relationOsmIds)
             ),
            dataSetFilter == null ? null : dataSetFilter.criteria(),
            dataSetFilter == null ? null : dataSetFilter.memberFilter()
        );

        result.setDelete(Optional.ofNullable(this.osmGeometriesService.getDataSet(deleteDataSetFilter))
                .map(DataSetMapper::toDomain).orElse(null));

        return ChangesetDataSetMapper.toDto(result);
    }

    /**
     * Returns a dataset of all relation members for the given relation in the specified changeset.
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
     * Retrieves all node features of the given changeset matching the provided filter criteria.
     */
    private List<Feature> getNodesByFeatureFilter(Long changesetId, OsmIds osmIds, Criteria criteria, String coordinateReferenceSystem, List<Relation> relations) {
        List<Feature> nodes = new ArrayList<>();
        List<NodeEntity> nodeEntities = this.nodeRepository.fetchByFeatureFilter(changesetId, osmIds, criteria);

        if (nodeEntities != null) {
            for (NodeEntity nodeEntity : nodeEntities) {
                List<Relation> loadedNodeRelations = this.getRelationsForOsmObject(changesetId,"n", nodeEntity.getOsmId());

                List<Relation> nodeRelations = new ArrayList<>();
                for (Relation nodeRelation : loadedNodeRelations) {
                    int idx = relations.indexOf(nodeRelation);
                    if (idx < 0) {
                        relations.add(nodeRelation);
                    } else {
                        nodeRelation = relations.get(idx);
                    }
                    nodeRelations.add(nodeRelation);
                }

                nodes.add(NodeEntityMapper.toFeature(nodeEntity, nodeRelations, coordinateReferenceSystem));
            }
        }

        return nodes;
    }

    /**
     * Retrieves all node features that are members of the given relation.
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
     * Retrieves all way features of the given changeset matching the provided filter criteria.
     */
    private List<Feature> getWaysByFeatureFilter(Long changesetId, OsmIds osmIds, Criteria criteria, String coordinateReferenceSystem, List<Relation> relations) {
        List<Feature> ways = new ArrayList<>();
        List<WayEntity> wayEntities = this.wayRepository.fetchByFeatureFilter(changesetId, osmIds, criteria);

        if (wayEntities != null) {
            for (WayEntity wayEntity : wayEntities) {
                List<Relation> loadedWayRelations = this.getRelationsForOsmObject(changesetId,"w", wayEntity.getOsmId());

                List<Relation> wayRelations = new ArrayList<>();
                for (Relation wayRelation : loadedWayRelations) {
                    int idx = relations.indexOf(wayRelation);
                    if (idx < 0) {
                        relations.add(wayRelation);
                    } else {
                        wayRelation = relations.get(idx);
                    }
                    wayRelations.add(wayRelation);
                }

                List<GeometryNode> geometryNodes = this.getGeometryNodes(changesetId, wayEntity, coordinateReferenceSystem);
                ways.add(WayEntityMapper.toFeature(wayEntity, geometryNodes, wayRelations, coordinateReferenceSystem));
            }
        }

        return ways;
    }

    /**
     * Retrieves all way features that are members of the given relation.
     */
    private List<Feature> getRelationMemberWays(Long changesetId, Long relationId, String role, String coordinateReferenceSystem) {
        List<Feature> ways = new ArrayList<>();
        List<WayEntity> wayEntities = this.wayRepository.fetchByRelationIdAndRole(changesetId, relationId, role);

        if (wayEntities != null) {
            for (WayEntity wayEntity : wayEntities) {
                List<Relation> relations = this.getRelationsForOsmObject(changesetId,"w", wayEntity.getOsmId());
                List<GeometryNode> geometryNodes = this.getGeometryNodes(changesetId, wayEntity, coordinateReferenceSystem);
                ways.add(WayEntityMapper.toFeature(wayEntity, geometryNodes, relations, coordinateReferenceSystem));
            }
        }

        return ways;
    }

    /**
     * Retrieves all area features of the given changeset matching the provided filter criteria.
     */
    private List<Feature> getAreasByFeatureFilter(Long changesetId, OsmIds osmIds, Criteria criteria, String coordinateReferenceSystem, List<Relation> relations) {
        List<Feature> areas = new ArrayList<>();
        List<AreaEntity> areaEntities = this.areaRepository.fetchByFeatureFilter(changesetId, osmIds, criteria);

        if (areaEntities != null) {
            for (AreaEntity areaEntity : areaEntities) {
                List<Relation> loadedAreaRelations = this.getRelationsForOsmObject(changesetId, areaEntity.getOsmGeometryType().toString(), areaEntity.getOsmId());

                List<Relation> areaRelations = new ArrayList<>();
                for (Relation areaRelation : loadedAreaRelations) {
                    int idx = relations.indexOf(areaRelation);
                    if (idx < 0) {
                        relations.add(areaRelation);
                    } else {
                        areaRelation = relations.get(idx);
                    }
                    areaRelations.add(areaRelation);
                }

                List<GeometryNode> geometryNodes = this.getGeometryNodes(changesetId, areaEntity, coordinateReferenceSystem);
                areas.add(AreaEntityMapper.toFeature(areaEntity, geometryNodes, areaRelations, coordinateReferenceSystem));
            }
        }

        return areas;
    }

    /**
     * Retrieves all area features that are members of the given relation.
     */
    private List<Feature> getRelationMemberAreas(Long changesetId, Long relationId, String role, String coordinateReferenceSystem) {
        List<Feature> areas = new ArrayList<>();
        List<AreaEntity> areaEntities = this.areaRepository.fetchByRelationIdAndRole(changesetId, relationId, role);

        if (areaEntities != null) {
            for (AreaEntity areaEntity : areaEntities) {
                List<Relation> relations = this.getRelationsForOsmObject(changesetId, areaEntity.getOsmGeometryType().toString(), areaEntity.getOsmId());
                List<GeometryNode> geometryNodes = this.getGeometryNodes(changesetId, areaEntity, coordinateReferenceSystem);
                areas.add(AreaEntityMapper.toFeature(areaEntity, geometryNodes, relations, coordinateReferenceSystem));
            }
        }

        return areas;
    }

    /**
     * Retrieves all relation features of the given changeset matching the provided filter criteria.
     */
    private List<Relation> getRelationsByFeatureFilter(Long changesetId, OsmIds osmIds, Criteria criteria) {
        List<Relation> relations = new ArrayList<>();
        List<RelationEntity> relationEntities = this.relationRepository.fetchByFeatureFilter(changesetId, osmIds, criteria);

        if (relationEntities != null) {
            for (RelationEntity relationEntity : relationEntities) {
                List<Relation> rels = this.getRelationsForOsmObject(changesetId,"r", relationEntity.getOsmId());
                relations.add(RelationEntityMapper.toRelation(relationEntity, rels));
            }
        }

        return relations;
    }

    /**
     * Retrieves all relation features that are members of the given relation.
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
     * Resolves all relations that reference the given OSM object as a member.
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

    /**
     * Updates the lifecycle state of the given changeset.
     */
    @Override
    @Transactional
    public void setChangesetState(Long changesetId, ChangesetState state) {
        ChangesetEntity cs = this.changesetRepository.findById(changesetId)
                .orElseThrow(() -> new IllegalArgumentException("Changeset %d not found".formatted(changesetId)));

        cs.setState(state);
        if (state == ChangesetState.FINISHED || state == ChangesetState.CANCELLED) {
            cs.setClosedAt(Instant.now());
        } else {
            cs.setClosedAt(null);
        }

        this.changesetRepository.save(cs);
    }

    /**
     * Returns the IDs of all changesets that are currently in one of the
     * given lifecycle states, ordered by their creation time.
     */
    @Override
    public List<Long> getChangesetIds(Set<ChangesetState> states) {
        return this.changesetRepository.findIdsByStatesOrderByCreatedAtAsc(states);
    }

    /**
     * Returns the geometry nodes for the given area in the requested coordinate reference system.
     */
    private List<GeometryNode> getGeometryNodes(Long changesetId, AreaEntity areaEntity, String coordinateReferenceSystem) {
        return this.areaNodeRepository.findByIdAreaOsmIdAndIdChangesetIdOrderByIdSeq(areaEntity.getOsmId(), changesetId)
                .stream().map(n  -> AreaNodeEntityMapper.toGeometryNode(n, coordinateReferenceSystem))
                .toList();
    }

    /**
     * Returns the geometry nodes for the given way in the requested coordinate reference system.
     */
    private List<GeometryNode> getGeometryNodes(Long changesetId, WayEntity wayEntity, String coordinateReferenceSystem) {
        return this.wayNodeRepository.findByIdWayOsmIdAndIdChangesetIdOrderByIdSeq(wayEntity.getOsmId(), changesetId)
                .stream().map(n  -> WayNodeEntityMapper.toGeometryNode(n, coordinateReferenceSystem))
                .toList();
    }

    /**
     * Collects OSM IDs of changeset objects matching the specified geometry
     * types and operation type, optionally intersected with a given ID set.
     */
    private Set<Long> getOsmIds(List<ChangesetObjectEntity> changesetObjects, Set<GeometryType> geometryTypes,
                                OperationType operationType, Set<Long> inputOsmIds){

        return changesetObjects.stream()
                .filter(o -> geometryTypes.contains(o.getGeometryType()) && o.getOperationType() == operationType)
                .map(ChangesetObjectEntity::getOsmId)
                .filter(id -> inputOsmIds == null || inputOsmIds.contains(id))
                .collect(Collectors.toSet());
    }

    /**
     * Wraps the collected OSM IDs into an {@link OsmIds} structure, assigning
     * them to the respective geometry-type slots (nodes, ways, areas, relations).
     */
    private OsmIds getOsmIds(List<ChangesetObjectEntity> changesetObjects, Set<GeometryType> geometryTypes,
                             OperationType operationType, int[] slot, Set<Long> inputOsmIds) {
        Set<Long> osmIdsSet = this.getOsmIds(changesetObjects, geometryTypes, operationType, inputOsmIds);

        return new OsmIds(
                slot[0] == 1 ? osmIdsSet : null, // nodes
                slot[1] == 1 ? osmIdsSet : null, // ways
                slot[2] == 1 ? osmIdsSet : null, // areas
                slot[3] == 1 ? osmIdsSet : null  // relations
        );
    }
}
