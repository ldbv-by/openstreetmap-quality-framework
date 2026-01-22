package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.regional_attribute_mapping;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.api.OsmSchemaService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.OsmPrimitive;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.ChangesetDataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.ChangesetDataSet;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.TaggedObject;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.mapper.ObjectTypeMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.ObjectType;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceRequestDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.mapper.QualityServiceResultMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.model.QualityServiceResult;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.spi.QualityService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.util.ChangesetEditor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service("regional-attribute-mapping")
@RequiredArgsConstructor
@Slf4j
public class RegionalAttributeMappingService implements QualityService {

    private final OsmSchemaService osmSchemaService;

    private static final String BY_DLM_PREFIX = "by:dlm:";
    private static final Pattern FIVE_DIGITS = Pattern.compile("^\\d{5}$");

    /**
     * Executes the quality check for the given request.
     * Sets the AdV tags using regional attributes (truncates 5-digit numbers to 4 digits).
     */
    @Override
    public QualityServiceResultDto checkChangesetQuality(QualityServiceRequestDto qualityServiceRequestDto) {
        Changeset modifiedChangeset = ChangesetMapper.toDomain(qualityServiceRequestDto.changesetId(), qualityServiceRequestDto.changesetDto());

        // ----- Initialize result of quality service.
        QualityServiceResult qualityServiceResult = new QualityServiceResult(
                qualityServiceRequestDto.qualityServiceId(), qualityServiceRequestDto.changesetId());

        // ----- Get tagged objects.
        ChangesetDataSet changesetDataSet = ChangesetDataSetMapper.toDomain(qualityServiceRequestDto.changesetDataSetDto());

        // ----- Check for each created and modified object
        for (TaggedObject changedObject : changesetDataSet.getCreatedAndModified()) {

            for (Map.Entry<String, String> tag : changedObject.getTags().entrySet()) {

                if (!tag.getKey().startsWith(BY_DLM_PREFIX)) continue;
                if (!FIVE_DIGITS.matcher(tag.getValue()).matches()) continue;

                ObjectType objectType = Optional.ofNullable(this.osmSchemaService.getObjectTypeInfo(changedObject.getObjectType()))
                        .map(ObjectTypeMapper::toDomain)
                        .orElse(null);

                if (objectType != null) {
                    String advKey = tag.getKey().substring(BY_DLM_PREFIX.length());
                    String advValue = tag.getValue().substring(0, 4);

                    if (objectType.getTags().stream().anyMatch(t -> t.getKey().equals(advKey))) {
                        OsmPrimitive osmPrimitive = ChangesetEditor.getOsmPrimitive(changedObject, modifiedChangeset);
                        ChangesetEditor.upsertTag(osmPrimitive, advKey, advValue);
                        qualityServiceResult.setModifiedChangeset(modifiedChangeset);
                    }
                }
            }
        }

        return QualityServiceResultMapper.toDto(qualityServiceResult);
    }
}