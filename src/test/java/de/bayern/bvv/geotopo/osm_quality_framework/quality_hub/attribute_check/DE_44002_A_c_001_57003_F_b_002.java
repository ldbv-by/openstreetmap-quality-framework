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
 * Die zu einem ZUSO 44002 "Wasserlauf" gehörenden REO 44004 'Gewässerachse' führen bei der
 * Attributart "Fließrichtung immer den gleichen Wert, entweder "TRUE" oder "FALSE"
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_44002_A_c_001_57003_F_b_002 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createWasserlaufMitGleicherFliessrichtung() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.33103749863201" lat="49.87911888618271" version="0"/>
                    <node id="-2" lon="12.331030993733307" lat="49.878754209010715" version="0"/>
                    <node id="-4" lon="12.331021236757456" lat="49.87835809096834" version="0"/>
                    <node id="-7" lon="12.33103432094183" lat="49.87894073908409" version="0"/>
                    <node id="-9" lon="12.331024850360901" lat="49.87850479802609" version="0"/>
                    <way id="-1" version="0">
                        <nd ref="-1"/>
                        <nd ref="-7"/>
                        <nd ref="-2"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM00000000"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM0000000020251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="object_type" v="AX_Gewaesserachse"/>
                        <tag k='breiteDesGewaessers' v='12' />
                        <tag k='fliessrichtung' v='TRUE' />
                    </way>
                    <way id="-2" version="0">
                        <nd ref="-4"/>
                        <nd ref="-9"/>
                        <nd ref="-2"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM11111111"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM1111111120251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="object_type" v="AX_Gewaesserachse"/>
                        <tag k='breiteDesGewaessers' v='12' />
                        <tag k='fliessrichtung' v='TRUE' />
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
                    <relation id='-3' changeset='-1'>
                        <member type='way' ref='-1' role='' />
                        <member type='way' ref='-2' role='' />
                        <tag k="identifikator:UUID" v="DEBYBDLM22222222"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM2222222220251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="object_type" v="AX_Wasserlauf"/>
                    </relation>
                    <relation id='-4' changeset='-1'>
                        <member type='relation' ref='-3' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                </create>
                <modify/>
                <delete if-unused="true"/>
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
    void createWasserlaufMitUnterschiedlicherFliessrichtung() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.33103749863201" lat="49.87911888618271" version="0"/>
                    <node id="-2" lon="12.331030993733307" lat="49.878754209010715" version="0"/>
                    <node id="-4" lon="12.331021236757456" lat="49.87835809096834" version="0"/>
                    <node id="-7" lon="12.33103432094183" lat="49.87894073908409" version="0"/>
                    <node id="-9" lon="12.331024850360901" lat="49.87850479802609" version="0"/>
                    <way id="-1" version="0">
                        <nd ref="-1"/>
                        <nd ref="-7"/>
                        <nd ref="-2"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM00000000"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM0000000020251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="object_type" v="AX_Gewaesserachse"/>
                        <tag k='breiteDesGewaessers' v='12' />
                        <tag k='fliessrichtung' v='TRUE' />
                    </way>
                    <way id="-2" version="0">
                        <nd ref="-4"/>
                        <nd ref="-9"/>
                        <nd ref="-2"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM11111111"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM1111111120251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="object_type" v="AX_Gewaesserachse"/>
                        <tag k='breiteDesGewaessers' v='12' />
                        <tag k='fliessrichtung' v='FALSE' />
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
                    <relation id='-3' changeset='-1'>
                        <member type='way' ref='-1' role='' />
                        <member type='way' ref='-2' role='' />
                        <tag k="identifikator:UUID" v="DEBYBDLM22222222"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM2222222220251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="object_type" v="AX_Wasserlauf"/>
                    </relation>
                    <relation id='-4' changeset='-1'>
                        <member type='relation' ref='-3' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                </create>
                <modify/>
                <delete if-unused="true"/>
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
                .contains("Alle Objekte 'AX_Gewaesserachse' in einer Relation 'AX_Wasserlauf' müssen die gleiche 'fliessrichtung' haben.");
    }
}