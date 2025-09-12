package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.component;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.spi.QualityService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.config.QualityPipeline;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.exception.QualityServiceException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Validates the {@link QualityPipeline} configuration at application startup.
 */
@Component
@RequiredArgsConstructor
public class QualityPipelineValidator {
    private final QualityPipeline qualityPipeline;
    private final Map<String, QualityService> qualityServices;

    /**
     * Runs all pipeline validations at application startup.
     */
    @PostConstruct
    public void validate() {
        this.validateIds();
        this.validateWaitFors();
        this.validateBeans();
        this.validateCycles();
    }

    /**
     * Validates step ids.
     */
    private void validateIds() {
        Set<String> seenIds = new HashSet<>();
        List<String> emptyIds = new ArrayList<>();
        List<String> duplicateIds = new ArrayList<>();

        for (QualityPipeline.Step step : this.qualityPipeline.getSteps()) {
            String id = (step != null) ? step.getId() : null;
            if (id == null || id.isBlank()) {
                emptyIds.add(String.valueOf(id));
                continue;
            }

            if (!seenIds.add(id)) {
                duplicateIds.add(id);
            }
        }

        if (!emptyIds.isEmpty()) {
            throw new QualityServiceException("Pipeline validation failed: step id(s) missing/blank: " + emptyIds);
        }

        if (!duplicateIds.isEmpty()) {
            throw new QualityServiceException("Pipeline validation failed: duplicate step id(s): " + duplicateIds);
        }
    }

    /**
     * Validates dependency references (waitsFor)
     */
    private void validateWaitFors() {
        Map<String, QualityPipeline.Step> byId = this.qualityPipeline.getSteps().stream()
                .collect(Collectors.toMap(QualityPipeline.Step::getId, s -> s));

        List<String> missingIds = new ArrayList<>();

        for (QualityPipeline.Step step : this.qualityPipeline.getSteps()) {
            for (String waitFor : step.getWaitsFor()) {
                if (!byId.containsKey(waitFor)) {
                    missingIds.add(step.getId() + " -> " + waitFor);
                }
            }
        }

        if (!missingIds.isEmpty()) {
            throw new QualityServiceException("Pipeline validation failed: unknown dependency references: " + missingIds);
        }
    }

    /**
     * Validates bean availability.
     */
    private void validateBeans() {
        List<String> missingBeans = this.qualityPipeline.getSteps().stream()
                .map(QualityPipeline.Step::getId)
                .filter(id -> !qualityServices.containsKey(id))
                .toList();

        if (!missingBeans.isEmpty()) {
            throw new QualityServiceException("Pipeline validation failed: no QualityService bean(s) found for: " + missingBeans);
        }
    }

    /**
     * Detects cycles in the dependency graph using Kahn's algorithm.
     */
    private void validateCycles() {
        Map<String, Integer> indegree = new HashMap<>();
        Map<String, List<String>> succ = new HashMap<>();

        for (QualityPipeline.Step step : this.qualityPipeline.getSteps()) {
            indegree.put(step.getId(), 0);
            succ.put(step.getId(), new ArrayList<>());
        }

        for (QualityPipeline.Step step : this.qualityPipeline.getSteps()) {
            for (String waitFor : step.getWaitsFor()) {
                succ.get(waitFor).add(step.getId());
                indegree.merge(step.getId(), 1, Integer::sum);
            }
        }

        Deque<String> q = new ArrayDeque<>(indegree.entrySet().stream()
                .filter(e -> e.getValue() == 0)
                .map(Map.Entry::getKey)
                .toList());

        int visited = 0;
        while (!q.isEmpty()) {
            String id = q.removeFirst();
            visited++;
            for (String nxt : succ.get(id)) {
                int left = indegree.merge(nxt, -1, Integer::sum);
                if (left == 0) q.addLast(nxt);
            }
        }

        if (visited != qualityPipeline.getSteps().size()) {
            List<String> cycleNodes = indegree.entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .map(Map.Entry::getKey)
                    .toList();

            throw new QualityServiceException("Pipeline validation failed: cycle detected. Involved step ids: " + cycleNodes);
        }
    }
}
