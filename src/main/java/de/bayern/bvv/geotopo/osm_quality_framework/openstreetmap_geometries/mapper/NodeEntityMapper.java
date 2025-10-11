package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.mapper;


import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.NodeEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Feature;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Relation;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.util.CoordinateTransformer;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;

/**
 * Mapping between {@link NodeEntity} and {@link Feature}.
 */
@UtilityClass
public class NodeEntityMapper {

    /**
     * Map node entity to feature.
     */
    public Feature toFeature(NodeEntity nodeEntity, List<Relation> relations, String coordinateReferenceSystem) {
        if (nodeEntity == null) return null;

        Feature feature = new Feature();
        feature.setOsmId(nodeEntity.getOsmId());
        feature.setObjectType(nodeEntity.getObjectType());
        feature.setTags(nodeEntity.getTags());
        feature.setRelations(relations);
        feature.setGeometry(nodeEntity.getGeom());
        feature.setGeometryTransformed(CoordinateTransformer.transform(nodeEntity.getGeom(), coordinateReferenceSystem));
        feature.setGeometryNodes(Collections.emptyList());

        return feature;
    }
}
