package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.attribute_check.schema;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.*;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.attribute_check.service.AttributeCheckService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceErrorDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceRequestDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class SchemaIntegrationTest extends DatabaseIntegrationTest {

    @Autowired
    private AttributeCheckService attributeCheckService;

    @Test
    void testCompleteTaggedObject() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("object_type", "AX_Wohnbauflaeche");
        tags.put("zeigtAufExternes:art", "https://www.adv-online.de/AdV-Produkte/Geotopographie/Digitale-Landschaftsmodelle/Basis-DLM/");
        tags.put("zeigtAufExternes:fachdatenobjekt:name", "Test");
        tags.put("zeigtAufExternes:fachdatenobjekt:uri", "Test-URI");
        tags.put("quellobjektID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:beginnt", "2021-03-12T11:48:42Z");
        tags.put("anlass", "000000");
        tags.put("datumDerLetztenUeberpruefung", "12.03.2021");
        tags.put("istWeitereNutzung", "1000");
        tags.put("ergebnisDerUeberpruefung", "1000");
        tags.put("artDerBebauung", "1000");
        tags.put("name", "Test");
        tags.put("zustand", "2100");
        tags.put("zweitname", "Test1,Test2");
        tags.put("funktion", "1200");

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

        DataSetDto dataSetDto = new DataSetDto(
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(feature),
                new ArrayList<>()
        );

        ChangesetDataSetDto changesetDataSetDto = new ChangesetDataSetDto(dataSetDto, null, null);

        QualityServiceRequestDto qualityServiceRequestDto = new QualityServiceRequestDto(
                "attribute-check",
                1L,
                null,
                changesetDataSetDto
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
    void testNecessaryTaggedObject() {
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

        DataSetDto dataSetDto = new DataSetDto(
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(feature),
                new ArrayList<>()
        );

        ChangesetDataSetDto changesetDataSetDto = new ChangesetDataSetDto(dataSetDto, null, null);

        QualityServiceRequestDto qualityServiceRequestDto = new QualityServiceRequestDto(
                "attribute-check",
                1L,
                null,
                changesetDataSetDto
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
    void testUnknownTagKeyTaggedObject() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("object_type", "AX_Wohnbauflaeche");
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:beginnt", "2021-03-12T11:48:42Z");
        tags.put("unknown", "12345");

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

        DataSetDto dataSetDto = new DataSetDto(
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(feature),
                new ArrayList<>()
        );

        ChangesetDataSetDto changesetDataSetDto = new ChangesetDataSetDto(dataSetDto, null, null);

        QualityServiceRequestDto qualityServiceRequestDto = new QualityServiceRequestDto(
                "attribute-check",
                1L,
                null,
                changesetDataSetDto
        );

        // Act
        QualityServiceResultDto result = this.attributeCheckService.checkChangesetQuality(qualityServiceRequestDto);

        // Assert
        String errorTexts = result.errors() == null ? "" :
                result.errors().stream()
                        .map(QualityServiceErrorDto::errorText)
                        .collect(java.util.stream.Collectors.joining(" | "));

        assertThat(result).as("QualityServiceResult must not be null").isNotNull();
        assertThat(result.isValid()).withFailMessage("Expected the result to be invalid because of unknown tags, but it was valid.").isFalse();
        assertThat(result.errors()).withFailMessage("Expected at least one error, but found none.").isNotEmpty();

        assertThat(result.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .withFailMessage("Expected an error about the unknown tag, but got: %s", errorTexts)
                .containsExactly("Tag 'unknown' ist nicht im Schema definiert.");

        assertThat(result.errors())
                .withFailMessage("Expected exactly one error, but found %d: %s", result.errors().size(), errorTexts)
                .hasSize(1);
    }

    @Test
    void testUnknownTagValueTaggedObject() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("object_type", "AX_Wohnbauflaeche");
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall:beginnt", "2021-03-12T11:48:42Z");
        tags.put("artDerBebauung", "12345");

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

        DataSetDto dataSetDto = new DataSetDto(
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(feature),
                new ArrayList<>()
        );

        ChangesetDataSetDto changesetDataSetDto = new ChangesetDataSetDto(dataSetDto, null, null);

        QualityServiceRequestDto qualityServiceRequestDto = new QualityServiceRequestDto(
                "attribute-check",
                1L,
                null,
                changesetDataSetDto
        );

        // Act
        QualityServiceResultDto result = this.attributeCheckService.checkChangesetQuality(qualityServiceRequestDto);

        // Assert
        String errorTexts = result.errors() == null ? "" :
                result.errors().stream()
                        .map(QualityServiceErrorDto::errorText)
                        .collect(java.util.stream.Collectors.joining(" | "));

        assertThat(result).as("QualityServiceResult must not be null").isNotNull();
        assertThat(result.isValid()).withFailMessage("Expected the result to be invalid because of an invalid tag value, but it was valid.").isFalse();
        assertThat(result.errors()).withFailMessage("Expected at least one error, but found none.").isNotEmpty();

        assertThat(result.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .withFailMessage("Expected an error about the invalid value for 'artDerBebauung', but got: %s", errorTexts)
                .containsExactly("Das Tag 'artDerBebauung' hat einen ungültigen Wert.");

        assertThat(result.errors())
                .withFailMessage("Expected exactly one error, but found %d: %s", result.errors().size(), errorTexts)
                .hasSize(1);
    }

    @Test
    void testMissingComplexTagTaggedObject() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("object_type", "AX_Wohnbauflaeche");
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");

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

        DataSetDto dataSetDto = new DataSetDto(
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(feature),
                new ArrayList<>()
        );

        ChangesetDataSetDto changesetDataSetDto = new ChangesetDataSetDto(dataSetDto, null, null);

        QualityServiceRequestDto qualityServiceRequestDto = new QualityServiceRequestDto(
                "attribute-check",
                1L,
                null,
                changesetDataSetDto
        );

        // Act
        QualityServiceResultDto result = this.attributeCheckService.checkChangesetQuality(qualityServiceRequestDto);

        // Assert
        String errorTexts = result.errors() == null ? "" :
                result.errors().stream()
                        .map(QualityServiceErrorDto::errorText)
                        .collect(java.util.stream.Collectors.joining(" | "));

        assertThat(result).as("QualityServiceResult must not be null").isNotNull();
        assertThat(result.isValid()).withFailMessage("Expected the result to be invalid due to complex tag issues, but it was valid.").isFalse();
        assertThat(result.errors()).withFailMessage("Expected at least one error, but found none.").isNotEmpty();

        assertThat(result.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .withFailMessage("Expected an error about missing 'lebenszeitintervall', but got: %s", errorTexts)
                .anySatisfy(msg -> assertThat(msg)
                        .isEqualTo("Das komplexe Tag 'lebenszeitintervall' ist nicht vorhanden."));

        assertThat(result.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .withFailMessage("Expected an error about missing 'lebenszeitintervall:beginnt', but got: %s", errorTexts)
                .anySatisfy(msg -> assertThat(msg)
                        .isEqualTo("Das Tag 'lebenszeitintervall:beginnt' ist nicht vorhanden."));

        assertThat(result.errors())
                .withFailMessage("Expected exactly two errors, but found %d: %s", result.errors().size(), errorTexts)
                .hasSize(2);
    }

    @Test
    void testMissingComplexSubTagTaggedObject() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("object_type", "AX_Wohnbauflaeche");
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");
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

        DataSetDto dataSetDto = new DataSetDto(
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(feature),
                new ArrayList<>()
        );

        ChangesetDataSetDto changesetDataSetDto = new ChangesetDataSetDto(dataSetDto, null, null);

        QualityServiceRequestDto qualityServiceRequestDto = new QualityServiceRequestDto(
                "attribute-check",
                1L,
                null,
                changesetDataSetDto
        );

        // Act
        QualityServiceResultDto result = this.attributeCheckService.checkChangesetQuality(qualityServiceRequestDto);

        // Assert
        String errorTexts = result.errors() == null ? "" :
                result.errors().stream()
                        .map(QualityServiceErrorDto::errorText)
                        .collect(java.util.stream.Collectors.joining(" | "));

        assertThat(result).as("QualityServiceResult must not be null").isNotNull();
        assertThat(result.isValid()).withFailMessage("Expected the result to be invalid due to complex tag issues, but it was valid.").isFalse();
        assertThat(result.errors()).withFailMessage("Expected at least one error, but found none.").isNotEmpty();

        assertThat(result.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .withFailMessage("Expected an error about missing 'lebenszeitintervall:beginnt', but got: %s", errorTexts)
                .anySatisfy(msg -> assertThat(msg)
                        .isEqualTo("Das Tag 'lebenszeitintervall:beginnt' ist nicht vorhanden."));

        assertThat(result.errors())
                .withFailMessage("Expected exactly one error, but found %d: %s", result.errors().size(), errorTexts)
                .hasSize(1);
    }

    @Test
    void testComplexWithDirectValueTaggedObject() {
        // Arrange
        Map<String, String> tags = new HashMap<>();
        tags.put("object_type", "AX_Wohnbauflaeche");
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");
        tags.put("lebenszeitintervall", "2021-03-12T11:48:42Z");

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

        DataSetDto dataSetDto = new DataSetDto(
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(feature),
                new ArrayList<>()
        );

        ChangesetDataSetDto changesetDataSetDto = new ChangesetDataSetDto(dataSetDto, null, null);

        QualityServiceRequestDto qualityServiceRequestDto = new QualityServiceRequestDto(
                "attribute-check",
                1L,
                null,
                changesetDataSetDto
        );

        // Act
        QualityServiceResultDto result = this.attributeCheckService.checkChangesetQuality(qualityServiceRequestDto);

        // Assert
        String errorTexts = result.errors() == null ? "" :
                result.errors().stream()
                        .map(QualityServiceErrorDto::errorText)
                        .collect(java.util.stream.Collectors.joining(" | "));

        assertThat(result).as("QualityServiceResult must not be null").isNotNull();
        assertThat(result.isValid()).withFailMessage("Expected the result to be invalid due to complex tag issues, but it was valid.").isFalse();
        assertThat(result.errors()).withFailMessage("Expected at least one error, but found none.").isNotEmpty();

        assertThat(result.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .withFailMessage("Expected an error about 'lebenszeitintervall' having a direct value, but got: %s", errorTexts)
                .anySatisfy(msg -> assertThat(msg)
                        .isEqualTo("Das komplexe Tag 'lebenszeitintervall' darf keine direkten Werte enthalten."));

        assertThat(result.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .withFailMessage("Expected an error about missing 'lebenszeitintervall:beginnt', but got: %s", errorTexts)
                .anySatisfy(msg -> assertThat(msg)
                        .isEqualTo("Das Tag 'lebenszeitintervall:beginnt' ist nicht vorhanden."));

        assertThat(result.errors())
                .withFailMessage("Expected exactly two errors, but found %d: %s", result.errors().size(), errorTexts)
                .hasSize(2);
    }

    @Test
    void testUnknownObjectTypeTaggedObject() {
        // Arrange:
        Map<String, String> tags = new HashMap<>();
        tags.put("object_type", "AX_Wohnbauflaeche");
        tags.put("identifikator:UUID", "DEBYBDLMCI0001qd");
        tags.put("identifikator:UUIDundZeit", "DEBYBDLMCI0001qd_2021-03-12T11:48:42Z");

        // WICHTIG: unbekannter ObjectType
        FeatureDto feature = new FeatureDto(
                1L, "AX_Unbekannt", tags, new ArrayList<>(), null, null, new ArrayList<>()
        );

        DataSetDto dataSetDto = new DataSetDto(
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(feature),
                new ArrayList<>()
        );

        ChangesetDataSetDto changesetDataSetDto = new ChangesetDataSetDto(dataSetDto, null, null);

        QualityServiceRequestDto qualityServiceRequestDto = new QualityServiceRequestDto(
                "attribute-check",
                1L,
                null,
                changesetDataSetDto
        );

        // Act
        QualityServiceResultDto result = this.attributeCheckService.checkChangesetQuality(qualityServiceRequestDto);

        // Assert
        String errorTexts = result.errors() == null ? "" :
                result.errors().stream()
                        .map(QualityServiceErrorDto::errorText)
                        .collect(java.util.stream.Collectors.joining(" | "));

        assertThat(result).as("QualityServiceResult must not be null").isNotNull();
        assertThat(result.isValid()).withFailMessage("Expected the result to be invalid due to missing schema, but it was valid.").isFalse();
        assertThat(result.errors()).withFailMessage("Expected at least one error, but found none.").isNotEmpty();

        assertThat(result.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .withFailMessage("Expected an error about missing schema for object type, but got: %s", errorTexts)
                .containsExactly("Keine Schemaeinträge für 'AX_Unbekannt' gefunden.");

        assertThat(result.errors())
                .withFailMessage("Expected exactly one error, but found %d: %s", result.errors().size(), errorTexts)
                .hasSize(1);
    }
}
