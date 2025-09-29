package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.attribute_check.service;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceRequestDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.mapper.QualityServiceResultMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.model.QualityServiceResult;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.spi.QualityService;
import org.springframework.stereotype.Service;

@Service("attribute-check")
public class AttributeCheckService implements QualityService {

    /**
     * Executes the quality check for the given request.
     * Check attribute consistency.
     */
    @Override
    public QualityServiceResultDto checkChangesetQuality(QualityServiceRequestDto qualityServiceRequestDto) {
        QualityServiceResult qualityServiceResult =
                new QualityServiceResult(qualityServiceRequestDto.qualityServiceId(), qualityServiceRequestDto.changesetId());



        System.out.println("AttributeCheckService started");

        try {
            Thread.sleep(15_000);
        } catch (InterruptedException e) {
        }

        System.out.println("AttributeCheckService finished");


        return QualityServiceResultMapper.toDto(qualityServiceResult);
    }

}
