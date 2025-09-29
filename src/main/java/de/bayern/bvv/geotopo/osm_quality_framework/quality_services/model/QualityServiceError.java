package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.model;

import lombok.Data;
import org.locationtech.jts.geom.Geometry;

/**
 * Representing a specific error of a quality service.
 */
@Data
public class QualityServiceError {
    private String errorText;
    private Geometry errorGeometry;

    public QualityServiceError(String errorText, Geometry errorGeometry) {
        this.errorText = errorText;
        this.errorGeometry = errorGeometry;
    }

    public QualityServiceError(String errorText) {
        this(errorText, null);
    }
}
