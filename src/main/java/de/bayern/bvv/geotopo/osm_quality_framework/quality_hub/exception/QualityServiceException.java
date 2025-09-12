package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.exception;

public class QualityServiceException extends RuntimeException {

    public QualityServiceException(String errorMessage) {
        super(errorMessage);
    }

    public QualityServiceException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
