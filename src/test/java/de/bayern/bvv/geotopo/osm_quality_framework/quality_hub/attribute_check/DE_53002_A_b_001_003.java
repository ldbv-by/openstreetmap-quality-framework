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
 * Die Attributart 'Bezeichnung' kann nur in Verbindung mit der
 * Attributart 'Art' und den Wertearten 3000 bis 3003 vorkommen.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_53002_A_b_001_003 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createKreuzMitBezeichnung() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25433' changeset='-1' lat='49.88559042534' lon='12.32353483847'>
                    <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_Strassenverkehrsanlage' />
                    <tag k='art' v='3001' />
                    <tag k='bezeichnung' v='Test1;Test2' />
                    <tag k='name' v='Kreuz' />
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
                    <tag k='object_type' v='AX_Strassenachse' />
                    <tag k='breiteDerFahrbahn' v='9' />
                  </way>
                  <relation id='-80' changeset='-1'>
                    <member type='way' ref='-802' role='' />
                    <tag k='widmung' v='1301' />
                    <tag k='object_type' v='AX_Strasse' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11112222' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111222220251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                  </relation>
                  <relation id='-79' changeset='-1'>
                    <member type='relation' ref='-80' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
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
    void createFurtMitBezeichnung() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25402' changeset='-1' lat='49.88567721142' lon='12.33907207933'>
                    <tag k='art' v='2000' />
                    <tag k='bezeichnung' v='Bezeichnung1;Bezeichnung2' />
                    <tag k='identifikator:UUID' v='DEBYBDLM12345678' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1234567820251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_Strassenverkehrsanlage' />
                  </node>
                  <relation id='-63' changeset='-1'>
                    <member type='node' ref='-25402' role='' />
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
                .contains("Das Tag 'bezeichnung' darf nur bei der 'art' 3000 bis 3003 vorkommen.");
    }
}