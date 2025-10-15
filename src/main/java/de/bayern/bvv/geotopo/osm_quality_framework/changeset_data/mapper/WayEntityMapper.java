package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.mapper;


import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.WayEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Feature;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Relation;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.util.CoordinateTransformer;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;

/**
 * Mapping between {@link WayEntity} and {@link Feature}.
 */
@UtilityClass
public class WayEntityMapper {

    /**
     * Map node entity to feature.
     */
    public Feature toFeature(WayEntity wayEntity, List<Relation> relations, String coordinateReferenceSystem) {
        if (wayEntity == null) return null;

        Feature feature = new Feature();
        feature.setOsmId(wayEntity.getOsmId());
        feature.setObjectType(wayEntity.getObjectType());
        feature.setTags(wayEntity.getTags());
        feature.setRelations(relations);
        feature.setGeometry(wayEntity.getGeom());
        feature.setGeometryTransformed(CoordinateTransformer.transform(wayEntity.getGeom(), coordinateReferenceSystem));
        feature.setGeometryNodes(Collections.emptyList()); // Todo

        return feature;
    }
}
