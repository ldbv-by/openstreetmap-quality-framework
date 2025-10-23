package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.mapper;


import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.WayNodeEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.GeometryNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.util.CoordinateTransformer;
import lombok.experimental.UtilityClass;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

/**
 * Mapping between {@link WayNodeEntity} and {@link GeometryNode}.
 */
@UtilityClass
public class WayNodeEntityMapper {

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * Map node entity to feature.
     */
    public GeometryNode toGeometryNode(WayNodeEntity wayNodeEntity, String coordinateReferenceSystem) {
        if (wayNodeEntity == null) return null;

        GeometryNode geometryNode = new GeometryNode();
        geometryNode.setOsmId(wayNodeEntity.getId().getNodeOsmId());
        geometryNode.setSequence(wayNodeEntity.getId().getSeq());

        Point geometry = geometryFactory.createPoint(new Coordinate(wayNodeEntity.getLon(), wayNodeEntity.getLat()));
        geometryNode.setGeometry(geometry);
        geometryNode.setGeometryTransformed((Point) CoordinateTransformer.transform(geometry, coordinateReferenceSystem));

        return geometryNode;
    }
}
