package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.service;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.component.Osm2PgSqlClient;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.component.OsmApiClient;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.mapper.AreaNodeEntityMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.mapper.WayNodeEntityMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.AreaEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.NodeEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.RelationEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.WayEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.mapper.AreaEntityMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.mapper.NodeEntityMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.mapper.RelationEntityMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.mapper.WayEntityMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository.*;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.api.OsmGeometriesService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.DataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This service provides access to the authoritative OpenStreetMap geometry
 * dataset stored in the openstreetmap_geometries schema. It is
 * responsible for applying finalized changesets to the geometry store and
 * exposing the current nodes, ways, areas and relations for read access.
 */
@Service
@RequiredArgsConstructor
public class OsmGeometriesServiceImpl implements OsmGeometriesService {

    private final NodeRepository nodeRepository;
    private final WayRepository wayRepository;
    private final AreaRepository areaRepository;
    private final RelationRepository relationRepository;

    private final AreaNodeRepository areaNodeRepository;
    private final WayNodeRepository wayNodeRepository;

    private final SequenceRepository sequenceRepository;

    private final OsmApiClient osmApiClient;
    private final Osm2PgSqlClient osm2PgSqlClient;

    /**
     * Applies the given changeset to the OpenStreetMap-Geometries dataset.
     **/
    @Override
    public void appendChangeset(Long changesetId) {
        // ----- Read the persisted changeset from the OSM-DB.
        Changeset changeset = Optional.ofNullable(this.osmApiClient.getChangesetById(changesetId))
                .map(cs -> ChangesetMapper.toDomain(changesetId, cs))
                .orElse(null);

        // ----- Persist the changeset in the OpenStreetMap-Geometries schema
        if (changesetId != null) {
            this.osm2PgSqlClient.appendChangeset(changeset);
        }
    }

    /**
     * Returns the current OSM objects that match the given filter.
     */
    @Override
    public DataSetDto getDataSet(DataSetFilter dataSetFilter) {
        DataSet resultDataSet = new DataSet();

        Criteria criteria = (dataSetFilter == null) ? null : dataSetFilter.criteria();
        OsmIds osmIds = (dataSetFilter == null) ? null : dataSetFilter.osmIds();
        String coordinateReferenceSystem = (dataSetFilter == null) ? null : dataSetFilter.coordinateReferenceSystem();

        List<Relation> relations = new ArrayList<>();
        resultDataSet.getNodes().addAll(this.getNodesByFeatureFilter(osmIds, criteria, coordinateReferenceSystem, relations));
        resultDataSet.getWays().addAll(this.getWaysByFeatureFilter(osmIds, criteria, coordinateReferenceSystem, relations));
        resultDataSet.getAreas().addAll(this.getAreasByFeatureFilter(osmIds, criteria, coordinateReferenceSystem, relations));

        if (osmIds != null && osmIds.relationIds() != null && !osmIds.relationIds().isEmpty()) {
            resultDataSet.getRelations().addAll(this.getRelationsByFeatureFilter(osmIds, criteria));
        } else {
            resultDataSet.getRelations().addAll(relations);
        }

        return DataSetMapper.toDto(resultDataSet);
    }

    /**
     * Returns all members of the given OSM relation in the current geometry dataset.
     */
    @Override
    public DataSetDto getRelationMembers(Long relationId, String role, String coordinateReferenceSystem) {
        DataSet resultDataSet = new DataSet();

        resultDataSet.getNodes().addAll(this.getRelationMemberNodes(relationId, role, coordinateReferenceSystem));
        resultDataSet.getWays().addAll(this.getRelationMemberWays(relationId, role, coordinateReferenceSystem));
        resultDataSet.getAreas().addAll(this.getRelationMemberAreas(relationId, role, coordinateReferenceSystem));
        resultDataSet.getRelations().addAll(this.getRelationMemberRelations(relationId, role));

        return DataSetMapper.toDto(resultDataSet);
    }

