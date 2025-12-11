package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.geometry_check;

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
 * Die Objektart 'Bauteil' muss innerhalb einer Objektart AX_Gebaeude liegen,
 * sofern die Attributart 'LageZurErdoberflaeche' nicht mit dem Wert "Unter der Erdoberfläche" (Wert = 1200) belegt ist.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_31002_G_c_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.31002.G.c.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createBauteilAufGebaeude() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.330373467592194" lat="49.87907509996792" version="0"/>
                    <node id="-2" lon="12.331279447036886" lat="49.87907209060794" version="0"/>
                    <node id="-3" lon="12.331284117034025" lat="49.878464196044135" version="0"/>
                    <node id="-4" lon="12.330387477583608" lat="49.87847021483971" version="0"/>
                    <node id="-7" lon="12.330761077354612" lat="49.87884939744812" version="0"/>
                    <node id="-8" lon="12.33076574735175" lat="49.87868689098059" version="0"/>
                    <node id="-9" lon="12.331064627168557" lat="49.87868990036458" version="0"/>
                    <node id="-10" lon="12.331045947180007" lat="49.87885842556917" version="0"/>
                    <way id="-1" version="0">
                        <nd ref="-1"/>
                        <nd ref="-2"/>
                        <nd ref="-3"/>
                        <nd ref="-4"/>
                        <nd ref="-1"/>
                        <tag k='object_type' v='AX_Gebaeude' />
                        <tag k='gebaeudefunktion' v='2000' />
                        <tag k='weitereGebaeudefunktion' v='1000' />
                    </way>
                    <way id="-2" version="0">
                        <nd ref="-7"/>
                        <nd ref="-8"/>
                        <nd ref="-9"/>
                        <nd ref="-10"/>
                        <nd ref="-7"/>
                        <tag k="object_type" v="AX_Bauteil"/>
                        <tag k="bauart" v="2710"/>
                    </way>
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
    void createBauteilUnterDerErdoberflaeche() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.330373467592194" lat="49.87907509996792" version="0"/>
                    <node id="-2" lon="12.331279447036886" lat="49.87907209060794" version="0"/>
                    <node id="-3" lon="12.331284117034025" lat="49.878464196044135" version="0"/>
                    <node id="-4" lon="12.330387477583608" lat="49.87847021483971" version="0"/>
                    <node id="-7" lon="12.330761077354612" lat="49.87884939744812" version="0"/>
                    <node id="-8" lon="12.33076574735175" lat="49.87868689098059" version="0"/>
                    <node id="-9" lon="12.331064627168557" lat="49.87868990036458" version="0"/>
                    <node id="-10" lon="12.331045947180007" lat="49.87885842556917" version="0"/>
                    <way id="-2" version="0">
                        <nd ref="-7"/>
                        <nd ref="-8"/>
                        <nd ref="-9"/>
                        <nd ref="-10"/>
                        <nd ref="-7"/>
                        <tag k="object_type" v="AX_Bauteil"/>
                        <tag k="bauart" v="2710"/>
                        <tag k="lageZurErdoberflaeche" v="1200"/>
                    </way>
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
    void createBauteilOhneGebaeude() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.330373467592194" lat="49.87907509996792" version="0"/>
                    <node id="-2" lon="12.331279447036886" lat="49.87907209060794" version="0"/>
                    <node id="-3" lon="12.331284117034025" lat="49.878464196044135" version="0"/>
                    <node id="-4" lon="12.330387477583608" lat="49.87847021483971" version="0"/>
                    <node id="-7" lon="12.330761077354612" lat="49.87884939744812" version="0"/>
                    <node id="-8" lon="12.33076574735175" lat="49.87868689098059" version="0"/>
                    <node id="-9" lon="12.331064627168557" lat="49.87868990036458" version="0"/>
                    <node id="-10" lon="12.331045947180007" lat="49.87885842556917" version="0"/>
                    <way id="-2" version="0">
                        <nd ref="-7"/>
                        <nd ref="-8"/>
                        <nd ref="-9"/>
                        <nd ref="-10"/>
                        <nd ref="-7"/>
                        <tag k="object_type" v="AX_Bauteil"/>
                        <tag k="bauart" v="2710"/>
                    </way>
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

        QualityServiceResultDto geometryCheck = qualityHubResultDto.qualityServiceResults().stream()
                .filter(s -> "geometry-check".equals(s.qualityServiceId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("QualityService 'geometry-check' not found"));

        assertThat(geometryCheck.isValid()).withFailMessage("Expected the result is not valid, but it was valid.").isFalse();

        assertThat(geometryCheck.errors())
                .as("Errors of 'geometry-check' must not be empty")
                .isNotEmpty();

        assertThat(geometryCheck.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .as("Error text of 'geometry-check'")
                .contains("Ein Objekt das nicht 'lageZurErdoberflaeche' 1200 hat, liegt immer auf einem Objekt 'AX_Gebaeude'.");
    }
}
