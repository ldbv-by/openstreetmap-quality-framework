package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.mapper;


import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.AreaEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Feature;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.GeometryNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Relation;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.util.CoordinateTransformer;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Mapping between {@link AreaEntity} and {@link Feature}.
 */
@UtilityClass
public class AreaEntityMapper {

    /**
     * Map node entity to feature.
     */
    public Feature toFeature(AreaEntity areaEntity, List<GeometryNode> geometryNodes, List<Relation> relations, String coordinateReferenceSystem) {
        if (areaEntity == null) return null;

        Feature feature = new Feature();
        feature.setOsmId(areaEntity.getOsmId());
        feature.setObjectType(areaEntity.getObjectType());
        feature.setTags(areaEntity.getTags());
        feature.setRelations(relations);
        feature.setGeometry(areaEntity.getGeom());
        feature.setGeometryTransformed(CoordinateTransformer.transform(areaEntity.getGeom(), coordinateReferenceSystem));
        feature.setGeometryNodes(geometryNodes);

        return feature;
    }
}
