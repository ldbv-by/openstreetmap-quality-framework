package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.attribute_check.rules;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.feature.dto.FeatureDto;
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

class DE_00001_A_a_001 extends DatabaseIntegrationTest {

    @Autowired
    private AttributeCheckService attributeCheckService;

    @Test
    void testBeginAndEndInCorrectFormat() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:beginnt", "2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:endet", "2021-03-12T11:48:42Z");
        tags.put("modellart:advStandardModell", "Basis-DLM"); // Todo: modellart muss eine Relation sein
        tags.put("modellart:sonstigesModell", "DTK10A"); // Todo: modellart muss eine Relation sein

        FeatureDto feature = new FeatureDto(
                1L, "AX_Wohnbauflaeche", tags, new HashSet<>(), null, null, new ArrayList<>()
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
    void testInvalidBeginFormat() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:beginnt", "2021-03-12");
        tags.put("lebenszeitintervall:endet", "2021-03-12T11:48:42Z");
        tags.put("modellart:advStandardModell", "Basis-DLM"); // Todo: modellart muss eine Relation sein
        tags.put("modellart:sonstigesModell", "DTK10A"); // Todo: modellart muss eine Relation sein

        FeatureDto feature = new FeatureDto(
                1L, "AX_Wohnbauflaeche", tags, new HashSet<>(), null, null, new ArrayList<>()
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
        assertThat(result.isValid()).withFailMessage("Expected the result to be invalid because of an invalid tag format, but it was valid.").isFalse();
        assertThat(result.errors()).withFailMessage("Expected at least one error, but found none.").isNotEmpty();

        assertThat(result.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .withFailMessage("Expected an error about the invalid tag format for 'lebenszeitintervall:beginnt', but got: %s", errorTexts)
                .containsExactly("Die Zeitangabe ist nicht GeoInfoDok-konform.");

        assertThat(result.errors())
                .withFailMessage("Expected exactly one error, but found %d: %s", result.errors().size(), errorTexts)
                .hasSize(1);
    }

    @Test
    void testInvalidEndFormat() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:beginnt", "2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:endet", "2021-03-12");
        tags.put("modellart:advStandardModell", "Basis-DLM"); // Todo: modellart muss eine Relation sein
        tags.put("modellart:sonstigesModell", "DTK10A"); // Todo: modellart muss eine Relation sein

        FeatureDto feature = new FeatureDto(
                1L, "AX_Wohnbauflaeche", tags, new HashSet<>(), null, null, new ArrayList<>()
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
        assertThat(result.isValid()).withFailMessage("Expected the result to be invalid because of an invalid tag format, but it was valid.").isFalse();
        assertThat(result.errors()).withFailMessage("Expected at least one error, but found none.").isNotEmpty();

        assertThat(result.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .withFailMessage("Expected an error about the invalid tag format for 'lebenszeitintervall:endet', but got: %s", errorTexts)
                .containsExactly("Die Zeitangabe ist nicht GeoInfoDok-konform.");

        assertThat(result.errors())
                .withFailMessage("Expected exactly one error, but found %d: %s", result.errors().size(), errorTexts)
                .hasSize(1);
    }

    @Test
    void testMissingEnd() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:beginnt", "2021-03-12T11:48:42Z");
        tags.put("modellart:advStandardModell", "Basis-DLM"); // Todo: modellart muss eine Relation sein
        tags.put("modellart:sonstigesModell", "DTK10A"); // Todo: modellart muss eine Relation sein

        FeatureDto feature = new FeatureDto(
                1L, "AX_Wohnbauflaeche", tags, new HashSet<>(), null, null, new ArrayList<>()
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