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
 * Ortslage' darf sich nicht gegenseitig überlagern.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_52001_F_b_001 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createAngrenzendeOrtslagen() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-8" lon="12.330094720996826" lat="49.88193386812974" version="0"/>
                        <node id="-9" lon="12.331042730415756" lat="49.881930858947925" version="0"/>
                        <node id="-10" lon="12.331052070410031" lat="49.88101906822168" version="0"/>
                        <node id="-11" lon="12.330085381002549" lat="49.88104314212553" version="0"/>
                        <node id="-18" lon="12.329216761534958" lat="49.881049160599645" version="0"/>
                        <node id="-19" lon="12.329258791509199" lat="49.88196095075758" version="0"/>
                        <way id="-2" version="0">
                            <nd ref="-8"/>
                            <nd ref="-9"/>
                            <nd ref="-10"/>
                            <nd ref="-11"/>
                            <nd ref="-8"/>
                            <tag k="object_type" v="AX_Ortslage"/>
                            <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                            <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                            <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                            <tag k='name' v='Test 1' />
                        </way>
                        <way id="-4" version="0">
                            <nd ref="-11"/>
                            <nd ref="-18"/>
                            <nd ref="-19"/>
                            <nd ref="-8"/>
                            <nd ref="-11"/>
                            <tag k="object_type" v="AX_Ortslage"/>
                            <tag k='identifikator:UUID' v='DEBYBDLM22222222' />
                            <tag k='identifikator:UUIDundZeit' v='DEBYBDLM2222222220251014T125300Z' />
                            <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                            <tag k='name' v='Test 2' />
                        </way>
                        <relation id='-1' changeset='-1'>
                            <member type='way' ref='-2' role='' />
                            <tag k='advStandardModell' v='Basis-DLM' />
                            <tag k='object_type' v='AA_modellart' />
                        </relation>
                        <relation id='-2' changeset='-1'>
                            <member type='way' ref='-4' role='' />
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
    void createUeberlappendeOrtslagen() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-8" lon="12.330094720996826" lat="49.88193386812974" version="0"/>
                        <node id="-11" lon="12.330085381002549" lat="49.88104314212553" version="0"/>
                        <node id="-18" lon="12.329216761534958" lat="49.881049160599645" version="0"/>
                        <node id="-19" lon="12.329258791509199" lat="49.88196095075758" version="0"/>
                        <node id="-23" lon="12.330790550570324" lat="49.88192484058373" version="0"/>
                        <node id="-25" lon="12.329786501185746" lat="49.881482488762515" version="0"/>
                        <node id="-27" lon="12.3308372505417" lat="49.881028095937026" version="0"/>
                        <way id="-4" version="0">
                            <nd ref="-11"/>
                            <nd ref="-18"/>
                            <nd ref="-19"/>
                            <nd ref="-8"/>
                            <nd ref="-11"/>
                            <tag k="object_type" v="AX_Ortslage"/>
                            <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                            <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                            <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                            <tag k='name' v='Test 1' />
                        </way>
                        <way id="-5" version="0">
                            <nd ref="-23"/>
                            <nd ref="-8"/>
                            <nd ref="-25"/>
                            <nd ref="-11"/>
                            <nd ref="-27"/>
                            <nd ref="-23"/>
                            <tag k="object_type" v="AX_Ortslage"/>
                            <tag k='identifikator:UUID' v='DEBYBDLM22222222' />
                            <tag k='identifikator:UUIDundZeit' v='DEBYBDLM2222222220251014T125300Z' />
                            <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                            <tag k='name' v='Test 2' />
                        </way>
                        <relation id='-4' changeset='-1'>
                            <member type='way' ref='-2' role='' />
                            <tag k='advStandardModell' v='Basis-DLM' />
                            <tag k='object_type' v='AA_modellart' />
                        </relation>
                        <relation id='-5' changeset='-1'>
                            <member type='way' ref='-4' role='' />
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
                .contains("'AX_Ortslage' darf sich nicht gegenseitig überlagern.");
    }
}
