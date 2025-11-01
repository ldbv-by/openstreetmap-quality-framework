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
 * Eine Gewässerstationierungsachse mit AGA 2000, die (vollständig)
 * in einem oder mehreren Fließgewässern mit FKT 8300 liegt, hat FLR 'FALSE'.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_57003_A_c_001 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createGewaesserstationierungsachseVollstaendigInZweiGewaesserOhneFliessrichtung() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.33103749863201" lat="49.87911888618271" version="0"/>
                    <node id="-2" lon="12.331030993733307" lat="49.878754209010715" version="0"/>
                    <node id="-7" lon="12.33103432094183" lat="49.87894073908409" version="0"/>
                    <node id="-11" lon="12.330705006139802" lat="49.87924665099593" version="0"/>
                    <node id="-12" lon="12.330697927747774" lat="49.87895700627898" version="0"/>
                    <node id="-13" lon="12.331405766950473" lat="49.87895016425679" version="0"/>
                    <node id="-14" lon="12.331412845342502" lat="49.87923752835419" version="0"/>
                    <node id="-17" lon="12.330683770963722" lat="49.87867876326103" version="0"/>
                    <node id="-20" lon="12.33139161016642" lat="49.87864911432034" version="0"/>
                    <way id="-1" version="0">
                        <nd ref="-1"/>
                        <nd ref="-7"/>
                        <nd ref="-2"/>
                        <tag k="object_type" v="AX_Gewaesserstationierungsachse"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM00000000"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM0000000020251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="artDerGewaesserstationierungsachse" v="2000"/>
                        <tag k="fliessrichtung" v="FALSE"/>
                    </way>
                    <way id="-4" version="0">
                        <nd ref="-11"/>
                        <nd ref="-12"/>
                        <nd ref="-13"/>
                        <nd ref="-14"/>
                        <nd ref="-11"/>
                        <tag k="object_type" v="AX_Fliessgewaesser"/>
                        <tag k="funktion" v="8300"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM22222222"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM2222222220251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                    </way>
                    <way id="-5" version="0">
                        <nd ref="-17"/>
                        <nd ref="-12"/>
                        <nd ref="-13"/>
                        <nd ref="-20"/>
                        <nd ref="-17"/>
                        <tag k="object_type" v="AX_Fliessgewaesser"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM11111111"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM1111111120251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="funktion" v="8300"/>
                    </way>
                    <relation id='-1' changeset='-1'>
                        <member type='way' ref='-1' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-2' changeset='-1'>
                        <member type='way' ref='-4' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-3' changeset='-1'>
                        <member type='way' ref='-5' role='' />
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
    void createGewaesserstationierungsachseVollstaendigInZweiGewaesserMitFliessrichtung() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.33103749863201" lat="49.87911888618271" version="0"/>
                    <node id="-2" lon="12.331030993733307" lat="49.878754209010715" version="0"/>
                    <node id="-7" lon="12.33103432094183" lat="49.87894073908409" version="0"/>
                    <node id="-11" lon="12.330705006139802" lat="49.87924665099593" version="0"/>
                    <node id="-12" lon="12.330697927747774" lat="49.87895700627898" version="0"/>
                    <node id="-13" lon="12.331405766950473" lat="49.87895016425679" version="0"/>
                    <node id="-14" lon="12.331412845342502" lat="49.87923752835419" version="0"/>
                    <node id="-17" lon="12.330683770963722" lat="49.87867876326103" version="0"/>
                    <node id="-20" lon="12.33139161016642" lat="49.87864911432034" version="0"/>
                    <way id="-1" version="0">
                        <nd ref="-1"/>
                        <nd ref="-7"/>
                        <nd ref="-2"/>
                        <tag k="object_type" v="AX_Gewaesserstationierungsachse"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM00000000"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM0000000020251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="artDerGewaesserstationierungsachse" v="2000"/>
                        <tag k="fliessrichtung" v="TRUE"/>
                    </way>
                    <way id="-4" version="0">
                        <nd ref="-11"/>
                        <nd ref="-12"/>
                        <nd ref="-13"/>
                        <nd ref="-14"/>
                        <nd ref="-11"/>
                        <tag k="object_type" v="AX_Fliessgewaesser"/>
                        <tag k="funktion" v="8300"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM22222222"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM2222222220251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                    </way>
                    <way id="-5" version="0">
                        <nd ref="-17"/>
                        <nd ref="-12"/>
                        <nd ref="-13"/>
                        <nd ref="-20"/>
                        <nd ref="-17"/>
                        <tag k="object_type" v="AX_Fliessgewaesser"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM11111111"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM1111111120251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="funktion" v="8300"/>
                    </way>
                    <relation id='-1' changeset='-1'>
                        <member type='way' ref='-1' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-2' changeset='-1'>
                        <member type='way' ref='-4' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-3' changeset='-1'>
                        <member type='way' ref='-5' role='' />
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
                .contains("Eine Gewässerstationierungsachse mit AGA 2000, die (vollständig) in einem oder mehreren Fließgewässern mit FKT 8300 liegt, hat FLR 'FALSE'");
    }
}