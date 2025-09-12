package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.overlay_check.service;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto.ChangesetQualityRequestDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto.ChangesetQualityResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.spi.QualityService;
import org.springframework.stereotype.Service;

@Service("overlay-check")
public class OverlayCheckService implements QualityService {

    /**
     * Executes the quality check for the given request.
     * Check overlays.
     */
    @Override
    public ChangesetQualityResultDto checkChangesetQuality(ChangesetQualityRequestDto request) {
        System.out.println("OverlayCheckService started");
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
        }
        System.out.println("OverlayCheckService finished");

        return new ChangesetQualityResultDto("overlay-check", true, null);
    }
}
