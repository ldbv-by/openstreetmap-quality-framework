package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.geometry_check;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.config.JacksonConfiguration;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.dto.QualityHubResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceErrorDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
@Import(JacksonConfiguration.class)
class DE_61003_F_b_002 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createDammWallDeichHochwasserOhneWegPfadSteig() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
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
                    <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_DammWallDeich' />
                    <tag k='funktion' v='3001' />
                  </way>
                  <relation id='-2' changeset='-1'>
                    <member type='way' ref='-2' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                </create>
                </osmChange>
                """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML))
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
        final Long CHANGESET_ID = 1L;
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
                    <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_WegPfadSteig' />
                    <tag k='art' v='1106' />
                  </way>
                  <way id='-2' changeset='-1'>
                    <nd ref='-3' />
                    <nd ref='-4' />
                    <nd ref='-5' />
                    <nd ref='-6' />
                    <nd ref='-3' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_DammWallDeich' />
                    <tag k='funktion' v='3001' />
                  </way>
                  <relation id='-1' changeset='-1'>
                    <member type='way' ref='-1' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                <relation id='-2' changeset='-1'>
                    <member type='way' ref='-2' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                </create>
                </osmChange>
                """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML))
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