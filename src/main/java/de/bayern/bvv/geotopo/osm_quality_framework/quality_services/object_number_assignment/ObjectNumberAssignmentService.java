package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.object_number_assignment;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.api.OsmGeometriesService;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.api.OsmSchemaService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.OsmPrimitive;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.ChangesetDataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.ObjectTypeDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceRequestDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.mapper.QualityServiceResultMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.model.QualityServiceResult;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.object_number_assignment.component.ObjectNumberGenerator;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.spi.QualityService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.util.ChangesetEditor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service("object-number-assignment")
@RequiredArgsConstructor
@Slf4j
public class ObjectNumberAssignmentService implements QualityService {

    private final OsmSchemaService osmSchemaService;
    private final OsmGeometriesService osmGeometriesService;
    private final ObjectNumberGenerator objectNumberGenerator;

    private static final String IDENTIFIER_TAG_KEY = "identifikator";
    private static final String IDENTIFIER_UUID_TAG_KEY = IDENTIFIER_TAG_KEY + ":UUID";
    private static final String IDENTIFIER_UUID_AND_TIME_TAG_KEY = IDENTIFIER_TAG_KEY + ":UUIDundZeit";
    private static final String OBJECT_START_TIME_TAG_KEY = "lebenszeitintervall:beginnt";
    private static final String OBJECT_END_TIME_TAG_KEY = "lebenszeitintervall:endet";
    private static final String OBJECT_TYPE_TAG_KEY = "object_type";

    private static final DateTimeFormatter IDENTIFIER_TIME_UUID_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    private static final DateTimeFormatter OBJECT_START_END_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * Executes the quality check for the given request.
     * Set object number automatically.
     */
    @Override
    public QualityServiceResultDto checkChangesetQuality(QualityServiceRequestDto qualityServiceRequestDto) {
        // ----- Initialize result of quality service.
        QualityServiceResult qualityServiceResult = new QualityServiceResult(
                qualityServiceRequestDto.qualityServiceId(), qualityServiceRequestDto.changesetId());

        Changeset modifiedChangeset = ChangesetMapper.toDomain(
                qualityServiceRequestDto.changesetId(), qualityServiceRequestDto.changesetDto());

        // ----- Determine current time.
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);

        // ----- Get tagged objects.
        ChangesetDataSet changesetDataSet = ChangesetDataSetMapper.toDomain(qualityServiceRequestDto.changesetDataSetDto());

        // ----- Set identifier on new objects.
        for (TaggedObject createdObject : changesetDataSet.getCreate().getAll()) {
            if (identifierIsNecessary(createdObject)) {
                setNewIdentifier(createdObject, modifiedChangeset, nowUtc);
            }
        }

        // ----- Set identifier on modified objects.
        for (TaggedObject modifyObject : changesetDataSet.getModify().getAll()) {
            if (identifierIsNecessary(modifyObject)) {
                TaggedObject modifyObjectBefore = this.osmGeometriesService.getTaggedObject(modifyObject);

                String identifierUUIDBefore = modifyObjectBefore.getTags().getOrDefault(IDENTIFIER_UUID_TAG_KEY, "");
                String identifierTimeUUIDBefore = modifyObjectBefore.getTags().getOrDefault(IDENTIFIER_UUID_AND_TIME_TAG_KEY, "");
                String objectStartTimeBefore = modifyObjectBefore.getTags().getOrDefault(OBJECT_START_TIME_TAG_KEY, "");
                String objectTypeBefore = modifyObjectBefore.getTags().getOrDefault(OBJECT_TYPE_TAG_KEY, "");

                // Identifier has changed manually -> set old identifier.
                if (!(modifyObject.getTags().getOrDefault(IDENTIFIER_UUID_TAG_KEY, "").equals(identifierUUIDBefore)) ||
                    !(modifyObject.getTags().getOrDefault(IDENTIFIER_UUID_AND_TIME_TAG_KEY, "").equals(identifierTimeUUIDBefore)) ||
                    !(modifyObject.getTags().getOrDefault(OBJECT_START_TIME_TAG_KEY, "").equals(objectStartTimeBefore))) {

                    setOldIdentifier(modifyObject, modifiedChangeset, identifierUUIDBefore, identifierTimeUUIDBefore, objectStartTimeBefore);
                }

                // Object Type has changed -> set new identifier.
                if (!(modifyObject.getTags().getOrDefault(OBJECT_TYPE_TAG_KEY, "").equals(objectTypeBefore))) {
                    setNewIdentifier(modifyObject, modifiedChangeset, nowUtc);
                }
            }
        }

        qualityServiceResult.setModifiedChangeset(modifiedChangeset);
        return QualityServiceResultMapper.toDto(qualityServiceResult);
    }

    /**
     * Check if identifier is necessary.
     */
    private boolean identifierIsNecessary(TaggedObject taggedObject) {
        ObjectTypeDto schemaInfo = this.osmSchemaService.getObjectTypeInfo(taggedObject.getObjectType());

        if (schemaInfo == null || schemaInfo.tags() == null) {
            return false;
        } else {
            return schemaInfo.tags().stream().anyMatch(tag -> IDENTIFIER_TAG_KEY.equals(tag.key()));
        }
    }

    /**
     * Set new identifier in changeset.
     */
    private void setNewIdentifier(TaggedObject taggedObject, Changeset changeset, ZonedDateTime nowUtc) {
        OsmPrimitive osmPrimitive = ChangesetEditor.getOsmPrimitive(taggedObject, changeset);

        String identifierUUID = objectNumberGenerator.getNextIdentifier();
        String identifierTimeUUID = identifierUUID + nowUtc.format(IDENTIFIER_TIME_UUID_FORMAT);
        String objectStartTime = nowUtc.format(OBJECT_START_END_TIME_FORMAT);

        ChangesetEditor.upsertTag(osmPrimitive, IDENTIFIER_UUID_TAG_KEY, identifierUUID);
        ChangesetEditor.upsertTag(osmPrimitive, IDENTIFIER_UUID_AND_TIME_TAG_KEY, identifierTimeUUID);
        ChangesetEditor.upsertTag(osmPrimitive, OBJECT_START_TIME_TAG_KEY, objectStartTime);
    }

    /**
     * Set old identifier in changeset.
     */
    private void setOldIdentifier(TaggedObject taggedObject, Changeset changeset,
                                  String identifierUUID, String identifierTimeUUID,
                                  String objectStartTime) {
        OsmPrimitive osmPrimitive = ChangesetEditor.getOsmPrimitive(taggedObject, changeset);
        ChangesetEditor.upsertTag(osmPrimitive, IDENTIFIER_UUID_TAG_KEY, identifierUUID);
        ChangesetEditor.upsertTag(osmPrimitive, IDENTIFIER_UUID_AND_TIME_TAG_KEY, identifierTimeUUID);
        ChangesetEditor.upsertTag(osmPrimitive, OBJECT_START_TIME_TAG_KEY, objectStartTime);
    }
}
