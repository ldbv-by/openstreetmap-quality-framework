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
 * 1. Ein Objekt der Objektart 53008 AX_EinrichtungenFuerDenSchiffsverkehr mit der Werteart 1460 "art",
 * zu dem hDU-Relationen durch ein oder mehrere Objekte der Objektarten 42003 AX_Strassenachse, 42008 AX_Fahrwegachse,
 * 42014 Bahnstrecke, 53003 AX_WegPfadSteig oder 53006 Gleis bestehen, muss bei linienförmiger Modellierung
 * immer durch ein oder mehrere verkettete Objekte dieser Objektarten genau abgedeckt werden.
 * All diese Objekte müssen hDU-Relationen zur Einrichtung besitzen und dürfen nicht über die Einrichtung hinausragen.
 * 2. Jedes Objekt mit einer hDU-Relation zu einem flächenförmigen Objekt der Objektart 53008 AX_EinrichtungenFuerDenSchiffsverkehr
 * mit der Werteart 1460 "art", muss innerhalb der Umrissgeometrie dieser EinrichtungenFuerDenSchiffsverkehr liegen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_53008_G_b_003_F_b_003 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.53008.G.b.003_F.b.003"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createAnlegerMitDarueberliegenderBahnstrecke() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25364' changeset='-1' lat='49.88391203301' lon='12.32100867214' />
                  <node id='-25363' changeset='-1' lat='49.88344292461' lon='12.31757273382' />
                  <node id='-25361' changeset='-1' lat='49.88225125607' lon='12.32115595486' />
                  <node id='-25360' changeset='-1' lat='49.88220160258' lon='12.32481623442' />
                  <node id='-25359' changeset='-1' lat='49.88582617344' lon='12.32489329293' />
                  <node id='-25358' changeset='-1' lat='49.88572687375' lon='12.32084772079' />
                  <way id='-667' changeset='-1'>
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <tag k='object_type' v='AX_EinrichtungenFuerDenSchiffsverkehr' />
                    <tag k='art' v='1460' />
                  </way>
                  <way id='-700' changeset='-1'>
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <tag k='object_type' v='AX_Bahnstrecke' />
                    <tag k='bahnkategorie' v='1100' />
                    <tag k='elektrifizierung' v='2000' />
                    <tag k='anzahlDerStreckengleise' v='1000' />
                    <tag k='spurweite' v='1000' />
                  </way>
                  <relation id='-100' changeset='-1'>
                    <member type='way' ref='-667' role='under' />
                    <member type='way' ref='-700' role='over' />
                    <tag k='object_type' v='AA_hatDirektUnten' />
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
    void createAnlegerMitNichtKompletterBahnstrecke() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25364' changeset='-1' lat='49.88391203301' lon='12.32100867214' />
                  <node id='-25363' changeset='-1' lat='49.88344292461' lon='12.31757273382' />
                  <node id='-25361' changeset='-1' lat='49.88225125607' lon='12.32115595486' />
                  <node id='-25360' changeset='-1' lat='49.88220160258' lon='12.32481623442' />
                  <node id='-25359' changeset='-1' lat='49.88582617344' lon='12.32489329293' />
                  <node id='-25358' changeset='-1' lat='49.88572687375' lon='12.32084772079' />
                  <node id='-25401' changeset='-1' lat='49.88348259962' lon='12.31321296609' />
                  <way id='-667' changeset='-1'>
                    <nd ref='-25401' />
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <tag k='object_type' v='AX_EinrichtungenFuerDenSchiffsverkehr' />
                    <tag k='art' v='1460' />
                  </way>
                  <way id='-700' changeset='-1'>
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <tag k='object_type' v='AX_Bahnstrecke' />
                    <tag k='bahnkategorie' v='1100' />
                    <tag k='elektrifizierung' v='2000' />
                    <tag k='anzahlDerStreckengleise' v='1000' />
                    <tag k='spurweite' v='1000' />
                  </way>
                  <relation id='-100' changeset='-1'>
                    <member type='way' ref='-667' role='under' />
                    <member type='way' ref='-700' role='over' />
                    <tag k='object_type' v='AA_hatDirektUnten' />
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
                .contains("Bei 'art' 1460 und linienförmiger Modellierung müssen die Geometrien der HDU Relations identisch sein. Bei flächenförmiger Modellierung müssen die 'over' in 'under' enthalten sein.");
    }
}
