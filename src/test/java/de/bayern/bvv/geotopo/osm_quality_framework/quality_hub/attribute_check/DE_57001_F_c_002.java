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
 * Höhe des Wasserspiegels' ist die Differenz zwischen dem mittleren Wasserstand
 * und der Höhenbezugsfläche in [m] auf [dm] gerundet.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_57001_F_c_002 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createWasserspiegelhoeheMitKorrektenWasserspiegel() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-3" lon="12.330429445329703" lat="49.87983268777027" version="0"/>
                    <node id="-4" lon="12.331322348839416" lat="49.87981824296623" version="0"/>
                    <node id="-5" lon="12.331314876672975" lat="49.87910322488495" version="0"/>
                    <node id="-6" lon="12.33037340536405" lat="49.87910803978428" version="0"/>
                    <node id="-9" lon="12.330732061087211" lat="49.87944267925359" version="0">
                        <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k='object_type' v='AX_Wasserspiegelhoehe' />
                        <tag k='hoeheDesWasserspiegels' v='3.400' />
                    </node>
                    <way id="-2" version="0">
                        <nd ref="-3"/>
                        <nd ref="-4"/>
                        <nd ref="-5"/>
                        <nd ref="-6"/>
                        <nd ref="-3"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM12345678' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1234567820251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k='object_type' v='AX_Hafenbecken' />
                    </way>
                    <relation id='-1' changeset='-1'>
                        <member type='node' ref='-9' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-60' changeset='-1'>
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
    void createWasserspiegelhoeheMitFlaschenWasserspiegel() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-3" lon="12.330429445329703" lat="49.87983268777027" version="0"/>
                    <node id="-4" lon="12.331322348839416" lat="49.87981824296623" version="0"/>
                    <node id="-5" lon="12.331314876672975" lat="49.87910322488495" version="0"/>
                    <node id="-6" lon="12.33037340536405" lat="49.87910803978428" version="0"/>
                    <node id="-9" lon="12.330732061087211" lat="49.87944267925359" version="0">
                        <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k='object_type' v='AX_Wasserspiegelhoehe' />
                        <tag k='hoeheDesWasserspiegels' v='test' />
                    </node>
                    <way id="-2" version="0">
                        <nd ref="-3"/>
                        <nd ref="-4"/>
                        <nd ref="-5"/>
                        <nd ref="-6"/>
                        <nd ref="-3"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM12345678' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1234567820251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k='object_type' v='AX_Hafenbecken' />
                    </way>
                    <relation id='-1' changeset='-1'>
                        <member type='node' ref='-9' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-60' changeset='-1'>
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
                .contains("Die Wasserspiegelhöhe ist in [m] auf [dm] gerundet anzugeben.");
    }
}