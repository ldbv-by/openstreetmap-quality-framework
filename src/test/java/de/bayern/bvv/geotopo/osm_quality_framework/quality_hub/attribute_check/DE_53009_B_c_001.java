package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.attribute_check;

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
 * Der Wert von 'objekthoehe'.'hoehe' muss größer Null sein.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_53009_B_c_001 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createWehrMitGueltigerHoehe() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
            <osmChange version="0.6" generator="JOSM">
            <create>
              <node id='-25433' changeset='-1' lat='49.88559042534' lon='12.32353483847'>
                <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                <tag k='object_type' v='AX_BauwerkImGewaesserbereich' />
                <tag k='bauwerksfunktion' v='2050' />
                <tag k='zustand' v='4000' />
              </node>
              <node id='-25434' changeset='-1' lat='49.88561514895' lon='12.32092479857' />
              <node id='-25432' changeset='-1' lat='49.8855530864' lon='12.32664639346' />
              <way id='-802' changeset='-1'>
                <nd ref='-25432' />
                <nd ref='-25433' />
                <nd ref='-25434' />
                <tag k='identifikator:UUID' v='DEBYBDLM44444444' />
                <tag k='identifikator:UUIDundZeit' v='DEBYBDLM4444444420251014T125300Z' />
                <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                <tag k='object_type' v='AX_Gewaesserachse' />
                <tag k='funktion' v='8300' />
                <tag k='breiteDesGewaessers' v='12' />
                <tag k='fliessrichtung' v='TRUE' />
                <tag k='zustand' v='4000' />
              </way>
              <relation id='-60' changeset='-1'>
                <member type='node' ref='-25433' role='' />
                <tag k='advStandardModell' v='Basis-DLM' />
                <tag k='object_type' v='AA_modellart' />
              </relation>
              <relation id='-70' changeset='-1'>
                <member type='way' ref='-802' role='' />
                <tag k='advStandardModell' v='Basis-DLM' />
                <tag k='object_type' v='AA_modellart' />
              </relation>
              <relation id='-80' changeset='-1'>
                <member type='node' ref='-25433' role='' />
                <tag k='hoehe' v='10' />
                <tag k='object_type' v='AX_objekthoehe' />
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
    void createWehrOhneHoehe() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
            <osmChange version="0.6" generator="JOSM">
            <create>
              <node id='-25433' changeset='-1' lat='49.88559042534' lon='12.32353483847'>
                <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                <tag k='object_type' v='AX_BauwerkImGewaesserbereich' />
                <tag k='bauwerksfunktion' v='2050' />
                <tag k='zustand' v='4000' />
              </node>
              <node id='-25434' changeset='-1' lat='49.88561514895' lon='12.32092479857' />
              <node id='-25432' changeset='-1' lat='49.8855530864' lon='12.32664639346' />
              <way id='-802' changeset='-1'>
                <nd ref='-25432' />
                <nd ref='-25433' />
                <nd ref='-25434' />
                <tag k='identifikator:UUID' v='DEBYBDLM44444444' />
                <tag k='identifikator:UUIDundZeit' v='DEBYBDLM4444444420251014T125300Z' />
                <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                <tag k='object_type' v='AX_Gewaesserachse' />
                <tag k='funktion' v='8300' />
                <tag k='breiteDesGewaessers' v='12' />
                <tag k='fliessrichtung' v='TRUE' />
                <tag k='zustand' v='4000' />
              </way>
              <relation id='-60' changeset='-1'>
                <member type='node' ref='-25433' role='' />
                <tag k='advStandardModell' v='Basis-DLM' />
                <tag k='object_type' v='AA_modellart' />
              </relation>
              <relation id='-70' changeset='-1'>
                <member type='way' ref='-802' role='' />
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
    void createWehrMitUngueltigerHoehe() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
            <osmChange version="0.6" generator="JOSM">
            <create>
              <node id='-25433' changeset='-1' lat='49.88559042534' lon='12.32353483847'>
                <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                <tag k='object_type' v='AX_BauwerkImGewaesserbereich' />
                <tag k='bauwerksfunktion' v='2050' />
                <tag k='zustand' v='4000' />
              </node>
              <node id='-25434' changeset='-1' lat='49.88561514895' lon='12.32092479857' />
              <node id='-25432' changeset='-1' lat='49.8855530864' lon='12.32664639346' />
              <way id='-802' changeset='-1'>
                <nd ref='-25432' />
                <nd ref='-25433' />
                <nd ref='-25434' />
                <tag k='identifikator:UUID' v='DEBYBDLM44444444' />
                <tag k='identifikator:UUIDundZeit' v='DEBYBDLM4444444420251014T125300Z' />
                <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                <tag k='object_type' v='AX_Gewaesserachse' />
                <tag k='funktion' v='8300' />
                <tag k='breiteDesGewaessers' v='12' />
                <tag k='fliessrichtung' v='TRUE' />
                <tag k='zustand' v='4000' />
              </way>
              <relation id='-60' changeset='-1'>
                <member type='node' ref='-25433' role='' />
                <tag k='advStandardModell' v='Basis-DLM' />
                <tag k='object_type' v='AA_modellart' />
              </relation>
              <relation id='-70' changeset='-1'>
                <member type='way' ref='-802' role='' />
                <tag k='advStandardModell' v='Basis-DLM' />
                <tag k='object_type' v='AA_modellart' />
              </relation>
              <relation id='-80' changeset='-1'>
                <member type='node' ref='-25433' role='' />
                <tag k='hoehe' v='test' />
                <tag k='object_type' v='AX_objekthoehe' />
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

        QualityServiceResultDto attributeCheck = qualityHubResultDto.qualityServiceResults().stream()
                .filter(s -> "attribute-check".equals(s.qualityServiceId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("QualityService 'attribute-check' not found"));

        assertThat(attributeCheck.isValid()).withFailMessage("Expected the result is not valid, but it was valid.").isFalse();

        assertThat(attributeCheck.errors())
                .as("Errors of 'attribute-check' must not be empty")
                .isNotEmpty();

        assertThat(attributeCheck.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .as("Error text of 'attribut-check'")
                .contains("Das Tag 'hoehe' der Relation 'AX_objekthoehe' muss größer Null sein.");
    }
}