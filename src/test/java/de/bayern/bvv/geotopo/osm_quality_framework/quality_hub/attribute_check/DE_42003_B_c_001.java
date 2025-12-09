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
 * Um feststellen zu können, wo sich der Attributwert im Sinne der Definition von 'Breite des Verkehrsweges' ändert, werden bei linienförmigen Objekten folgende Klassenangaben gebildet:
 * Klasse 6 (von > 0 m bis <= 6 m Breite des Verkehrsweges)
 * Klasse 9 (von > 6 m bis <= 9 m Breite des Verkehrsweges)
 * Klasse 12 (von > 9 m bis <= 12 m Breite des Verkehrsweges)
 * Klasse 15 (von > 12 m bis <= 15 m Breite des Verkehrsweges)
 * usw. in Schritten von 3 m.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_42003_B_c_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("attribute-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.42003.B.c.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createStrassenachseMitMindestBreitenklasse() throws Exception {
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
                    <tag k='breiteDesVerkehrsweges' v='6' />
                    <tag k='breiteDerFahrbahn' v='9' />
                  </way>
                  <relation id='-80' changeset='-1'>
                    <member type='way' ref='-1308' role='' />
                    <tag k='widmung' v='1301' />
                    <tag k='object_type' v='AX_Strasse' />
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
    void createStrassenachseMitGueltigerBreitenklasse() throws Exception {
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
                    <tag k='breiteDesVerkehrsweges' v='18' />
                    <tag k='breiteDerFahrbahn' v='9' />
                  </way>
                  <relation id='-80' changeset='-1'>
                    <member type='way' ref='-1308' role='' />
                    <tag k='widmung' v='1301' />
                    <tag k='object_type' v='AX_Strasse' />
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
    void createStrassenachseMitUngueltigerBreitenklasse() throws Exception {
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
                    <tag k='breiteDesVerkehrsweges' v='2' />
                    <tag k='breiteDerFahrbahn' v='9' />
                  </way>
                  <relation id='-80' changeset='-1'>
                    <member type='way' ref='-1308' role='' />
                    <tag k='widmung' v='1301' />
                    <tag k='object_type' v='AX_Strasse' />
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
                .contains("Das Attribut 'breiteDesVerkehrsweges' muss immer mit dem Klassenwert 6, 9, 12 oder 15 usw. belegt sein.");
    }
}