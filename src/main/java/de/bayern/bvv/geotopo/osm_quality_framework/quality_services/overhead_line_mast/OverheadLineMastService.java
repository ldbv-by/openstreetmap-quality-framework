package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.overhead_line_mast;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.OsmPrimitive;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Tag;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.ChangesetDataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceRequestDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.mapper.QualityServiceResultMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.model.QualityServiceError;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.model.QualityServiceResult;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.spi.QualityService;
import de.bayern.bvv.geotopo.osm_quality_framework.geodata_view.api.GeodataViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("overhead-line-mast")
@RequiredArgsConstructor
@Slf4j
public class OverheadLineMastService implements QualityService {

    private final GeodataViewService geodataViewService;

    /**
     * Executes the quality check for the given request.
     * Set overhead line mast automatically.
     */
    @Override
    public QualityServiceResultDto checkChangesetQuality(QualityServiceRequestDto qualityServiceRequestDto) {
        // ----- Initialize result of quality service.
        QualityServiceResult qualityServiceResult = new QualityServiceResult(
                qualityServiceRequestDto.qualityServiceId(), qualityServiceRequestDto.changesetId());

        Changeset modifiedChangeset = ChangesetMapper.toDomain(
                qualityServiceRequestDto.changesetId(), qualityServiceRequestDto.changesetDto());

        // ----- Get tagged objects.
        ChangesetDataSet changesetDataSet = ChangesetDataSetMapper.toDomain(qualityServiceRequestDto.changesetDataSetDto());

        // ----- Check for each line if a mast is set.
        for (Feature overheadLine : changesetDataSet.getCreatedAndModified().stream()
                .filter(o -> "AX_Leitung".equals(o.getObjectType())
                        && o.getTags() != null
                        && "1110".equals(o.getTags().get("bauwerksfunktion")))
                .filter(Feature.class::isInstance)
                .map(Feature.class::cast)
                .collect(Collectors.toSet())
        ) {

            List<Feature> taggedWayNodes = this.geodataViewService.getWayNodesAsFeature(overheadLine);

            for (GeometryNode overheadLineNode : overheadLine.getGeometryNodes()) {

                Feature taggedWayNode = taggedWayNodes.stream()
                        .filter(wn -> wn.getOsmId().equals(overheadLineNode.getOsmId()))
                        .findFirst().orElse(null);

                if (taggedWayNode != null && taggedWayNode.getObjectType() != null) {

                    // Overhead line mast already set.
                    if ("AX_BauwerkOderAnlageFuerIndustrieUndGewerbe".equals(taggedWayNode.getObjectType()) &&
                        taggedWayNode.getTags() != null &&
                        "1251".equals(taggedWayNode.getTags().get("bauwerksfunktion"))) {
                        continue;
                    }

                    // Other object type already set.
                    qualityServiceResult.addError(
                            new QualityServiceError(
                                    "Der Knickpunkt einer Freileitung muss ein Freileitungsmast sein.",
                                    taggedWayNode.getGeometry())
                    );

                    break;
                }

                // Set overhead line mast tags.
                OsmPrimitive osmPrimitive;
                if (overheadLineNode.getOsmId() < 0) {
                    osmPrimitive = modifiedChangeset.getCreatePrimitives().stream()
                            .filter(osm -> osm.getId().equals(overheadLineNode.getOsmId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Way node %d in changeset not found".formatted(overheadLineNode.getOsmId())));
                } else {
                    osmPrimitive = modifiedChangeset.getModifyPrimitives().stream()
                            .filter(osm -> osm.getId().equals(overheadLineNode.getOsmId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Way node %d in changeset not found".formatted(overheadLineNode.getOsmId())));
                }

                upsertTag(osmPrimitive, "object_type", "AX_BauwerkOderAnlageFuerIndustrieUndGewerbe");
                upsertTag(osmPrimitive, "bauwerksfunktion", "1251");

                qualityServiceResult.setModifiedChangeset(modifiedChangeset);
            }

        }

        return QualityServiceResultMapper.toDto(qualityServiceResult);
    }

    private static void upsertTag(OsmPrimitive prim, String key, String value) {
        List<Tag> tags = prim.getTags();
        if (!(tags instanceof ArrayList)) {
            tags = new ArrayList<>(tags == null ? List.of() : tags);
            prim.setTags(tags);
        }
        tags.removeIf(t -> Objects.equals(t.getK(), key));
        tags.add(new Tag(key, value));
    }
}
