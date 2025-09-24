package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.component;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.spi.QualityService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.config.QualityPipeline;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.exception.QualityServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QualityPipelineValidatorTest {

    /**
     * Helper to build a pipeline step.
     **/
    private static QualityPipeline.Step step(String id, String... waitsFor) {
        return new QualityPipeline.Step(id, (waitsFor == null) ? List.of() : List.of(waitsFor));
    }

    /**
     * Verifies a valid diamond pipeline (A & B → C) */
    @Test
    void testValidPipeline() {
        // Arrange
        QualityPipeline pipeline = mock(QualityPipeline.class);
        when(pipeline.getSteps()).thenReturn(List.of(
                step("A"),
                step("B"),
                step("C", "A", "B")
        ));
        Map<String, QualityService> beans = Map.of(
                "A", mock(QualityService.class),
                "B", mock(QualityService.class),
                "C", mock(QualityService.class)
        );
        QualityPipelineValidator validator = new QualityPipelineValidator(pipeline, beans);

        // Act / Assert
        assertDoesNotThrow(validator::validate);
    }

    /**
     * Ensures that missing/blank or duplicate step ids are detected and cause a QualityServiceException.
     **/
    @Test
    void testInvalidIds() {
        // Arrange
        QualityPipeline pipeline = mock(QualityPipeline.class);
        when(pipeline.getSteps()).thenReturn(List.of(
                step("A"),
                step("", "A"), // blank id
                step("A") // duplicate id
        ));
        Map<String, QualityService> beans = Map.of("A", mock(QualityService.class));
        QualityPipelineValidator validator = new QualityPipelineValidator(pipeline, beans);

        // Act / Assert
        assertThrows(QualityServiceException.class, validator::validate);
    }

    /**
     * Verifies that waitsFor references to unknown step ids are reported as a validation failure.
     **/
    @Test
    void testUnknownWaitFor() {
        // Arrange
        QualityPipeline pipeline = mock(QualityPipeline.class);
        when(pipeline.getSteps()).thenReturn(List.of(
                step("A"),
                step("B", "Z") // Z does not exist
        ));
        Map<String, QualityService> beans = Map.of(
                "A", mock(QualityService.class),
                "B", mock(QualityService.class)
        );
        QualityPipelineValidator validator = new QualityPipelineValidator(pipeline, beans);

        // Act / Assert
        assertThrows(QualityServiceException.class, validator::validate);
    }

    /**
     * Ensures validation fails when a step id has no corresponding QualityService bean in the map.
     **/
    @Test
    void testMissingBean() {
        // Arrange
        QualityPipeline pipeline = mock(QualityPipeline.class);
        when(pipeline.getSteps()).thenReturn(List.of(
                step("X")
        ));
        Map<String, QualityService> beans = Map.of(); // no bean for X
        QualityPipelineValidator validator = new QualityPipelineValidator(pipeline, beans);

        // Act / Assert
        QualityServiceException ex = assertThrows(QualityServiceException.class, validator::validate);
        assertTrue(ex.getMessage().contains("no QualityService bean(s) found"));
    }

    /** Detects a simple cycle (A<>B) and throws a QualityServiceException. */
    @Test
    void testCycleDetection() {
        // Arrange
        QualityPipeline pipeline = mock(QualityPipeline.class);
        when(pipeline.getSteps()).thenReturn(List.of(
                step("A", "B"),
                step("B", "A")
        ));
        Map<String, QualityService> beans = Map.of(
                "A", mock(QualityService.class),
                "B", mock(QualityService.class)
        );
        QualityPipelineValidator validator = new QualityPipelineValidator(pipeline, beans);

        // Act / Assert
        assertThrows(QualityServiceException.class, validator::validate);
    }

    /**
     * Confirms that an empty pipeline (no steps) is considered valid and does not throw.
     **/
    @Test
    void testEmptyPipeline() {
        // Arrange
        QualityPipeline pipeline = mock(QualityPipeline.class);
        when(pipeline.getSteps()).thenReturn(List.of());

        Map<String, QualityService> beans = Map.of();
        QualityPipelineValidator validator = new QualityPipelineValidator(pipeline, beans);

        // Act / Assert
        assertDoesNotThrow(validator::validate);
    }
}