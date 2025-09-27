package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration class for the quality pipeline.
 */
@ConfigurationProperties(prefix = "quality.pipeline")
@Data
@Component
public class QualityPipeline {

    /**
     * List of pipeline steps configured for execution.
     */
    private List<Step> steps = List.of();

    /**
     * Represents a single step in the quality pipeline,
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Step {

        /**
         * Unique identifier of the step.
         */
        private String id;

        /**
         * List of step identifiers that must finish before this step runs.
         */
        private List<String> waitsFor = List.of();

        /**
         * Possible runtime states of a pipeline step.
         */
        public enum State {
            RUNNING,
            FINISHED,
            FAILED
        }
    }

}

