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
 * Im Gemeindekennzeichen muss die Attributart 'Gemeindeteil' belegt sein.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_75012_A_b_002 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createKommunalesTeilgebietMitGemeindeteil() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25394' changeset='-1' lat='49.87416602228' lon='12.32177006323' />
                  <node id='-25393' changeset='-1' lat='49.87416602228' lon='12.32515603631' />
                  <node id='-25392' changeset='-1' lat='49.87826679058' lon='12.32548684977' />
                  <node id='-25391' changeset='-1' lat='49.87831695072' lon='12.32177006323' />
                  <node id='-25389' changeset='-1' lat='49.87776518624' lon='12.32408575746' />
                  <node id='-25388' changeset='-1' lat='49.8749357157' lon='12.32409063939' />
                  <node id='-25364' changeset='-1' lat='49.87494356474' lon='12.32280142285' />
                  <node id='-25363' changeset='-1' lat='49.87777772641' lon='12.32285980169' />
                  <way id='-1656' changeset='-1'>
                    <nd ref='-25391' />
                    <nd ref='-25392' />
                    <nd ref='-25393' />
                    <nd ref='-25394' />
                    <nd ref='-25391' />
                    <tag k='bezeichnung' v='Test' />
                    <tag k='schluesselGesamt' v='0000000000' />
                    <tag k='gemeindekennzeichen:gemeinde' v='00' />
                    <tag k='gemeindekennzeichen:gemeindeteil' v='00' />
                    <tag k='gemeindekennzeichen:kreis' v='00' />
                    <tag k='gemeindekennzeichen:land' v='00' />
                    <tag k='gemeindekennzeichen:regierungsbezirk' v='00' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11112222' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111222220251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_Gemeindeteil' />
                  </way>
                  <way id='-1308' changeset='-1'>
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <nd ref='-25388' />
                    <nd ref='-25389' />
                    <nd ref='-25363' />
                    <tag k='bezeichnung' v='Test' />
                    <tag k='schluesselGesamt' v='0000000000' />
                    <tag k='hierarchiename' v='Hierarchie' />
                    <tag k='hierarchiestufe' v='1' />
                    <tag k='identifikator:UUID' v='DEBYBDLM12345678' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1234567820251014T125300Z' />
                    <tag k='kennzeichen:gemeinde' v='00' />
                    <tag k='kennzeichen:gemeindeteil' v='00' />
                    <tag k='kennzeichen:kreis' v='00' />
                    <tag k='kennzeichen:land' v='00' />
                    <tag k='kennzeichen:regierungsbezirk' v='00' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_KommunalesTeilgebiet' />
                  </way>
                  <relation id='-90' changeset='-1'>
                    <member type='way' ref='-1656' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-81' changeset='-1'>
                    <member type='way' ref='-1308' role='' />
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
    void createKommunalesTeilgebietOhneGemeindeteil() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25394' changeset='-1' lat='49.87416602228' lon='12.32177006323' />
                  <node id='-25393' changeset='-1' lat='49.87416602228' lon='12.32515603631' />
                  <node id='-25392' changeset='-1' lat='49.87826679058' lon='12.32548684977' />
                  <node id='-25391' changeset='-1' lat='49.87831695072' lon='12.32177006323' />
                  <node id='-25389' changeset='-1' lat='49.87776518624' lon='12.32408575746' />
                  <node id='-25388' changeset='-1' lat='49.8749357157' lon='12.32409063939' />
                  <node id='-25364' changeset='-1' lat='49.87494356474' lon='12.32280142285' />
                  <node id='-25363' changeset='-1' lat='49.87777772641' lon='12.32285980169' />
                  <way id='-1656' changeset='-1'>
                    <nd ref='-25391' />
                    <nd ref='-25392' />
                    <nd ref='-25393' />
                    <nd ref='-25394' />
                    <nd ref='-25391' />
                    <tag k='bezeichnung' v='Test' />
                    <tag k='schluesselGesamt' v='1000000000' />
                    <tag k='gemeindekennzeichen:gemeinde' v='00' />
                    <tag k='gemeindekennzeichen:gemeindeteil' v='00' />
                    <tag k='gemeindekennzeichen:kreis' v='00' />
                    <tag k='gemeindekennzeichen:land' v='10' />
                    <tag k='gemeindekennzeichen:regierungsbezirk' v='00' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11112222' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111222220251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_Gemeindeteil' />
                  </way>
                  <way id='-1308' changeset='-1'>
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <nd ref='-25388' />
                    <nd ref='-25389' />
                    <nd ref='-25363' />
                    <tag k='bezeichnung' v='Test' />
                    <tag k='schluesselGesamt' v='0000000000' />
                    <tag k='hierarchiename' v='Hierarchie' />
                    <tag k='hierarchiestufe' v='1' />
                    <tag k='identifikator:UUID' v='DEBYBDLM12345678' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1234567820251014T125300Z' />
                    <tag k='kennzeichen:gemeinde' v='00' />
                    <tag k='kennzeichen:kreis' v='00' />
                    <tag k='kennzeichen:land' v='00' />
                    <tag k='kennzeichen:regierungsbezirk' v='00' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_KommunalesTeilgebiet' />
                  </way>
                  <relation id='-90' changeset='-1'>
                    <member type='way' ref='-1656' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-81' changeset='-1'>
                    <member type='way' ref='-1308' role='' />
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
                .contains("Das Tag 'kennzeichen:gemeindeteil' muss bei einem Gemeindeteil belegt sein.");
    }
}