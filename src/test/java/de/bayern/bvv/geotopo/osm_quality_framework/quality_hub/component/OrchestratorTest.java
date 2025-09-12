package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.component;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto.ChangesetQualityRequestDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto.ChangesetQualityResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.spi.QualityService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.model.ChangesetQualityResult;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.config.QualityPipeline;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.exception.QualityServiceException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class OrchestratorTest {

    /**
     * Helper to build a pipeline step
     **/
    private static QualityPipeline.Step step(String id, String... waitsFor) {
        return new QualityPipeline.Step(id, (waitsFor == null) ? List.of() : List.of(waitsFor));
    }

    /**
     * Builds an Orchestrator with a mocked pipeline and a given services map
     **/
    private Orchestrator orchestratorWith(QualityPipeline pipeline, Map<String, QualityService> services) {
        return new Orchestrator(pipeline, services);
    }

    /**
     * Verifies a linear A -> B -> C pipeline executes all steps and yields three results.
     **/
    @Test
    void testLinearPipeline() {
        // Arrange: steps A -> B -> C
        QualityPipeline pipeline = mock(QualityPipeline.class);
        when(pipeline.getSteps()).thenReturn(List.of(
                step("A"),
                step("B", "A"),
                step("C", "B")
        ));

        // QualityService beans
        QualityService svcA = mock(QualityService.class);
        QualityService svcB = mock(QualityService.class);
        QualityService svcC = mock(QualityService.class);
        Map<String, QualityService> services = new HashMap<>();
        services.put("A", svcA);
        services.put("B", svcB);
        services.put("C", svcC);

        // Each service returns a non-null DTO
        when(svcA.checkChangesetQuality(any(ChangesetQualityRequestDto.class)))
                .thenReturn(mock(ChangesetQualityResultDto.class));
        when(svcB.checkChangesetQuality(any(ChangesetQualityRequestDto.class)))
                .thenReturn(mock(ChangesetQualityResultDto.class));
        when(svcC.checkChangesetQuality(any(ChangesetQualityRequestDto.class)))
                .thenReturn(mock(ChangesetQualityResultDto.class));

        Orchestrator orchestrator = orchestratorWith(pipeline, services);
        Changeset changeset = mock(Changeset.class);

        // Act
        List<ChangesetQualityResult> results = orchestrator.start(changeset);

        // Assert
        assertEquals(3, results.size(), "All three steps should produce a result");
        verify(svcA, times(1)).checkChangesetQuality(any());
        verify(svcB, times(1)).checkChangesetQuality(any());
        verify(svcC, times(1)).checkChangesetQuality(any());
    }

    /**
     * Ensures C runs only after A and B complete in a diamond pipeline and three results are produced.
     **/
    @Test
    void testDiamondPipeline() {
        // Arrange
        QualityPipeline pipeline = mock(QualityPipeline.class);
        when(pipeline.getSteps()).thenReturn(List.of(
                step("A"),
                step("B"),
                step("C", "A", "B")
        ));

        QualityService svcA = mock(QualityService.class);
        QualityService svcB = mock(QualityService.class);
        QualityService svcC = mock(QualityService.class);
        Map<String, QualityService> services = Map.of(
                "A", svcA,
                "B", svcB,
                "C", svcC
        );

        CountDownLatch latchAB = new CountDownLatch(2);

        when(svcA.checkChangesetQuality(any())).thenAnswer(_ -> {
            latchAB.countDown();
            return mock(ChangesetQualityResultDto.class);
        });
        when(svcB.checkChangesetQuality(any())).thenAnswer(_ -> {
            latchAB.countDown();
            return mock(ChangesetQualityResultDto.class);
        });
        when(svcC.checkChangesetQuality(any())).thenAnswer(_ -> {
            assertTrue(latchAB.await(2, TimeUnit.SECONDS), "C must start after A and B completed");
            return mock(ChangesetQualityResultDto.class);
        });

        Orchestrator orchestrator = orchestratorWith(pipeline, new HashMap<>(services));

        // Act
        var results = orchestrator.start(mock(Changeset.class));

        // Assert
        assertEquals(3, results.size(), "All three steps should produce a result");
        verify(svcA, times(1)).checkChangesetQuality(any());
        verify(svcB, times(1)).checkChangesetQuality(any());
        verify(svcC, times(1)).checkChangesetQuality(any());
    }

    /**
     * Confirms a failing service triggers fail-fast with QualityServiceException and prevents dependent execution.
     **/
    @Test
    void testQualityServiceFailure() {
        // Arrange
        QualityPipeline pipeline = mock(QualityPipeline.class);
        when(pipeline.getSteps()).thenReturn(List.of(
                step("A"),
                step("B"),
                step("C", "B")
        ));

        QualityService svcA = mock(QualityService.class);
        QualityService svcB = mock(QualityService.class);
        QualityService svcC = mock(QualityService.class);
        Map<String, QualityService> services = new HashMap<>();
        services.put("A", svcA);
        services.put("B", svcB);
        services.put("C", svcC);

        when(svcA.checkChangesetQuality(any())).thenReturn(mock(ChangesetQualityResultDto.class));
        when(svcB.checkChangesetQuality(any())).thenThrow(new RuntimeException("boom"));

        Orchestrator orchestrator = orchestratorWith(pipeline, services);

        // Act / Assert
        QualityServiceException ex = assertThrows(QualityServiceException.class,
                () -> orchestrator.start(mock(Changeset.class)));

        assertTrue(ex.getMessage().contains("failed")
                        || ex.getMessage().contains("Pipeline failed"),
                "Exception should indicate failure");

        verify(svcA, atMost(1)).checkChangesetQuality(any());
        verify(svcB, times(1)).checkChangesetQuality(any());
        verify(svcC, times(0)).checkChangesetQuality(any());
    }
}