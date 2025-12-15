package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.geometry_check;

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
 * Gemäß AX_Themendefinition und Topologiethema 'Gewaesserachsen Basis-DLM' müssen Gewässerachsen (OA 44004, 57003, 57004)
 * jeweils über topologische Knoten verbunden sein und lösen damit an Kreuzungspunkten eine REO Bildung aus.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_02000_G_a_025 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.02000.G.a.025"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createWassernetzMitKorrekterReoBildung() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.318230721144031" lat="49.872907368824826" version="0"/>
                        <node id="-2" lon="12.318238193025438" lat="49.87237283544489" version="0"/>
                        <node id="-5" lon="12.318241929536212" lat="49.87169141769891" version="0"/>
                        <node id="-7" lon="12.319355256397756" lat="49.87238005958586" version="0"/>
                        <node id="-9" lon="12.317248153917303" lat="49.8723752437401" version="0"/>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_Sickerstrecke"/>
                            <tag k="fliessrichtung" v="TRUE"/>
                        </way>
                        <way id="-2" version="0">
                            <nd ref="-2"/>
                            <nd ref="-5"/>
                            <tag k="object_type" v="AX_Sickerstrecke"/>
                        </way>
                        <way id="-3" version="0">
                            <nd ref="-2"/>
                            <nd ref="-7"/>
                            <tag k="object_type" v="AX_Sickerstrecke"/>
                            <tag k="fliessrichtung" v="TRUE"/>
                        </way>
                        <way id="-4" version="0">
                            <nd ref="-9"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_Sickerstrecke"/>
                            <tag k="fliessrichtung" v="TRUE"/>
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
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result to be valid, but it was invalid.").isTrue();
    }

    @Test
    void createWassernetzMitFehlenderReoBildung() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.318230721144031" lat="49.872907368824826" version="0"/>
                        <node id="-2" lon="12.318200833048337" lat="49.87152527433903" version="0"/>
                        <node id="-20" lon="12.31821562672491" lat="49.87220937301096" version="0"/>
                        <node id="-38" lon="12.317102449949605" lat="49.87231023188308" version="0"/>
                        <node id="-39" lon="12.318217288281488" lat="49.87228620716961" version="0"/>
                        <node id="-40" lon="12.319448656055474" lat="49.872266890936395" version="0"/>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-39"/>
                            <nd ref="-20"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_Sickerstrecke"/>
                            <tag k="fliessrichtung" v="TRUE"/>
                        </way>
                        <way id="-6" version="0">
                            <nd ref="-38"/>
                            <nd ref="-39"/>
                            <nd ref="-40"/>
                            <tag k="object_type" v="AX_Sickerstrecke"/>
                            <tag k="fliessrichtung" v="TRUE"/>
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
                .contains("Fehlende REO Bildung.");
    }
}