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
 * Die Objektart 'Bauteil' muss innerhalb einer Objektart AX_Gebaeude liegen,
 * sofern die Attributart 'LageZurErdoberflaeche' nicht mit dem Wert "Unter der Erdoberfläche" (Wert = 1200) belegt ist.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_31002_G_c_001 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createBauteilAufGebaeude() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.330373467592194" lat="49.87907509996792" version="0"/>
                    <node id="-2" lon="12.331279447036886" lat="49.87907209060794" version="0"/>
                    <node id="-3" lon="12.331284117034025" lat="49.878464196044135" version="0"/>
                    <node id="-4" lon="12.330387477583608" lat="49.87847021483971" version="0"/>
                    <node id="-7" lon="12.330761077354612" lat="49.87884939744812" version="0"/>
                    <node id="-8" lon="12.33076574735175" lat="49.87868689098059" version="0"/>
                    <node id="-9" lon="12.331064627168557" lat="49.87868990036458" version="0"/>
                    <node id="-10" lon="12.331045947180007" lat="49.87885842556917" version="0"/>
                    <way id="-1" version="0">
                        <nd ref="-1"/>
                        <nd ref="-2"/>
                        <nd ref="-3"/>
                        <nd ref="-4"/>
                        <nd ref="-1"/>
                        <tag k='gebaeudefunktion' v='2000' />
                        <tag k='weitereGebaeudefunktion' v='1000' />
                        <tag k='identifikator:UUID' v='DEBYBDLM12345678' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1234567820251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k='object_type' v='AX_Gebaeude' />
                    </way>
                    <way id="-2" version="0">
                        <nd ref="-7"/>
                        <nd ref="-8"/>
                        <nd ref="-9"/>
                        <nd ref="-10"/>
                        <nd ref="-7"/>
                        <tag k="object_type" v="AX_Bauteil"/>
                        <tag k="bauart" v="2710"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM11112222' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111222220251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
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
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result to be valid, but it was invalid.").isTrue();
    }

    @Test
    void createBauteilUnterDerErdoberflaeche() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.330373467592194" lat="49.87907509996792" version="0"/>
                    <node id="-2" lon="12.331279447036886" lat="49.87907209060794" version="0"/>
                    <node id="-3" lon="12.331284117034025" lat="49.878464196044135" version="0"/>
                    <node id="-4" lon="12.330387477583608" lat="49.87847021483971" version="0"/>
                    <node id="-7" lon="12.330761077354612" lat="49.87884939744812" version="0"/>
                    <node id="-8" lon="12.33076574735175" lat="49.87868689098059" version="0"/>
                    <node id="-9" lon="12.331064627168557" lat="49.87868990036458" version="0"/>
                    <node id="-10" lon="12.331045947180007" lat="49.87885842556917" version="0"/>
                    <way id="-2" version="0">
                        <nd ref="-7"/>
                        <nd ref="-8"/>
                        <nd ref="-9"/>
                        <nd ref="-10"/>
                        <nd ref="-7"/>
                        <tag k="object_type" v="AX_Bauteil"/>
                        <tag k="bauart" v="2710"/>
                        <tag k="lageZurErdoberflaeche" v="1200"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM11112222' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111222220251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
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
    void createBauteilOhneGebaeude() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.330373467592194" lat="49.87907509996792" version="0"/>
                    <node id="-2" lon="12.331279447036886" lat="49.87907209060794" version="0"/>
                    <node id="-3" lon="12.331284117034025" lat="49.878464196044135" version="0"/>
                    <node id="-4" lon="12.330387477583608" lat="49.87847021483971" version="0"/>
                    <node id="-7" lon="12.330761077354612" lat="49.87884939744812" version="0"/>
                    <node id="-8" lon="12.33076574735175" lat="49.87868689098059" version="0"/>
                    <node id="-9" lon="12.331064627168557" lat="49.87868990036458" version="0"/>
                    <node id="-10" lon="12.331045947180007" lat="49.87885842556917" version="0"/>
                    <way id="-2" version="0">
                        <nd ref="-7"/>
                        <nd ref="-8"/>
                        <nd ref="-9"/>
                        <nd ref="-10"/>
                        <nd ref="-7"/>
                        <tag k="object_type" v="AX_Bauteil"/>
                        <tag k="bauart" v="2710"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM11112222' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111222220251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
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
                .contains("Ein Objekt das nicht 'lageZurErdoberflaeche' 1200 hat, liegt immer auf einem Objekt 'AX_Gebaeude'.");
    }
}
