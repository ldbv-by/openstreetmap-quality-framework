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
 * Bei linienförmiger Modellierung der Wertearten 1800 bis 1870 der Attributart 'Bauwerksfunktion'
 * ist bei einer Überlagerung durch Objekte 42003 'Straßenachse', 42008 'Fahrwegachse', 42014 'Bahnstrecke',
 * 44004 'Gewässerachse', 53003 'Weg, Pfad, Steig' oder 53006 'Gleis' die Geometrie immer identisch,
 *
 * bei flächenförmiger Modellierung liegen die überlagernden Objekte immer innerhalb der Umrissgeometrie des
 * Objekts 53001 'Bauwerk im Verkehrsbereich'.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_53001_G_b_001 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createBrueckeMitDarueberliegenderBahnstrecke() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25362' changeset='-1' lat='49.88064989274' lon='12.32196506929' />
                  <node id='-25361' changeset='-1' lat='49.88237537717' lon='12.32196506929' />
                  <way id='-663' changeset='-1'>
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <tag k='identifikator:UUID' v='DEBYBDLM12345678' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1234567820251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_Bahnstrecke' />
                    <tag k='bahnkategorie' v='1100' />
                    <tag k='elektrifizierung' v='2000' />
                    <tag k='anzahlDerStreckengleise' v='1000' />
                    <tag k='spurweite' v='1000' />
                  </way>
                  <way id='-600' changeset='-1'>
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_BauwerkImVerkehrsbereich' />
                    <tag k='bauwerksfunktion' v='1800' />
                  </way>
                  <relation id='-60' changeset='-1'>
                    <member type='way' ref='-663' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-80' changeset='-1'>
                    <member type='way' ref='-600' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-100' changeset='-1'>
                    <member type='way' ref='-663' role='over' />
                    <member type='way' ref='-600' role='under' />
                    <tag k='object_type' v='AA_hatDirektUnten' />
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
    void createBrueckeMitNichtKompletterBahnstrecke() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25362' changeset='-1' lat='49.88064989274' lon='12.32196506929' />
                  <node id='-25361' changeset='-1' lat='49.88237537717' lon='12.32196506929' />
                  <node id='-25364' changeset='-1' lat='49.88488281818' lon='12.32196506929' />
                  <way id='-663' changeset='-1'>
                    <nd ref='-25361' />
                    <nd ref='-25364' />
                    <tag k='identifikator:UUID' v='DEBYBDLM12345678' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1234567820251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_Bahnstrecke' />
                    <tag k='bahnkategorie' v='1100' />
                    <tag k='elektrifizierung' v='2000' />
                    <tag k='anzahlDerStreckengleise' v='1000' />
                    <tag k='spurweite' v='1000' />
                  </way>
                  <way id='-600' changeset='-1'>
                    <nd ref='-25364' />
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_BauwerkImVerkehrsbereich' />
                    <tag k='bauwerksfunktion' v='1800' />
                  </way>
                  <relation id='-60' changeset='-1'>
                    <member type='node' ref='-663' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-80' changeset='-1'>
                    <member type='way' ref='-600' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-100' changeset='-1'>
                    <member type='way' ref='-663' role='over' />
                    <member type='way' ref='-600' role='under' />
                    <tag k='object_type' v='AA_hatDirektUnten' />
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
                .contains("Bei 'bauwerksfunktion' 1800 bis 1870 und linienförmiger Modellierung müssen die Geometrien der HDU Relations identisch sein. Bei flächenförmiger Modellierung müssen die 'over' in 'under' enthalten sein.");
    }
}
