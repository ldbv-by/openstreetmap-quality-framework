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
 * Die Werteart 1620 der Attributart 'Bauwerksfunktion' überlagert bei linienförmiger Modellierung immer ein
 * Objekt 42003 'Straßenachse', 42008 'Fahrwegachse' oder 53003 'Weg Pfad Steig' mit identischer Geometrie.
 *
 * Bei punktförmiger Modellierung liegt die Treppe immer auf der Geometrie eines
 * Objekts 42003 'Straßenachse', 42008 'Fahrwegachse' oder 53003 'Weg Pfad Steig'.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_51009_G_b_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.51009.G.b.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createPunkfoermingeTreppeInnerhalbWegPfadSteig() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25362' changeset='-1' lat='49.88064989274' lon='12.32196506929' />
                  <node id='-25361' changeset='-1' lat='49.88237537717' lon='12.32196506929' />
                  <node id='-25359' changeset='-1' lat='49.87713667792' lon='12.32196506929' />
                  <node id='-25358' changeset='-1' lat='49.88647161642' lon='12.32196506929' />
                  <node id='-25364' changeset='-1' lat='49.88488281818' lon='12.32196506929'>
                    <tag k='object_type' v='AX_SonstigesBauwerkOderSonstigeEinrichtung' />
                    <tag k='bauwerksfunktion' v='1620' />
                  </node>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25358' />
                    <nd ref='-25364' />
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <nd ref='-25359' />
                    <tag k='object_type' v='AX_WegPfadSteig' />
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
    void createLinienfoermigeTreppeInnerhalbWegPfadSteig() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25362' changeset='-1' lat='49.88064989274' lon='12.32196506929' />
                  <node id='-25361' changeset='-1' lat='49.88237537717' lon='12.32196506929' />
                  <node id='-25359' changeset='-1' lat='49.87713667792' lon='12.32196506929' />
                  <node id='-25358' changeset='-1' lat='49.88647161642' lon='12.32196506929' />
                  <node id='-25364' changeset='-1' lat='49.88488281818' lon='12.32196506929' />
                  <way id='-736' changeset='-1'>
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <tag k='object_type' v='AX_SonstigesBauwerkOderSonstigeEinrichtung' />
                    <tag k='bauwerksfunktion' v='1620' />
                  </way>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25358' />
                    <nd ref='-25364' />
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <nd ref='-25359' />
                    <tag k='object_type' v='AX_WegPfadSteig' />
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
                .contains("Ein Objekt mit der 'bauwerksfunktion' 1620 liegt immer auf einem Objekt 'AX_Strassenachse', 'AX_Fahrwegachse' oder 'AX_WegPfadSteig'.");
    }

    @Test
    void createLinienfoermigeTreppeIdentischMitWegPfadSteig() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25362' changeset='-1' lat='49.88064989274' lon='12.32196506929' />
                  <node id='-25361' changeset='-1' lat='49.88237537717' lon='12.32196506929' />
                  <way id='-736' changeset='-1'>
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <tag k='object_type' v='AX_SonstigesBauwerkOderSonstigeEinrichtung' />
                    <tag k='bauwerksfunktion' v='1620' />
                  </way>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <tag k='object_type' v='AX_WegPfadSteig' />
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
    void createTreppeOhneWegPfadSteig() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25364' changeset='-1' lat='49.88488281818' lon='12.32196506929'>
                    <tag k='object_type' v='AX_SonstigesBauwerkOderSonstigeEinrichtung' />
                    <tag k='bauwerksfunktion' v='1620' />
                  </node>
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
                .contains("Ein Objekt mit der 'bauwerksfunktion' 1620 liegt immer auf einem Objekt 'AX_Strassenachse', 'AX_Fahrwegachse' oder 'AX_WegPfadSteig'.");
    }
}
