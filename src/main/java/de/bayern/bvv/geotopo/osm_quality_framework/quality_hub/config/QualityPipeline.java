package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "quality.pipeline")
@Data
@Component
public class QualityPipeline {
    private List<Step> steps = List.of();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Step {
        private String id;
        private List<String> waitsFor = List.of();

        public enum State {
            RUNNING,
            FINISHED,
            FAILED
        }
    }

}

