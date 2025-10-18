package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.service;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.AreaEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.NodeEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.RelationEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.WayEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.mapper.AreaEntityMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.mapper.NodeEntityMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.mapper.RelationEntityMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.mapper.WayEntityMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository.AreaRepository;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository.NodeRepository;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository.RelationRepository;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository.WayRepository;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.api.OsmGeometriesService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.DataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.DataSet;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Feature;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.FeatureFilter;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Relation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OsmGeometriesServiceImpl implements OsmGeometriesService {

    private final NodeRepository nodeRepository;
    private final WayRepository wayRepository;
    private final AreaRepository areaRepository;
    private final RelationRepository relationRepository;

    /**
     * Returns the current tagged objects.
     */
    @Override
    public DataSetDto getDataSet(FeatureFilter featureFilter, String coordinateReferenceSystem) {
        DataSet resultDataSet = new DataSet();

        resultDataSet.getNodes().addAll(this.getNodesByFeatureFilter(featureFilter, coordinateReferenceSystem));
        resultDataSet.getWays().addAll(this.getWaysByFeatureFilter(featureFilter, coordinateReferenceSystem));
        resultDataSet.getAreas().addAll(this.getAreasByFeatureFilter(featureFilter, coordinateReferenceSystem));

        // Todo: Bounding box filter currently excludes relations — necessary?
        if (featureFilter.boundingBox() == null) {
            resultDataSet.getRelations().addAll(this.getRelationsByFeatureFilter(featureFilter));
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
    private List<Feature> getNodesByFeatureFilter(FeatureFilter featureFilter, String coordinateReferenceSystem) {
        List<Feature> nodes = new ArrayList<>();
        List<NodeEntity> nodeEntities = this.nodeRepository.fetchByFeatureFilter(featureFilter);

        if (nodeEntities != null) {
            for (NodeEntity nodeEntity : nodeEntities) {
                List<Relation> relations = this.getRelationsForOsmObject("n", nodeEntity.getOsmId());
                nodes.add(NodeEntityMapper.toFeature(nodeEntity, relations, coordinateReferenceSystem));
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
    private List<Feature> getWaysByFeatureFilter(FeatureFilter featureFilter, String coordinateReferenceSystem) {
        List<Feature> ways = new ArrayList<>();
        List<WayEntity> wayEntities = this.wayRepository.fetchByFeatureFilter(featureFilter);

        if (wayEntities != null) {
            for (WayEntity wayEntity : wayEntities) {
                List<Relation> relations = this.getRelationsForOsmObject("w", wayEntity.getOsmId());
                ways.add(WayEntityMapper.toFeature(wayEntity, relations, coordinateReferenceSystem));
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
                ways.add(WayEntityMapper.toFeature(wayEntity, relations, coordinateReferenceSystem));
            }
        }

        return ways;
    }

    /**
     * Returns the current areas by feature filter.
     */
    private List<Feature> getAreasByFeatureFilter(FeatureFilter featureFilter, String coordinateReferenceSystem) {
        List<Feature> areas = new ArrayList<>();
        List<AreaEntity> areaEntities = this.areaRepository.fetchByFeatureFilter(featureFilter);

        if (areaEntities != null) {
            for (AreaEntity areaEntity : areaEntities) {
                List<Relation> relations = this.getRelationsForOsmObject(areaEntity.getOsmGeometryType().toString(), areaEntity.getOsmId());
                areas.add(AreaEntityMapper.toFeature(areaEntity, relations, coordinateReferenceSystem));
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
                areas.add(AreaEntityMapper.toFeature(areaEntity, relations, coordinateReferenceSystem));
            }
        }

        return areas;
    }

    /**
     * Returns the current relations by feature filter.
     */
    private List<Relation> getRelationsByFeatureFilter(FeatureFilter featureFilter) {
        List<Relation> relations = new ArrayList<>();
        List<RelationEntity> relationEntities = this.relationRepository.fetchByFeatureFilter(featureFilter);

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

}
