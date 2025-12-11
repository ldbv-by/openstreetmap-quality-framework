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
 * Flächen mit der Werteart 1200 'Parken' der Attributart 'Funktion' dürfen sich gegenseitig nicht überschneiden.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_40000_G_b_002 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.40000.G.b.002"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createWohnbauflaechenMitFunktionParkenUeberschneidungsfrei() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-42" lon="12.320663009995553" lat="49.87565502776777" version="0"/>
                    <node id="-43" lon="12.320602928120719" lat="49.87468703199092" version="0"/>
                    <node id="-44" lon="12.32206204982482" lat="49.87463724918566" version="0"/>
                    <node id="-45" lon="12.3220877992933" lat="49.875588651520914" version="0"/>
                    <node id="-49" lon="12.323546921652236" lat="49.87554993240587" version="0"/>
                    <node id="-50" lon="12.323495423370119" lat="49.87455427758886" version="0"/>
                    <way id="-9" version="0">
                        <nd ref="-42"/>
                        <nd ref="-43"/>
                        <nd ref="-44"/>
                        <nd ref="-45"/>
                        <nd ref="-42"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
                        <tag k="funktion" v="1200"/>
                    </way>
                    <way id="-10" version="0">
                        <nd ref="-45"/>
                        <nd ref="-49"/>
                        <nd ref="-50"/>
                        <nd ref="-44"/>
                        <nd ref="-45"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
                        <tag k="funktion" v="1200"/>
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
    void createWohnbauflaechenMitFunktionParkenNichtUeberschneidungsfrei() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-42" lon="12.320663009995553" lat="49.87565502776777" version="0"/>
                    <node id="-43" lon="12.320602928120719" lat="49.87468703199092" version="0"/>
                    <node id="-44" lon="12.32206204982482" lat="49.87463724918566" version="0"/>
                    <node id="-45" lon="12.3220877992933" lat="49.875588651520914" version="0"/>
                    <node id="-55" lon="12.322800194269588" lat="49.87523464347068" version="0"/>
                    <node id="-56" lon="12.321375404971846" lat="49.875295488268065" version="0"/>
                    <node id="-57" lon="12.321341073220323" lat="49.87426110805642" version="0"/>
                    <node id="-58" lon="12.322783028393827" lat="49.8742168558173" version="0"/>
                    <way id="-9" version="0">
                        <nd ref="-42"/>
                        <nd ref="-43"/>
                        <nd ref="-44"/>
                        <nd ref="-45"/>
                        <nd ref="-42"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
                        <tag k="funktion" v="1200"/>
                    </way>
                    <way id="-11" version="0">
                        <nd ref="-55"/>
                        <nd ref="-56"/>
                        <nd ref="-57"/>
                        <nd ref="-58"/>
                        <nd ref="-55"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
                        <tag k="funktion" v="1200"/>
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
                .contains("Im Bereich der Objekte \"Tatsächliche Nutzung\" dürfen sich Flächen mit der Funktion Parken nicht überschneiden.");
    }
}