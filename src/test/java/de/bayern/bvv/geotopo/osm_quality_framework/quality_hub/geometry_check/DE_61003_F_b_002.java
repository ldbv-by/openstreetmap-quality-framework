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
 * Bei linienförmiger Modellierung darf kein Objekt der Objektart 42003 Strassenachse, 42008 Fahrwegachse oder 53003 WegPfadSteig mit ART=1106 Radweg oder 1110 Rad- und Fußweg, 42014 Bahnstrecke oder 53006 Gleis auf dem Damm verlaufen.
 * Bei flächenförmiger Modellierung darf kein Objekt der Objektart 42001 Strassenverkehr, 42003 Strassenachse, 42005 Fahrbahnachse, 42008 Fahrwegachse, 53003 WegPfadSteig mit ART=1106 Radweg oder 1110 Rad- und Fußweg, 42010 Bahnverkehr, 42014 Bahnstrecke oder 53006 Gleis auf dem Damm verlaufen.
 * DammWallDeich mit FKT 3001 Hochwasserschutz, Sturmflutschutz darf eine Verkehrsachse in einem identischen Koordinatenpaar berühren.
 * Verkehrswege unter und über der Erdoberfläche sind nicht relevant. Daher werden nur Verkehrswege ohne hDU oder hDU zum 'AX_DammWallDeich' in die Prüfmenge mit einbezogen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_61003_F_b_002 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.61003.F.b.002"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createDammWallDeichHochwasserOhneWegPfadSteig() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-6' changeset='-1' lat='49.88083611148' lon='12.31946066748' />
                  <node id='-5' changeset='-1' lat='49.8806126638' lon='12.32512446848' />
                  <node id='-4' changeset='-1' lat='49.88565239886' lon='12.32481623442' />
                  <node id='-3' changeset='-1' lat='49.88585099833' lon='12.31976890155' />
                  <way id='-2' changeset='-1'>
                    <nd ref='-3' />
                    <nd ref='-4' />
                    <nd ref='-5' />
                    <nd ref='-6' />
                    <nd ref='-3' />
                    <tag k='object_type' v='AX_DammWallDeich' />
                    <tag k='funktion' v='3001' />
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
    void createDammWallDeichHochwasserMitWegPfadSteig() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-1' changeset='-1' lat='49.88282226758' lon='12.32104036708' />
                  <node id='-2' changeset='-1' lat='49.88416287676' lon='12.32119448412' />
                  <node id='-6' changeset='-1' lat='49.88083611148' lon='12.31946066748' />
                  <node id='-5' changeset='-1' lat='49.8806126638' lon='12.32512446848' />
                  <node id='-4' changeset='-1' lat='49.88565239886' lon='12.32481623442' />
                  <node id='-3' changeset='-1' lat='49.88585099833' lon='12.31976890155' />
                  <way id='-1' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <tag k='object_type' v='AX_WegPfadSteig' />
                    <tag k='art' v='1106' />
                  </way>
                  <way id='-2' changeset='-1'>
                    <nd ref='-3' />
                    <nd ref='-4' />
                    <nd ref='-5' />
                    <nd ref='-6' />
                    <nd ref='-3' />
                    <tag k='object_type' v='AX_DammWallDeich' />
                    <tag k='funktion' v='3001' />
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
                .contains("Ein Objekt 'AX_DammWallDeich' mit 'funktion' 3001 darf keinen Verkehrsweg führen.");
    }
}