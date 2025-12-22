package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.attribute_check;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.config.JtsJackson3Module;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.dto.QualityHubResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceErrorDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
 * Verläuft ein Objekt der Objektart 42001 'AX_Strassenverkehr', 42003 'AX_Strassenachse', 42005 'Fahrbahnachse', 42008 'AX_Fahrwegachse', 42010 'AX_Bahnverkehr',
 * 42014 'AX_Bahnstrecke', 53003 'AX_WegPfadSteig' oder 53006 'AX_Gleis' innerhalb eines Objektes der Objektart
 * 53009 AX_BauwerkImGewaesserbereich mit BWF 2030, 2040, 2050, 2060, 2080, 2131, 2133 so muss dieses Objekt eine hDU-Relation zu dem Bauwerk im Gewässerbereich besitzen,
 * es sei denn das Objekt hat bereits eine hDU-Relation zu einem Bauwerk im Verkehrsbereich.
 *
 * a) Bei linienförmiger Erfassung von AX_BauwerkImGewaesserbereich ist das daraufliegende Verkehrsobjekt immer geometrieidentisch (auch verkettet) mit diesem.
 * b) Bei flächenförmiger Erfassung von AX_BauwerkImGewaesserbereich müssen die Verkehrsobjekte innerhalb der Umrissgeometrie des Objekts 53009 'Bauwerk im Gewässerbereich' liegen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_53009_R_c_004 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("attribute-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.53009.R.c.004"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createStaudammMitHDU() throws Exception {
        // Arrange
        final String CHANGESET_XML;
        CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.317446189523055" lat="49.87283851843256" version="0"/>
                        <node id="-2" lon="12.318295913469939" lat="49.872866176770636" version="0"/>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_WegPfadSteig"/>
                        </way>
                        <way id="-2" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_BauwerkImGewaesserbereich"/>
                            <tag k="bauwerksfunktion" v="2040"/>
                        </way>
                        <relation id="-1" version="0">
                            <member type="way" role="under" ref="-2"/>
                            <member type="way" role="over" ref="-1"/>
                            <tag k="object_type" v="AA_hatDirektUnten"/>
                        </relation>
                    </create>
                    <modify/>
                    <delete if-unused="true"/>
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
    void createStaudammOhneHDU() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.317446189523055" lat="49.87283851843256" version="0"/>
                        <node id="-2" lon="12.318295913469939" lat="49.872866176770636" version="0"/>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_WegPfadSteig"/>
                        </way>
                        <way id="-2" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_BauwerkImGewaesserbereich"/>
                            <tag k="bauwerksfunktion" v="2040"/>
                        </way>
                    </create>
                    <modify/>
                    <delete if-unused="true"/>
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
                .as("Error text of 'attribute-check'")
                .contains("Das Bauwerk wird nicht korrekt referenziert.");
    }
}
