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

class DE_02300_A_a_001 extends DatabaseIntegrationTest {

    @Autowired
    private AttributeCheckService attributeCheckService;

    @Test
    void testValidSignaturnummer() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:beginnt", "2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:endet", "2022-03-12T11:48:42Z");
        tags.put("modellart:advStandardModell", "Basis-DLM"); // Todo: modellart muss eine Relation sein
        tags.put("modellart:sonstigesModell", "DTK10A"); // Todo: modellart muss eine Relation sein
        tags.put("position:punkt", "1");
        tags.put("position:punkthaufen", "1");
        tags.put("signaturnummer", "BY1234");

        FeatureDto feature = new FeatureDto(
                1L, "AP_PPO", tags, new HashSet<>(), null, null, new ArrayList<>()
        );

        TaggedObjectsDto taggedObjectsDto = new TaggedObjectsDto(
                List.of(feature),
                new ArrayList<>(),
                new ArrayList<>(),
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
    void testInvalidSignaturnummer() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:beginnt", "2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:endet", "2022-03-12T11:48:42Z");
        tags.put("modellart:advStandardModell", "Basis-DLM"); // Todo: modellart muss eine Relation sein
        tags.put("modellart:sonstigesModell", "DTK10A"); // Todo: modellart muss eine Relation sein
        tags.put("position:punkt", "1");
        tags.put("position:punkthaufen", "1");
        tags.put("signaturnummer", "unkown");

        FeatureDto feature = new FeatureDto(
                1L, "AP_PPO", tags, new HashSet<>(), null, null, new ArrayList<>()
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
        assertThat(result.isValid()).withFailMessage("Expected the result to be invalid because 'signaturnummer' has wrong format, but it was valid.").isFalse();
        assertThat(result.errors()).withFailMessage("Expected at least one error, but found none.").isNotEmpty();

        assertThat(result.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .withFailMessage("Expected the invalid format error message, but got: %s", errorTexts)
                .containsExactly("Das Tag signaturnummer dem regulären Ausdruck ^([0-9]{4,5})$|^((BKG|BW|BU|BY|ST|SL|SH|NI|BE|BB|NW|RP|HE|MV|SN|HH|HB|TH)[0-9]{4})$ entsprechen.");

        assertThat(result.errors())
                .withFailMessage("Expected exactly one error, but found %d: %s", result.errors().size(), errorTexts)
                .hasSize(1);
    }
}