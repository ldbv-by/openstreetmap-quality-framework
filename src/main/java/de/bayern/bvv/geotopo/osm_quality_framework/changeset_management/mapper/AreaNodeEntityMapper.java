package de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.mapper;


import de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.entity.AreaNodeEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.GeometryNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.util.CoordinateTransformer;
import lombok.experimental.UtilityClass;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

/**
 * Mapping between {@link AreaNodeEntity} and {@link GeometryNode}.
 */
@UtilityClass
public class AreaNodeEntityMapper {

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * Map node entity to feature.
     */
    public GeometryNode toGeometryNode(AreaNodeEntity areaNodeEntity, String coordinateReferenceSystem) {
        if (areaNodeEntity == null) return null;

        GeometryNode geometryNode = new GeometryNode();
        geometryNode.setOsmId(areaNodeEntity.getId().getNodeOsmId());
        geometryNode.setMemberOsmId(areaNodeEntity.getMemberOsmId());
        geometryNode.setSequence(areaNodeEntity.getId().getSeq());

        Point geometry = geometryFactory.createPoint(new Coordinate(areaNodeEntity.getLon(), areaNodeEntity.getLat()));
        geometryNode.setGeometry(geometry);
        geometryNode.setGeometryTransformed((Point) CoordinateTransformer.transform(geometry, coordinateReferenceSystem));

        return geometryNode;
    }
}
