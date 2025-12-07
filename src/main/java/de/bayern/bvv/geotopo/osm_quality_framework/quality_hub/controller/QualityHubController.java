package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.controller;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.dto.QualityHubResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.service.QualityHubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * REST controller of the central Quality Hub component.
 * <p>
 * The Quality Hub acts as the decoupled validation layer within the OpenStreetMap-based data acquisition workflow.
 * Incoming changesets from any editing environment are received, persisted, and forwarded to the configured quality services.
 * Each changeset is validated against the relevant quality constraints (e.g. the AdV specification).
 */
@RestController
@RequestMapping("/osm-quality-framework/v1/quality-hub")
@RequiredArgsConstructor
@Slf4j
public class QualityHubController {

    private final QualityHubService qualityHubService;

    /**
     * Submits a changeset for automated quality validation.
     * <p>
     * The received changeset is persisted in the internal data store and is subsequently published to the registered
     * quality modules (e.g., rule-based checks, geometric validation).
     * The modules return consolidated validation results, including errors and potential automated corrections.
     */
    @PostMapping(
            value = "/check/changeset/{changesetId}",
            consumes = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<QualityHubResultDto> checkChangesetQuality(
            @PathVariable Long changesetId,
            @RequestBody ChangesetDto changesetDto,
            @RequestParam(required = false) Set<String> steps,
            @RequestParam(required = false) Set<String> rules) {

        QualityHubResultDto qualityHubResultDto =
                this.qualityHubService.checkChangesetQuality(changesetId, changesetDto, steps, rules);

        return ResponseEntity.ok(qualityHubResultDto);
    }

    /**
     * Finalizes a validated changeset after successful processing.
     * <p>
     * After the validation workflow has been completed — including any automated
     * geometry or attribute corrections — the changeset is marked as {@code finished}
     * and the finalized changeset are persisted in the OpenStreetMap geometry schema.
     */
    @PutMapping("/finish/changeset/{changesetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> finishChangeset(@PathVariable Long changesetId) {
        this.qualityHubService.finishChangeset(changesetId);
        return ResponseEntity.noContent().build();
    }
}
