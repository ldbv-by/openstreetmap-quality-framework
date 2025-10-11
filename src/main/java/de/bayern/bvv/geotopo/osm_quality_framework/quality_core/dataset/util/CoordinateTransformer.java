package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.util;

import lombok.experimental.UtilityClass;
import org.locationtech.jts.geom.*;
import org.locationtech.proj4j.*;

@UtilityClass
public class CoordinateTransformer {
    private static final double WGS84_ROUND_FACTOR = 10000000.0;
    private static final double UTM32_ROUND_FACTOR = 1000.0;

    private static final CRSFactory crsFactory = new CRSFactory();
    private static final CoordinateReferenceSystem WGS84 = crsFactory.createFromName("EPSG:4326");
    private static final CoordinateReferenceSystem UTM32 = crsFactory.createFromName("EPSG:25832");

    private static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
    private static final CoordinateTransform transformToUTM = ctFactory.createTransform(WGS84, UTM32);
    private static final CoordinateTransform transformToWGS = ctFactory.createTransform(UTM32, WGS84);

    /**
     * Transform geometry.
     */
    public static Geometry transform(Geometry geometry, String coordinateReferenceSystem) {
        if (coordinateReferenceSystem != null) {
            if (coordinateReferenceSystem.equalsIgnoreCase("EPSG:4326")) {
                return toWGS84(geometry);
            } else if (coordinateReferenceSystem.equalsIgnoreCase("EPSG:25832")) {
                return toUTM32(geometry);
            } else {
                throw new IllegalArgumentException(coordinateReferenceSystem + " is not a valid coordinate reference system");
            }
        }

        return null;
    }

    /**
     * Transform geometry from EPSG:4326 (WGS84) to EPSG:25832 (UTM32)
     */
    public static Geometry toUTM32(Geometry geometry) {
        return transformGeometry(geometry, transformToUTM);
    }

    /**
     * Transform geometry from EPSG:25832 (UTM32) to EPSG:4326 (WGS84)
     */
    public static Geometry toWGS84(Geometry geometry) {
        return transformGeometry(geometry, transformToWGS);
    }

    /**
     * Transform Geometry.
     */
    private static Geometry transformGeometry(Geometry geometry, CoordinateTransform transform) {
        GeometryFactory geometryFactory = new GeometryFactory();

        if (geometry instanceof Point) {
            return geometryFactory.createPoint(transformCoordinate(geometry.getCoordinate(), transform));

        } else if (geometry instanceof LineString) {
            return geometryFactory.createLineString(transformCoordinates(geometry.getCoordinates(), transform));

        } else if (geometry instanceof Polygon polygon) {
            LinearRing shell = geometryFactory.createLinearRing(
                    transformCoordinates(polygon.getExteriorRing().getCoordinates(), transform)
            );

            LinearRing[] holes = new LinearRing[polygon.getNumInteriorRing()];
            for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
                holes[i] = geometryFactory.createLinearRing(
                        transformCoordinates(polygon.getInteriorRingN(i).getCoordinates(), transform)
                );
            }

            return geometryFactory.createPolygon(shell, holes);

        } else if (geometry instanceof MultiPoint) {
            return geometryFactory.createMultiPointFromCoords(transformCoordinates(geometry.getCoordinates(), transform));

        } else if (geometry instanceof MultiLineString) {
            LineString[] lineStrings = new LineString[geometry.getNumGeometries()];
            for (int i = 0; i < geometry.getNumGeometries(); i++) {
                lineStrings[i] = geometryFactory.createLineString(
                        transformCoordinates(geometry.getGeometryN(i).getCoordinates(), transform)
                );
            }
            return geometryFactory.createMultiLineString(lineStrings);

        } else if (geometry instanceof MultiPolygon) {
            Polygon[] polygons = new Polygon[geometry.getNumGeometries()];
            for (int i = 0; i < geometry.getNumGeometries(); i++) {
                polygons[i] = (Polygon) transformGeometry(geometry.getGeometryN(i), transform);
            }
            return geometryFactory.createMultiPolygon(polygons);
        }

        throw new IllegalArgumentException("Unsupported geometry type: " + geometry.getGeometryType());
    }

    /**
     * Transform Coordinates.
     */
    private static Coordinate[] transformCoordinates(Coordinate[] coords, CoordinateTransform transform) {
        Coordinate[] transformed = new Coordinate[coords.length];
        for (int i = 0; i < coords.length; i++) {
            transformed[i] = transformCoordinate(coords[i], transform);
        }
        return transformed;
    }

    /**
     * Transform Coordinate.
     */
    private static Coordinate transformCoordinate(Coordinate coord, CoordinateTransform transform) {
        ProjCoordinate src = new ProjCoordinate(coord.x, coord.y);
        ProjCoordinate dest = new ProjCoordinate();
        transform.transform(src, dest);
        double roundFactor = transform.getTargetCRS().getName().equals("EPSG:25832") ? UTM32_ROUND_FACTOR : WGS84_ROUND_FACTOR;
        return roundCoordinate(dest, roundFactor);
    }

    private static Coordinate roundCoordinate(ProjCoordinate dest, double roundFactor) {
        dest.x = Math.round(dest.x * roundFactor) / roundFactor;
        dest.y = Math.round(dest.y * roundFactor) / roundFactor;
        return new Coordinate(dest.x, dest.y);
    }
}
