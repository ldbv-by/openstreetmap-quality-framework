package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.attribute_check.rules;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto.FeatureDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto.MemberDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto.RelationDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto.TaggedObjectsDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.attribute_check.service.AttributeCheckService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceErrorDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceRequestDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class DE_00001_A_a_002 extends DatabaseIntegrationTest {

    @Autowired
    private AttributeCheckService attributeCheckService;

    @Test
    void testEndedGreaterThenBegin() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("object_type", "AX_Wohnbauflaeche");
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:beginnt", "2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:endet", "2022-03-12T11:48:42Z");

        RelationDto modellart = new RelationDto(
                2L,
                "AA_modellart",
                Map.of("advStandardModell", "Basis-DLM"),
                List.of(new MemberDto("*", 1L, "")),
                new ArrayList<>()
        );

        FeatureDto feature = new FeatureDto(
                1L, "AX_Wohnbauflaeche", tags, List.of(modellart), null, null, new ArrayList<>()
        );

        TaggedObjectsDto taggedObjectsDto = new TaggedObjectsDto(
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(feature),
                new ArrayList<>()
        );

        QualityServiceRequestDto qualityServiceRequestDto = new QualityServiceRequestDto(
                "attribute-check",
                1L,
                null,
                taggedObjectsDto
        );

        // Act
        QualityServiceResultDto result = this.attributeCheckService.checkChangesetQuality(qualityServiceRequestDto);

        // Assert
        String errorTexts = result.errors() == null ? "" :
                result.errors().stream()
                        .map(QualityServiceErrorDto::errorText)
                        .collect(java.util.stream.Collectors.joining(" | "));

        assertThat(result).as("QualityServiceResult must not be null").isNotNull();
        assertThat(result.isValid()).withFailMessage("Expected the result to be valid, but it was invalid. Errors: %s", errorTexts).isTrue();
        assertThat(result.errors()).withFailMessage("Expected no errors, but found: %s", errorTexts).isEmpty();
    }

    @Test
    void testEndedLowerThenBegin() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("object_type", "AX_Wohnbauflaeche");
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:beginnt", "2022-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:endet", "2021-03-12T11:48:42Z");

        RelationDto modellart = new RelationDto(
                2L,
                "AA_modellart",
                Map.of("advStandardModell", "Basis-DLM"),
                List.of(new MemberDto("*", 1L, "")),
                new ArrayList<>()
        );

        FeatureDto feature = new FeatureDto(
                1L, "AX_Wohnbauflaeche", tags, List.of(modellart), null, null, new ArrayList<>()
        );

        TaggedObjectsDto taggedObjectsDto = new TaggedObjectsDto(
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(feature),
                new ArrayList<>()
        );

        QualityServiceRequestDto qualityServiceRequestDto = new QualityServiceRequestDto(
                "attribute-check",
                1L,
                null,
                taggedObjectsDto
        );

        // Act
        QualityServiceResultDto result = this.attributeCheckService.checkChangesetQuality(qualityServiceRequestDto);

        // Assert
        String errorTexts = result.errors() == null ? "" :
                result.errors().stream()
                        .map(QualityServiceErrorDto::errorText)
                        .collect(java.util.stream.Collectors.joining(" | "));

        assertThat(result).as("QualityServiceResult must not be null").isNotNull();
        assertThat(result.isValid()).withFailMessage("Expected the result to be invalid because 'lebenszeitintervall:endet' is earlier than 'lebenszeitintervall:beginnt', but it was valid.").isFalse();
        assertThat(result.errors()).withFailMessage("Expected at least one error, but found none.").isNotEmpty();

        assertThat(result.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .withFailMessage("Expected the ordering error message, but got: %s", errorTexts)
                .containsExactly("Das Tag lebenszeitintervall:endet muss zeitlich nach dem Tag lebenszeitintervall:beginnt liegen.");

        assertThat(result.errors())
                .withFailMessage("Expected exactly one error, but found %d: %s", result.errors().size(), errorTexts)
                .hasSize(1);
    }

    @Test
    void testEndedIsEqualThenBegin() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("object_type", "AX_Wohnbauflaeche");
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:beginnt", "2022-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:endet", "2022-03-12T11:48:42Z");

        RelationDto modellart = new RelationDto(
                2L,
                "AA_modellart",
                Map.of("advStandardModell", "Basis-DLM"),
                List.of(new MemberDto("*", 1L, "")),
                new ArrayList<>()
        );

        FeatureDto feature = new FeatureDto(
                1L, "AX_Wohnbauflaeche", tags, List.of(modellart), null, null, new ArrayList<>()
        );

        TaggedObjectsDto taggedObjectsDto = new TaggedObjectsDto(
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(feature),
                new ArrayList<>()
        );

        QualityServiceRequestDto qualityServiceRequestDto = new QualityServiceRequestDto(
                "attribute-check",
                1L,
                null,
                taggedObjectsDto
        );

        // Act
        QualityServiceResultDto result = this.attributeCheckService.checkChangesetQuality(qualityServiceRequestDto);

        // Assert
        String errorTexts = result.errors() == null ? "" :
                result.errors().stream()
                        .map(QualityServiceErrorDto::errorText)
                        .collect(java.util.stream.Collectors.joining(" | "));

        assertThat(result).as("QualityServiceResult must not be null").isNotNull();
        assertThat(result.isValid()).withFailMessage("Expected the result to be invalid because 'lebenszeitintervall:endet' is equal than 'lebenszeitintervall:beginnt', but it was valid.").isFalse();
        assertThat(result.errors()).withFailMessage("Expected at least one error, but found none.").isNotEmpty();

        assertThat(result.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .withFailMessage("Expected the ordering error message, but got: %s", errorTexts)
                .containsExactly("Das Tag lebenszeitintervall:endet muss zeitlich nach dem Tag lebenszeitintervall:beginnt liegen.");

        assertThat(result.errors())
                .withFailMessage("Expected exactly one error, but found %d: %s", result.errors().size(), errorTexts)
                .hasSize(1);
    }

    @Test
    void testEndedIsMissing() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("object_type", "AX_Wohnbauflaeche");
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:beginnt", "2021-03-12T11:48:42Z");

        RelationDto modellart = new RelationDto(
                2L,
                "AA_modellart",
                Map.of("advStandardModell", "Basis-DLM"),
                List.of(new MemberDto("*", 1L, "")),
                new ArrayList<>()
        );

        FeatureDto feature = new FeatureDto(
                1L, "AX_Wohnbauflaeche", tags, List.of(modellart), null, null, new ArrayList<>()
        );

        TaggedObjectsDto taggedObjectsDto = new TaggedObjectsDto(
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(feature),
                new ArrayList<>()
        );

        QualityServiceRequestDto qualityServiceRequestDto = new QualityServiceRequestDto(
                "attribute-check",
                1L,
                null,
                taggedObjectsDto
        );

        // Act
        QualityServiceResultDto result = this.attributeCheckService.checkChangesetQuality(qualityServiceRequestDto);

        // Assert
        String errorTexts = result.errors() == null ? "" :
                result.errors().stream()
                        .map(QualityServiceErrorDto::errorText)
                        .collect(java.util.stream.Collectors.joining(" | "));

        assertThat(result).as("QualityServiceResult must not be null").isNotNull();
        assertThat(result.isValid()).withFailMessage("Expected the result to be valid, but it was invalid. Errors: %s", errorTexts).isTrue();
        assertThat(result.errors()).withFailMessage("Expected no errors, but found: %s", errorTexts).isEmpty();
    }
}