    /**
     * Retrieves node features matching the given filter from the geometry store.
     */
    private List<Feature> getNodesByFeatureFilter(OsmIds osmIds, Criteria criteria, String coordinateReferenceSystem, List<Relation> relations) {
        List<Feature> nodes = new ArrayList<>();
        List<NodeEntity> nodeEntities = this.nodeRepository.fetchByFeatureFilter(osmIds, criteria);

        if (nodeEntities != null) {
            for (NodeEntity nodeEntity : nodeEntities) {
                List<Relation> loadedNodeRelations = this.getRelationsForOsmObject("n", nodeEntity.getOsmId());

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
     * Retrieves all node members of the given relation.
     */
    private List<Feature> getRelationMemberNodes(Long relationId, String role, String coordinateReferenceSystem) {
        List<Feature> nodes = new ArrayList<>();
        List<NodeEntity> nodeEntities = this.nodeRepository.fetchByRelationIdAndRole(relationId, role);

        if (nodeEntities != null) {
            for (NodeEntity nodeEntity : nodeEntities) {
                List<Relation> relations = this.getRelationsForOsmObject("n", nodeEntity.getOsmId());
                nodes.add(NodeEntityMapper.toFeature(nodeEntity, relations, coordinateReferenceSystem));
            }
        }

        return nodes;
    }

    /**
     * Retrieves way features matching the given filter from the geometry store.
     */
    private List<Feature> getWaysByFeatureFilter(OsmIds osmIds, Criteria criteria, String coordinateReferenceSystem, List<Relation> relations) {
        List<Feature> ways = new ArrayList<>();
        List<WayEntity> wayEntities = this.wayRepository.fetchByFeatureFilter(osmIds, criteria);

        if (wayEntities != null) {
            for (WayEntity wayEntity : wayEntities) {
                List<Relation> loadedWayRelations = this.getRelationsForOsmObject("w", wayEntity.getOsmId());

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

                List<GeometryNode> geometryNodes = this.getGeometryNodes(wayEntity, coordinateReferenceSystem);
                ways.add(WayEntityMapper.toFeature(wayEntity, geometryNodes, wayRelations, coordinateReferenceSystem));
            }
        }

        return ways;
    }

    /**
     * Retrieves all way members of the given relation.
     */
    private List<Feature> getRelationMemberWays(Long relationId, String role, String coordinateReferenceSystem) {
        List<Feature> ways = new ArrayList<>();
        List<WayEntity> wayEntities = this.wayRepository.fetchByRelationIdAndRole(relationId, role);

        if (wayEntities != null) {
            for (WayEntity wayEntity : wayEntities) {
                List<Relation> relations = this.getRelationsForOsmObject("w", wayEntity.getOsmId());
                List<GeometryNode> geometryNodes = this.getGeometryNodes(wayEntity, coordinateReferenceSystem);
                ways.add(WayEntityMapper.toFeature(wayEntity, geometryNodes, relations, coordinateReferenceSystem));
            }
        }

        return ways;
    }

    /**
     * Retrieves area features matching the given filter from the geometry store.
     */
    private List<Feature> getAreasByFeatureFilter(OsmIds osmIds, Criteria criteria, String coordinateReferenceSystem, List<Relation> relations) {
        List<Feature> areas = new ArrayList<>();
        List<AreaEntity> areaEntities = this.areaRepository.fetchByFeatureFilter(osmIds, criteria);

        if (areaEntities != null) {
            for (AreaEntity areaEntity : areaEntities) {
                List<Relation> loadedAreaRelations = this.getRelationsForOsmObject(areaEntity.getOsmGeometryType().toString(), areaEntity.getOsmId());

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

                List<GeometryNode> geometryNodes = this.getGeometryNodes(areaEntity, coordinateReferenceSystem);
                areas.add(AreaEntityMapper.toFeature(areaEntity, geometryNodes, areaRelations, coordinateReferenceSystem));
            }
        }

        return areas;
    }

    /**
     * Retrieves all area members of the given relation.
     */
    private List<Feature> getRelationMemberAreas(Long relationId, String role, String coordinateReferenceSystem) {
        List<Feature> areas = new ArrayList<>();
        List<AreaEntity> areaEntities = this.areaRepository.fetchByRelationIdAndRole(relationId, role);

        if (areaEntities != null) {
            for (AreaEntity areaEntity : areaEntities) {
                List<Relation> relations = this.getRelationsForOsmObject(areaEntity.getOsmGeometryType().toString(), areaEntity.getOsmId());
                List<GeometryNode> geometryNodes = this.getGeometryNodes(areaEntity, coordinateReferenceSystem);
                areas.add(AreaEntityMapper.toFeature(areaEntity, geometryNodes, relations, coordinateReferenceSystem));
            }
        }

        return areas;
    }

    /**
     * Retrieves relation features matching the given filter from the geometry store.
     */
    private List<Relation> getRelationsByFeatureFilter(OsmIds osmIds, Criteria criteria) {
        List<Relation> relations = new ArrayList<>();
        List<RelationEntity> relationEntities = this.relationRepository.fetchByFeatureFilter(osmIds, criteria);

        if (relationEntities != null) {
            for (RelationEntity relationEntity : relationEntities) {
                List<Relation> rels = this.getRelationsForOsmObject("r", relationEntity.getOsmId());
                relations.add(RelationEntityMapper.toRelation(relationEntity, rels));
            }
        }

        return relations;
    }

    /**
     * Retrieves all relation members that are themselves relations for the given relation.
     */
    private List<Relation> getRelationMemberRelations(Long relationId, String role) {
        List<Relation> relations = new ArrayList<>();
        List<RelationEntity> relationEntities = this.relationRepository.fetchByRelationIdAndRole(relationId, role);

        if (relationEntities != null) {
            for (RelationEntity relationEntity : relationEntities) {
                List<Relation> rels = this.getRelationsForOsmObject("r", relationEntity.getOsmId());
                relations.add(RelationEntityMapper.toRelation(relationEntity, rels));
            }
        }

        return relations;
    }

    /**
     * Resolves all relations that reference the given OSM object (node/way/area/relation)
     * as a member.
     */
    private List<Relation> getRelationsForOsmObject(String memberType, Long memberOsmId) {
        List<Relation> relations = new ArrayList<>();
        List<RelationEntity> relationEntities = this.relationRepository.findAllByMember(memberType, memberOsmId);

        for (RelationEntity relationEntity : relationEntities) {
            List<Relation> rels = this.getRelationsForOsmObject("r", relationEntity.getOsmId());
            relations.add(RelationEntityMapper.toRelation(relationEntity, rels));
        }

        return relations;
    }

    /**
     * Retrieves the geometry nodes belonging to the given area and converts them
     * into GeometryNode instances in the requested CRS.
     */
    private List<GeometryNode> getGeometryNodes(AreaEntity areaEntity, String coordinateReferenceSystem) {
        return this.areaNodeRepository.findById_AreaOsmIdOrderById_Seq(areaEntity.getOsmId())
                .stream().map(n  -> AreaNodeEntityMapper.toGeometryNode(n, coordinateReferenceSystem))
                .toList();
    }

    /**
     * Retrieves the geometry nodes belonging to the given way and converts them
     * into GeometryNode instances in the requested CRS.
     */
    private List<GeometryNode> getGeometryNodes(WayEntity wayEntity, String coordinateReferenceSystem) {
        return this.wayNodeRepository.findById_WayOsmIdOrderById_Seq(wayEntity.getOsmId())
                .stream().map(n  -> WayNodeEntityMapper.toGeometryNode(n, coordinateReferenceSystem))
                .toList();
    }

    /**
     * Get next identifier sequence.
     */
    @Override
    public Long getNextIdentifierSequence() {
        return this.sequenceRepository.getNextIdentifierSequence();
    }
}
