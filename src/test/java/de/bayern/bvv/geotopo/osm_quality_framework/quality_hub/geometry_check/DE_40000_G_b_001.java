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
 * Linienförmige Objekte aus dem Bereich der Tatsächlichen Nutzung (AX_Gewaesserachse, AX_Bahnstrecke, AX_Fahrbahnachse, AX_Fahrwegachse, AX_Strassenachse)
 * dürfen nur eine Geometrieidentität - mit einem anderen linienförmigen Objekt der TN, einem Objekt 53001 AX_BauwerkImVerkehrsbereich, 53009 AX_BauwerkImGewaesserbereich oder 61003 AX_DammWallDeich - besitzen,
 * wenn bei solch einem linienförmigen Objekt der TN eine Relation hatDirektUnten zu einem Objekt 53001 AX_BauwerkImVerkehrsbereich, 53009 AX_BauwerkImGewaesserbereich oder 61003 AX_DammWallDeich besteht.
 * <p>
 * Ausnahmen:
 * Bahnstrecke darf auf Straßen- oder Fahrbahnachse verlaufen;
 * Fahrbahnachse darf auf Straßenachse verlaufen;
 * <p>
 * Bahnstrecke, Fahrbahnachse, Fahrwegachse, Strassenachse mit Geometrieidentität zu 53001 AX_BauwerkImVerkehrsbereich BWF 1900 Durchfahrt oder
 * BWF 1880 Schutzgalerie stets ohne Relation hatDirektUnten;
 * <p>
 * Zwei geometrieidentische linienförmige Objekte der TN ohne eine Relation hatDirektUnten zu einem Objekt 53001 AX_BauwerkImVerkehrsbereich, 53009 AX_BauwerkImGewaesserbereich oder
 * 61001 AX_DammWallDeich sind nicht zulässig – bis auf die zuvor genannten Ausnahmen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_40000_G_b_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.40000.G.b.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createBahnstreckeAufStrassenachse() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.317446189523055" lat="49.87283851843256" version="0"/>
                        <node id="-2" lon="12.318295913469939" lat="49.872866176770636" version="0"/>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_Bahnstrecke"/>
                        </way>
                        <way id="-2" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
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
    void createBahnstreckeAufFahrwegachse() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.317446189523055" lat="49.87283851843256" version="0"/>
                        <node id="-2" lon="12.318295913469939" lat="49.872866176770636" version="0"/>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_Bahnstrecke"/>
                        </way>
                        <way id="-2" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_Fahrwegachse"/>
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
                .contains("Geometrieidentische Überlagerung darf nur mit HDU vorkommen.");
    }

    @Test
    void createStrassenachseMitBrueckeOhneHdu() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.317446189523055" lat="49.87283851843256" version="0"/>
                        <node id="-2" lon="12.318295913469939" lat="49.872866176770636" version="0"/>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-2" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                            <tag k="bauwerksfunktion" v="1800"/>
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
    void createStrassenachseAufBrueckeOhneHdu() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.317446189523055" lat="49.87283851843256" version="0"/>
                        <node id="-2" lon="12.318295913469939" lat="49.872866176770636" version="0"/>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-2" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                            <tag k="bauwerksfunktion" v="1800"/>
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
                .contains("Geometrieidentische Überlagerung darf nur mit HDU vorkommen.");
    }
}