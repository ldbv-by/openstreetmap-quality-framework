package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.component;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.config.QualityPipeline;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.exception.QualityServiceException;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.model.QualityHubResult;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceRequestDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.spi.QualityService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Orchestrates execution of {@link QualityService} tasks  defined by a DAG.
 * Run all possible services asynchronously using virtual threads.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class Orchestrator {
    private final QualityPipeline qualityPipeline;
    private final Map<String, QualityService> qualityServices;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * Starts the pipeline for the given {@link Changeset} and blocks until:
     * - All services completed successfully, or
     * - Any service failed, or
     * - The overall timeout is reached.
     **/
    public QualityHubResult start(Changeset changeset) {
        QualityHubResult qualityHubResult = new QualityHubResult(changeset);

        final Map<String, QualityPipeline.Step.State> publishedPipelineSteps = new ConcurrentHashMap<>();
        final AtomicInteger cntRemainingSteps = new AtomicInteger(this.qualityPipeline.getSteps().size());
        final CompletableFuture<Void> allStepsDone = new CompletableFuture<>();

        // Execute pipeline
        this.publishRunnableSteps(changeset, publishedPipelineSteps, qualityHubResult, cntRemainingSteps, allStepsDone);

        // Wait for completion (maximum 5 minutes)
        try {
            allStepsDone.orTimeout(5, TimeUnit.MINUTES).join();
        } catch (CompletionException e) {
            Throwable cause = (e.getCause() != null) ? e.getCause() : e;

            if (cause instanceof QualityServiceException qualityServiceException) {
                throw qualityServiceException;
            }

            throw new QualityServiceException("Pipeline failed or timed out", cause);
        }

        return qualityHubResult;
    }

    /**
     * Enqueue all currently runnable services.
     */
    private void publishRunnableSteps(Changeset changeset,
                                      Map<String, QualityPipeline.Step.State> publishedPipelineSteps,
                                      QualityHubResult qualityHubResult,
                                      AtomicInteger cntRemainingSteps,
                                      CompletableFuture<Void> allStepsDone) {

        Set<QualityPipeline.Step> runnableSteps = this.getRunnableSteps(publishedPipelineSteps);

        for (QualityPipeline.Step step : runnableSteps) {

            // Set step to state running
            QualityPipeline.Step.State stepState = publishedPipelineSteps.putIfAbsent(
                    step.getId(), QualityPipeline.Step.State.RUNNING);

            if (stepState != null) {
                // Step is already running
                continue;
            }

            // Get bean for the step
            QualityService qualityServiceBean = this.qualityServices.get(step.getId());

            // Send changeset to quality service asynchronous
            CompletableFuture
                .supplyAsync(() -> {
                    QualityServiceRequestDto qualityServiceRequestDto =
                            new QualityServiceRequestDto(
                                    step.getId(),
                                    changeset.getId(),
                                    ChangesetMapper.toDto(changeset),
                                    null); // todo: tagged objects laden

                    return qualityServiceBean.checkChangesetQuality(qualityServiceRequestDto);
                }, executor)
                .orTimeout(5, TimeUnit.MINUTES)
                .whenComplete((qualityServiceResultDto, throwable) -> {
                    try {
                        if (throwable == null && qualityServiceResultDto != null) {

                            // Set step to state finished
                            publishedPipelineSteps.put(step.getId(), QualityPipeline.Step.State.FINISHED);

                            // Add result of quality service to response list
                            if (qualityServiceResultDto.modifiedChangesetDto() != null) {
                                qualityHubResult.setChangeset(ChangesetMapper.toDomain(changeset.getId(), qualityServiceResultDto.modifiedChangesetDto()));
                            }

                            if (qualityHubResult.isValid() && !qualityServiceResultDto.isValid()) {
                                qualityHubResult.setValid(false);
                            }

                            qualityHubResult.addQualityServiceResult(qualityServiceResultDto);

                            // publish next quality services with modified changeset
                            this.publishRunnableSteps(
                                    qualityHubResult.getChangeset(),
                                    publishedPipelineSteps,
                                    qualityHubResult,
                                    cntRemainingSteps,
                                    allStepsDone
                            );

                        } else {
                            // Quality service response failed
                            if (!allStepsDone.isDone()) {
                                QualityServiceException qualityServiceException = (throwable != null) ?
                                        new QualityServiceException("Quality Service '" + step.getId() + "' failed", throwable) :
                                        new QualityServiceException("Quality Service '" + step.getId() + "' has no response");

                                allStepsDone.completeExceptionally(qualityServiceException);
                            }
                        }
                    } catch (Exception e) {
                        log.error("Quality Service '{}' failed: {}", step.getId(), e.getMessage());
                    } finally {
                        if (cntRemainingSteps.decrementAndGet() == 0 && !allStepsDone.isDone()) {
                            allStepsDone.complete(null);
                        }
                    }
                });
        }
    }

    /**
     * Get all quality services that are ready for execution.
     * Executable quality services are all those that have not startet yet and are not waiting for any other service.
     */
    private Set<QualityPipeline.Step> getRunnableSteps(Map<String, QualityPipeline.Step.State> publishedPipelineSteps) {
        return this.qualityPipeline.getSteps().stream()
                .filter(step -> !publishedPipelineSteps.containsKey(step.getId()) &&
                        !this.isWaitingOnAnotherQualityService(publishedPipelineSteps, step))
                .collect(Collectors.toSet());
    }

    /**
     * Check if quality service is waiting on other service that has either not startet yes or has not finished.
     */
    private boolean isWaitingOnAnotherQualityService(Map<String, QualityPipeline.Step.State> publishedPipelineSteps,
                                                     QualityPipeline.Step step) {
        if (step.getWaitsFor().isEmpty()) {
            return false;
        }

        return step.getWaitsFor().stream()
                .anyMatch(waitForQualityServiceId -> this.qualityPipeline.getSteps().stream()
                        .filter(waitForStep -> waitForStep.getId().equals(waitForQualityServiceId))
                        .anyMatch(waitForStep -> !publishedPipelineSteps.containsKey(waitForStep.getId()) ||
                                publishedPipelineSteps.get(waitForStep.getId()) != QualityPipeline.Step.State.FINISHED));
    }

    /**
     * Gracefully shut down executor on app stop.
     **/
    @PreDestroy
    void shutdown() {
        this.executor.close();
    }
}
