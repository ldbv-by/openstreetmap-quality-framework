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
 * Die Werteart 2000 'Furt' der Attributart 'Art' darf kein Gewässer überlagern,
 * dass durch ein Objekt der Objektart 53009 'Bauwerk im Gewässerbereich' mit BWF 2010 – 2013 fließt.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_53002_F_c_001 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createDurchlassMitGewaesserachse() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25366' changeset='-1' lat='49.88418770251' lon='12.32312094704' />
                  <node id='-25365' changeset='-1' lat='49.88279744114' lon='12.32304388852' />
                  <way id='-667' changeset='-1'>
                    <nd ref='-25365' />
                    <nd ref='-25366' />
                    <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_BauwerkImGewaesserbereich' />
                    <tag k='bauwerksfunktion' v='2010' />
                  </way>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25365' />
                    <nd ref='-25366' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_Gewaesserachse' />
                    <tag k='funktion' v='8300' />
                    <tag k='breiteDesGewaessers' v='6' />
                    <tag k='fliessrichtung' v='TRUE' />
                    <tag k='zustand' v='4000' />
                  </way>
                  <relation id='-70' changeset='-1'>
                    <member type='way' ref='-663' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-60' changeset='-1'>
                    <member type='way' ref='-667' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-50' changeset='-1'>
                    <member type='way' ref='-663' role='over' />
                    <member type='way' ref='-667' role='under' />
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
    void createDurchlassMitGewaesserachseUndDarueberLiegenderFurt() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25366' changeset='-1' lat='49.88418770251' lon='12.32312094704' />
                  <node id='-25365' changeset='-1' lat='49.88279744114' lon='12.32304388852' />
                  <way id='-667' changeset='-1'>
                    <nd ref='-25365' />
                    <nd ref='-25366' />
                    <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_BauwerkImGewaesserbereich' />
                    <tag k='bauwerksfunktion' v='2010' />
                  </way>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25365' />
                    <nd ref='-25366' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_Gewaesserachse' />
                    <tag k='funktion' v='8300' />
                    <tag k='breiteDesGewaessers' v='6' />
                    <tag k='fliessrichtung' v='TRUE' />
                    <tag k='zustand' v='4000' />
                  </way>
                  <way id='-700' changeset='-1'>
                    <nd ref='-25365' />
                    <nd ref='-25366' />
                    <tag k='identifikator:UUID' v='DEBYBDLM22222222' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM2222222220251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_Strassenverkehrsanlage' />
                    <tag k='art' v='2000' />
                  </way>
                  <relation id='-70' changeset='-1'>
                    <member type='way' ref='-663' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-60' changeset='-1'>
                    <member type='way' ref='-667' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-50' changeset='-1'>
                    <member type='way' ref='-663' role='over' />
                    <member type='way' ref='-667' role='under' />
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
                .contains("Furt darf ein unter der Erdoberfläche verlaufendes Gewässer nicht überlagern.");
    }
}
