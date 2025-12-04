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
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.ChangesetState;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.DataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OsmGeometriesServiceImpl implements OsmGeometriesService {

    private final NodeRepository nodeRepository;
    private final WayRepository wayRepository;
    private final AreaRepository areaRepository;
    private final RelationRepository relationRepository;

    private final AreaNodeRepository areaNodeRepository;
    private final WayNodeRepository wayNodeRepository;

    private final OsmApiClient osmApiClient;
    private final Osm2PgSqlClient osm2PgSqlClient;

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
     * Returns the current tagged objects.
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
     * Returns a data set of all relation members.
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
     * Returns the current nodes by feature filter.
     */
    private List<Feature> getNodesByFeatureFilter(OsmIds osmIds, Criteria criteria, String coordinateReferenceSystem, List<Relation> relations) {
        List<Feature> nodes = new ArrayList<>();
        List<NodeEntity> nodeEntities = this.nodeRepository.fetchByFeatureFilter(osmIds, criteria);

        if (nodeEntities != null) {
            for (NodeEntity nodeEntity : nodeEntities) {
                List<Relation> nodeRelations = this.getRelationsForOsmObject("n", nodeEntity.getOsmId());
                nodes.add(NodeEntityMapper.toFeature(nodeEntity, nodeRelations, coordinateReferenceSystem));
                relations.addAll(nodeRelations);
            }
        }

        return nodes;
    }

    /**
     * Returns the current relation member nodes for a relation id.
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
     * Returns the current ways by feature filter.
     */
    private List<Feature> getWaysByFeatureFilter(OsmIds osmIds, Criteria criteria, String coordinateReferenceSystem, List<Relation> relations) {
        List<Feature> ways = new ArrayList<>();
        List<WayEntity> wayEntities = this.wayRepository.fetchByFeatureFilter(osmIds, criteria);

        if (wayEntities != null) {
            for (WayEntity wayEntity : wayEntities) {
                List<Relation> wayRelations = this.getRelationsForOsmObject("w", wayEntity.getOsmId());
                List<GeometryNode> geometryNodes = this.getGeometryNodes(wayEntity, coordinateReferenceSystem);
                ways.add(WayEntityMapper.toFeature(wayEntity, geometryNodes, wayRelations, coordinateReferenceSystem));
                relations.addAll(wayRelations);
            }
        }

        return ways;
    }

    /**
     * Returns the current relation member ways for a relation id.
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
     * Returns the current areas by feature filter.
     */
    private List<Feature> getAreasByFeatureFilter(OsmIds osmIds, Criteria criteria, String coordinateReferenceSystem, List<Relation> relations) {
        List<Feature> areas = new ArrayList<>();
        List<AreaEntity> areaEntities = this.areaRepository.fetchByFeatureFilter(osmIds, criteria);

        if (areaEntities != null) {
            for (AreaEntity areaEntity : areaEntities) {
                List<Relation> areaRelations = this.getRelationsForOsmObject(areaEntity.getOsmGeometryType().toString(), areaEntity.getOsmId());
                List<GeometryNode> geometryNodes = this.getGeometryNodes(areaEntity, coordinateReferenceSystem);
                areas.add(AreaEntityMapper.toFeature(areaEntity, geometryNodes, areaRelations, coordinateReferenceSystem));
                relations.addAll(areaRelations);
            }
        }

        return areas;
    }

    /**
     * Returns the current relation member areas for a relation id.
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
     * Returns the current relations by feature filter.
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
     * Returns the current relation member relations for a relation id.
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
     * Returns the current relations for an osm object.
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
     * Get Geometry Nodes for Area Entity.
     */
    private List<GeometryNode> getGeometryNodes(AreaEntity areaEntity, String coordinateReferenceSystem) {
        return this.areaNodeRepository.findById_AreaOsmIdOrderById_Seq(areaEntity.getOsmId())
                .stream().map(n  -> AreaNodeEntityMapper.toGeometryNode(n, coordinateReferenceSystem))
                .toList();
    }

    /**
     * Get Geometry Nodes for Way Entity.
     */
    private List<GeometryNode> getGeometryNodes(WayEntity wayEntity, String coordinateReferenceSystem) {
        return this.wayNodeRepository.findById_WayOsmIdOrderById_Seq(wayEntity.getOsmId())
                .stream().map(n  -> WayNodeEntityMapper.toGeometryNode(n, coordinateReferenceSystem))
                .toList();
    }

}
