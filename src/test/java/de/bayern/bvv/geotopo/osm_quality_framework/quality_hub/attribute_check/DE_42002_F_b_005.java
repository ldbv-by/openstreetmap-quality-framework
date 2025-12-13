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
 * Es ist zu prüfen, dass alle Fahrgassen der Raststätten an Bundesautobahnen als Objekte 42003 AX_Strassenachse als Teil von
 * ZUSO 42002 AX_Strasse mit Widmung 1301 'Bundesautobahn' belegt ist und Bezeichnung nur mit 'A' beginnt.
 * Das Attribut Internationale Bedeutung ist nicht belegt.
 * Rückwärtige Betriebszufahrten von Raststätten müssen bei der Attributart Widmung den Wert 1307 oder 9997 haben. (?!)
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_42002_F_b_005 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("attribute-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.42002.F.b.005"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createBundesautobahnAufRaststaette() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.325389620907949" lat="49.87812864509372" version="0"/>
                        <node id="-2" lon="12.32713472680777" lat="49.87814135242938" version="0"/>
                        <node id="-3" lon="12.327193882799724" lat="49.87673720372163" version="0"/>
                        <node id="-4" lon="12.325488214478609" lat="49.87674355733058" version="0"/>
                        <node id="-7" lon="12.326198430249947" lat="49.8781345346002" version="0"/>
                        <node id="-8" lon="12.326286795568805" lat="49.87674058261873" version="0"/>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-7"/>
                            <nd ref="-2"/>
                            <nd ref="-3"/>
                            <nd ref="-8"/>
                            <nd ref="-4"/>
                            <nd ref="-1"/>
                            <tag k="funktion" v="5330"/>
                            <tag k="object_type" v="AX_Platz"/>
                        </way>
                        <way id="-2" version="0">
                            <nd ref="-7"/>
                            <nd ref="-8"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                            <tag k='breiteDerFahrbahn' v='9' />
                        </way>
                        <relation id='-77' changeset='-1'>
                            <member type='way' ref='-2' role='' />
                            <tag k='bezeichnung' v='Autobahn' />
                            <tag k='object_type' v='AX_Strasse' />
                            <tag k='widmung' v='1301' />
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
    void createEuropastrasseAufRaststaette() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.325389620907949" lat="49.87812864509372" version="0"/>
                        <node id="-2" lon="12.32713472680777" lat="49.87814135242938" version="0"/>
                        <node id="-3" lon="12.327193882799724" lat="49.87673720372163" version="0"/>
                        <node id="-4" lon="12.325488214478609" lat="49.87674355733058" version="0"/>
                        <node id="-7" lon="12.326198430249947" lat="49.8781345346002" version="0"/>
                        <node id="-8" lon="12.326286795568805" lat="49.87674058261873" version="0"/>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-7"/>
                            <nd ref="-2"/>
                            <nd ref="-3"/>
                            <nd ref="-8"/>
                            <nd ref="-4"/>
                            <nd ref="-1"/>
                            <tag k="funktion" v="5330"/>
                            <tag k="object_type" v="AX_Platz"/>
                        </way>
                        <way id="-2" version="0">
                            <nd ref="-7"/>
                            <nd ref="-8"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                            <tag k='breiteDerFahrbahn' v='9' />
                        </way>
                        <relation id='-77' changeset='-1'>
                            <member type='way' ref='-2' role='' />
                            <tag k='internationaleBedeutung' v='2001' />
                            <tag k='bezeichnung' v='Autobahn' />
                            <tag k='object_type' v='AX_Strasse' />
                            <tag k='widmung' v='1301' />
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
                .contains("Ein Strasse auf einer Raststätte muss Widmung 1301, keine internationaleBedeutung und die Bezeichnung mit A beginnen.");
   }
}