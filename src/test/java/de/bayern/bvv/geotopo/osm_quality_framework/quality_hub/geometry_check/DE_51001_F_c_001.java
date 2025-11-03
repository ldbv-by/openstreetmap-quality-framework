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
 * 51001 'Turm' mit der Attributart 'Bauwerksfunktion' und der Werteart 1004 'Kontrollturm' muss innerhalb 42015 'Flugverkehr' liegen.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_51001_F_c_001 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createKontrollturmInnerhalbFlugverkehr() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.329861221139945" lat="49.88190076711952" version="0"/>
                        <node id="-2" lon="12.330907300498763" lat="49.88190076711952" version="0"/>
                        <node id="-3" lon="12.330921310490178" lat="49.88108226219371" version="0"/>
                        <node id="-4" lon="12.329935941094147" lat="49.88111235453228" version="0"/>
                        <node id="-7" lon="12.33031421086229" lat="49.88160285700639" version="0">
                            <tag k="object_type" v="AX_Turm"/>
                            <tag k="bauwerksfunktion" v="1004"/>
                            <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                            <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                            <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        </node>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <nd ref="-3"/>
                            <nd ref="-4"/>
                            <nd ref="-1"/>
                            <tag k="object_type" v="AX_Flugverkehr"/>
                            <tag k='identifikator:UUID' v='DEBYBDLM11111112' />
                            <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111220251014T125300Z' />
                            <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                            <tag k='object_type' v='AX_Flugverkehr' />
                            <tag k='art' v='5511' />
                            <tag k='nutzung' v='1000' />
                        </way>
                        <relation id='-1' changeset='-1'>
                          <member type='way' ref='-1' role='' />
                          <tag k='advStandardModell' v='Basis-DLM' />
                          <tag k='object_type' v='AA_modellart' />
                        </relation>
                      <relation id='-2' changeset='-1'>
                         <member type='node' ref='-7' role='' />
                         <tag k='advStandardModell' v='Basis-DLM' />
                         <tag k='object_type' v='AA_modellart' />
                      </relation>
                      <relation id='-3' changeset='-1'>
                        <member type='node' ref='-7' role='' />
                        <tag k='hoehe' v='10' />
                        <tag k='object_type' v='AX_objekthoehe' />
                      </relation>
                    </create>
                    <modify/>
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
    void createKontrollturmOhneFlugverkehr() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                      <node id="-7" lon="12.33031421086229" lat="49.88160285700639" version="0">
                        <tag k="object_type" v="AX_Turm"/>
                        <tag k="bauwerksfunktion" v="1004"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                      </node>
                      <relation id='-2' changeset='-1'>
                         <member type='node' ref='-7' role='' />
                         <tag k='advStandardModell' v='Basis-DLM' />
                         <tag k='object_type' v='AA_modellart' />
                      </relation>
                      <relation id='-3' changeset='-1'>
                        <member type='node' ref='-7' role='' />
                        <tag k='hoehe' v='10' />
                        <tag k='object_type' v='AX_objekthoehe' />
                      </relation>
                    </create>
                    <modify/>
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
                .contains("Ein 'AX_Turm' mit 'bauwerksfunktion' 1004 muss innerhalb von 'AX_Flugverkehr' liegen.");
    }
}