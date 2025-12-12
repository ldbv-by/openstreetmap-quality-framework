package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.attribute_check;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.config.JtsJackson3Module;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.dto.QualityHubResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceErrorDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AdV-Beschreibung:
 * Die Attributart 'Internationale Bedeutung' mit der Werteart 2001 'Europastraße' kann nur
 * in Verbindung mit der Attributart 'Bezeichnung' mit "E" vorkommen.
 *
 * Die Attributart 'Internationale Bedeutung' mit der Werteart 2001 'Europastraße' muss mindestens 2 Bezeichnungen führen.
 *
 * Falls IBD vorhanden ist, beginnt BEZ bei WDM 1301 mit "E" oder mit "A" und bei WDM 1303 mit "E" oder mit "B".
 * Jede weitere Bezeichnung muss mit "E" beginnen. Ansonsten beginnt BEZ bei WDM 1301 mit "A", bei 1303 mit "B", bei 1305 mit "L" oder "S".
 * Bei WDM 1307, 9997 und 9999 ist BEZ nicht erlaubt. Jedes BEZ muss den regulären Ausdruck '^[A-Za-z0-9äÄöÖüÜ]+$' erfüllen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_42002_F_b_001_003_F_d_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("attribute-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.42002.F.b.001_003_F.d.001_1",
                                                       "DE.42002.F.b.001_003_F.d.001_2",
                                                       "DE.42002.F.b.001_003_F.d.001_3",
                                                       "DE.42002.F.b.001_003_F.d.001_4",
                                                       "DE.42002.F.b.001_003_F.d.001_5",
                                                       "DE.42002.F.b.001_003_F.d.001_6",
                                                       "DE.42002.F.b.001_003_F.d.001_7"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createBundesautobahnMitBezeichnung() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25364' changeset='-1' lat='49.87494356474' lon='12.32280142285' />
                  <node id='-25363' changeset='-1' lat='49.87777772641' lon='12.32285980169' />
                  <way id='-1308' changeset='-1'>
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <tag k='object_type' v='AX_Strassenachse' />
                    <tag k='breiteDerFahrbahn' v='9' />
                  </way>
                  <relation id='-77' changeset='-1'>
                    <member type='way' ref='-1308' role='' />
                    <tag k='bezeichnung' v='Europastrasse1;Autobahn' />
                    <tag k='internationaleBedeutung' v='2001' />
                    <tag k='object_type' v='AX_Strasse' />
                    <tag k='widmung' v='1301' />
                  </relation>
                </create>
                </osmChange>
                """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML)
                                .param("steps", String.join(",", stepsToValidate))
                                .param("rules", String.join(",", rulesToValidate)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        QualityHubResultDto qualityHubResultDto = this.objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), QualityHubResultDto.class);

        // Assert
        assertThat(qualityHubResultDto).as("Quality-Hub result must not be null").isNotNull();
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result to be valid, but it was invalid.").isTrue();
    }

    @Test
    void createBundesautobahnMitFalscherBezeichnung() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25364' changeset='-1' lat='49.87494356474' lon='12.32280142285' />
                  <node id='-25363' changeset='-1' lat='49.87777772641' lon='12.32285980169' />
                  <way id='-1308' changeset='-1'>
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <tag k='object_type' v='AX_Strassenachse' />
                    <tag k='breiteDerFahrbahn' v='9' />
                  </way>
                  <relation id='-77' changeset='-1'>
                    <member type='way' ref='-1308' role='' />
                    <tag k='bezeichnung' v='Europastrasse1;Test' />
                    <tag k='internationaleBedeutung' v='2001' />
                    <tag k='object_type' v='AX_Strasse' />
                    <tag k='widmung' v='1301' />
                  </relation>
                </create>
                </osmChange>
                """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML)
                                .param("steps", String.join(",", stepsToValidate))
                                .param("rules", String.join(",", rulesToValidate)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        QualityHubResultDto qualityHubResultDto = this.objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), QualityHubResultDto.class);

        // Assert
        assertThat(qualityHubResultDto).as("Quality-Hub result must not be null").isNotNull();
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result is not valid, but it was valid.").isFalse();

        QualityServiceResultDto attributeCheck = qualityHubResultDto.qualityServiceResults().stream()
                .filter(s -> "attribute-check".equals(s.qualityServiceId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("QualityService 'attribute-check' not found"));

        assertThat(attributeCheck.isValid()).withFailMessage("Expected the result is not valid, but it was valid.").isFalse();

        assertThat(attributeCheck.errors())
                .as("Errors of 'attribute-check' must not be empty")
                .isNotEmpty();

        assertThat(attributeCheck.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .as("Error text of 'attribut-check'")
                .contains("Das Tag 'bezeichnung' muss mit 'E' oder 'A' beginnen und mindestens zwei Bezeichnungen vorkommen, wenn die 'internationaleBedeutung' 2001 gesetzt ist.");
    }

    @Test
    void createBundesautobahnMitNurEinerBezeichnung() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25364' changeset='-1' lat='49.87494356474' lon='12.32280142285' />
                  <node id='-25363' changeset='-1' lat='49.87777772641' lon='12.32285980169' />
                  <way id='-1308' changeset='-1'>
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <tag k='object_type' v='AX_Strassenachse' />
                    <tag k='breiteDerFahrbahn' v='9' />
                  </way>
                  <relation id='-77' changeset='-1'>
                    <member type='way' ref='-1308' role='' />
                    <tag k='internationaleBedeutung' v='2001' />
                    <tag k='bezeichnung' v='Europastrasse' />
                    <tag k='object_type' v='AX_Strasse' />
                    <tag k='widmung' v='1301' />
                  </relation>
                </create>
                </osmChange>
                """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML)
                                .param("steps", String.join(",", stepsToValidate))
                                .param("rules", String.join(",", rulesToValidate)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        QualityHubResultDto qualityHubResultDto = this.objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), QualityHubResultDto.class);

        // Assert
        assertThat(qualityHubResultDto).as("Quality-Hub result must not be null").isNotNull();
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result is not valid, but it was valid.").isFalse();

        QualityServiceResultDto attributeCheck = qualityHubResultDto.qualityServiceResults().stream()
                .filter(s -> "attribute-check".equals(s.qualityServiceId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("QualityService 'attribute-check' not found"));

        assertThat(attributeCheck.isValid()).withFailMessage("Expected the result is not valid, but it was valid.").isFalse();

        assertThat(attributeCheck.errors())
                .as("Errors of 'attribute-check' must not be empty")
                .isNotEmpty();

        assertThat(attributeCheck.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .as("Error text of 'attribut-check'")
                .contains("Das Tag 'bezeichnung' muss mit 'E' oder 'A' beginnen und mindestens zwei Bezeichnungen vorkommen, wenn die 'internationaleBedeutung' 2001 gesetzt ist.");
    }

    @Test
    void createBundesstrasseMitBezeichnung() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25364' changeset='-1' lat='49.87494356474' lon='12.32280142285' />
                  <node id='-25363' changeset='-1' lat='49.87777772641' lon='12.32285980169' />
                  <way id='-1308' changeset='-1'>
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <tag k='object_type' v='AX_Strassenachse' />
                    <tag k='breiteDerFahrbahn' v='9' />
                  </way>
                  <relation id='-77' changeset='-1'>
                    <member type='way' ref='-1308' role='' />
                    <tag k='bezeichnung' v='Europastrasse1;Bundesstrasse' />
                    <tag k='internationaleBedeutung' v='2001' />
                    <tag k='object_type' v='AX_Strasse' />
                    <tag k='widmung' v='1303' />
                  </relation>
                </create>
                </osmChange>
                """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML)
                                .param("steps", String.join(",", stepsToValidate))
                                .param("rules", String.join(",", rulesToValidate)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        QualityHubResultDto qualityHubResultDto = this.objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), QualityHubResultDto.class);

        // Assert
        assertThat(qualityHubResultDto).as("Quality-Hub result must not be null").isNotNull();
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result to be valid, but it was invalid.").isTrue();
    }

    @Test
    void createBundesstrasseMitFalscherBezeichnung() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25364' changeset='-1' lat='49.87494356474' lon='12.32280142285' />
                  <node id='-25363' changeset='-1' lat='49.87777772641' lon='12.32285980169' />
                  <way id='-1308' changeset='-1'>
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <tag k='object_type' v='AX_Strassenachse' />
                    <tag k='breiteDerFahrbahn' v='9' />
                  </way>
                  <relation id='-77' changeset='-1'>
                    <member type='way' ref='-1308' role='' />
                    <tag k='bezeichnung' v='Europastrasse1;Autobahn' />
                    <tag k='internationaleBedeutung' v='2001' />
                    <tag k='object_type' v='AX_Strasse' />
                    <tag k='widmung' v='1303' />
                  </relation>
                </create>
                </osmChange>
                """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML)
                                .param("steps", String.join(",", stepsToValidate))
                                .param("rules", String.join(",", rulesToValidate)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        QualityHubResultDto qualityHubResultDto = this.objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), QualityHubResultDto.class);

        // Assert
        assertThat(qualityHubResultDto).as("Quality-Hub result must not be null").isNotNull();
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result is not valid, but it was valid.").isFalse();

        QualityServiceResultDto attributeCheck = qualityHubResultDto.qualityServiceResults().stream()
                .filter(s -> "attribute-check".equals(s.qualityServiceId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("QualityService 'attribute-check' not found"));

        assertThat(attributeCheck.isValid()).withFailMessage("Expected the result is not valid, but it was valid.").isFalse();

        assertThat(attributeCheck.errors())
                .as("Errors of 'attribute-check' must not be empty")
                .isNotEmpty();

        assertThat(attributeCheck.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .as("Error text of 'attribut-check'")
                .contains("Das Tag 'bezeichnung' muss mit 'E' oder 'B' beginnen und mindestens zwei Bezeichnungen vorkommen, wenn die 'internationaleBedeutung' 2001 gesetzt ist.");
    }

    @Test
    void createLandstrasseMitBezeichnung() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25364' changeset='-1' lat='49.87494356474' lon='12.32280142285' />
                  <node id='-25363' changeset='-1' lat='49.87777772641' lon='12.32285980169' />
                  <way id='-1308' changeset='-1'>
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <tag k='object_type' v='AX_Strassenachse' />
                    <tag k='breiteDerFahrbahn' v='9' />
                  </way>
                  <relation id='-77' changeset='-1'>
                    <member type='way' ref='-1308' role='' />
                    <tag k='bezeichnung' v='Landstrasse' />
                    <tag k='object_type' v='AX_Strasse' />
                    <tag k='widmung' v='1305' />
                  </relation>
                </create>
                </osmChange>
                """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML)
                                .param("steps", String.join(",", stepsToValidate))
                                .param("rules", String.join(",", rulesToValidate)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        QualityHubResultDto qualityHubResultDto = this.objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), QualityHubResultDto.class);

        // Assert
        assertThat(qualityHubResultDto).as("Quality-Hub result must not be null").isNotNull();
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result to be valid, but it was invalid.").isTrue();
    }

    @Test
    void createLandstrasseMitFalscherBezeichnung() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25364' changeset='-1' lat='49.87494356474' lon='12.32280142285' />
                  <node id='-25363' changeset='-1' lat='49.87777772641' lon='12.32285980169' />
                  <way id='-1308' changeset='-1'>
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <tag k='object_type' v='AX_Strassenachse' />
                    <tag k='breiteDerFahrbahn' v='9' />
                  </way>
                  <relation id='-77' changeset='-1'>
                    <member type='way' ref='-1308' role='' />
                    <tag k='bezeichnung' v='Autobahn' />
                    <tag k='object_type' v='AX_Strasse' />
                    <tag k='widmung' v='1305' />
                  </relation>
                </create>
                </osmChange>
                """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML)
                                .param("steps", String.join(",", stepsToValidate))
                                .param("rules", String.join(",", rulesToValidate)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        QualityHubResultDto qualityHubResultDto = this.objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), QualityHubResultDto.class);

        // Assert
        assertThat(qualityHubResultDto).as("Quality-Hub result must not be null").isNotNull();
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result is not valid, but it was valid.").isFalse();

        QualityServiceResultDto attributeCheck = qualityHubResultDto.qualityServiceResults().stream()
                .filter(s -> "attribute-check".equals(s.qualityServiceId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("QualityService 'attribute-check' not found"));

        assertThat(attributeCheck.isValid()).withFailMessage("Expected the result is not valid, but it was valid.").isFalse();

        assertThat(attributeCheck.errors())
                .as("Errors of 'attribute-check' must not be empty")
                .isNotEmpty();

        assertThat(attributeCheck.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .as("Error text of 'attribut-check'")
                .contains("Das Tag 'bezeichnung' muss bei 'widmung' 1305 mit 'L' oder 'S' beginnen.");
    }
}