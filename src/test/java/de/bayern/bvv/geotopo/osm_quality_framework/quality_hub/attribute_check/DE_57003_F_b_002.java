package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.attribute_check;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.config.JtsJackson3Module;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.dto.QualityHubResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceErrorDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AdV-Beschreibung:
 * Innerhalb der Längenausdehnung der zu einem ZUSO 44002 'Wasserlauf' gehörenden REO 44004 'Gewässerachse' und 44001 'Fließgewässer'
 * ist 'Fließrichtung' an allen Gewässerachsen und Gewässerstationierungsachsen (=linienförmiger Repräsentant von Fließgewässer)
 * mit AGA 2000 identisch, die Koordinatenfolge ist bei 'Fließrichtung' "1" (true) homogen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_57003_F_b_002 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("attribute-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.57003.F.b.002"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createGewaesserstationierungsachseMitIdentischerFliessrichtung() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                    <osmChange version="0.6" generator="iD">
                        <create>
                            <node id="-1" lon="12.32392987547029" lat="49.87735489350956" version="0"/>
                            <node id="-2" lon="12.323939215464565" lat="49.87653029248019" version="0"/>
                            <node id="-3" lon="12.325368234588666" lat="49.87651223536578" version="0"/>
                            <node id="-4" lon="12.325340214605841" lat="49.87734285563961" version="0"/>
                            <node id="-7" lon="12.32660111383299" lat="49.87735489350956" version="0"/>
                            <node id="-8" lon="12.326647813804366" lat="49.87644602588854" version="0"/>
                            <node id="-12" lon="12.32393437528746" lat="49.87695761944921" version="0"/>
                            <node id="-13" lon="12.326624334926267" lat="49.876902970321524" version="0"/>
                            <way id="-1" version="0">
                                <nd ref="-1"/>
                                <nd ref="-12"/>
                                <nd ref="-2"/>
                                <nd ref="-3"/>
                                <nd ref="-4"/>
                                <nd ref="-1"/>
                                <tag k="object_type" v="AX_Gewaesserachse"/>
                                <tag k='breiteDesGewaessers' v='12' />
                                <tag k="fliessrichtung" v="TRUE"/>
                            </way>
                            <way id="-2" version="0">
                                <nd ref="-4"/>
                                <nd ref="-7"/>
                                <nd ref="-13"/>
                                <nd ref="-8"/>
                                <nd ref="-3"/>
                                <nd ref="-4"/>
                                <tag k="object_type" v="AX_Gewaesserachse"/>
                                <tag k='breiteDesGewaessers' v='12' />
                                <tag k="fliessrichtung" v="TRUE"/>
                            </way>
                            <way id="-3" version="0">
                                <nd ref="-12"/>
                                <nd ref="-13"/>
                                <tag k="object_type" v="AX_Gewaesserstationierungsachse"/>
                                <tag k="artDerGewaesserstationierungsachse" v="2000"/>
                                <tag k="fliessrichtung" v="TRUE"/>
                            </way>
                        </create>
                        <modify/>
                        <delete if-unused="true"/>
                    </osmChange>
                """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML)
                                .param("steps", String.join(",", stepsToValidate))
                                .param("rules", String.join(",", rulesToValidate)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        QualityHubResultDto qualityHubResultDto = this.objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), QualityHubResultDto.class);

        // Assert
        assertThat(qualityHubResultDto).as("Quality-Hub result must not be null").isNotNull();
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result to be valid, but it was invalid.").isTrue();
    }

    @Test
    void createGewaesserstationierungsachseMitUnterschiedlicherFliessrichtung() throws Exception {
        // Arrange
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
                        <tag k="artDerGewaesserstationierungsachse" v="2000"/>
                        <tag k="fliessrichtung" v="TRUE"/>
                    </way>
                    <way id="-4" version="0">
                        <nd ref="-11"/>
                        <nd ref="-12"/>
                        <nd ref="-13"/>
                        <nd ref="-14"/>
                        <nd ref="-11"/>
                        <tag k="object_type" v="AX_Gewaesserachse"/>
                        <tag k='breiteDesGewaessers' v='12' />
                        <tag k="fliessrichtung" v="FALSE"/>
                    </way>
                    <way id="-5" version="0">
                        <nd ref="-17"/>
                        <nd ref="-12"/>
                        <nd ref="-13"/>
                        <nd ref="-20"/>
                        <nd ref="-17"/>
                        <tag k="object_type" v="AX_Gewaesserachse"/>
                        <tag k='breiteDesGewaessers' v='12' />
                        <tag k="fliessrichtung" v="TRUE"/>
                    </way>
                </create>
                <modify/>
                <delete if-unused="true"/>
            </osmChange>
            """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML)
                                .param("steps", String.join(",", stepsToValidate))
                                .param("rules", String.join(",", rulesToValidate)))
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
                .contains("Die Gewässerstationierungsachse hat die falsche Fließrichtung.");
    }
}