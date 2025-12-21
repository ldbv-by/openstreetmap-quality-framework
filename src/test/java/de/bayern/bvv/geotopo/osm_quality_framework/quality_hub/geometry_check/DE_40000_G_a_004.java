package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.geometry_check;

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
 * Flächenförmige Objekte aus dem Objektartenbereich "Tatsächliche Nutzung" mit der Relation hatDirektUnten auf ein AX_BauwerkImVerkehrsbereich oder
 * auf ein AX_BauwerkImGewaesserbereich müssen untereinander überschneidungsfrei sein.
 * Die Prüfung auf Überlagerung erfolgt pro Bauwerk, für die Menge der flächenförmigen TN-Objekte,
 * welche das Bauwerk per Relation hatDirektUnten direkt referenzieren.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_40000_G_a_004 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.40000.G.a.004"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createHatDirektUntenUeberschneidungsfrei() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.317446189523055" lat="49.87283851843256" version="0"/>
                        <node id="-2" lon="12.318295913469939" lat="49.872866176770636" version="0"/>
                        <node id="-3" lon="12.318338828814177" lat="49.87161600393924" version="0"/>
                        <node id="-4" lon="12.317497687805174" lat="49.871610472214115" version="0"/>
                        <node id="-8" lon="12.318319452614706" lat="49.87218045869448" version="0"/>
                        <node id="-9" lon="12.317474482245547" lat="49.8721638440477" version="0"/>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <nd ref="-8"/>
                            <nd ref="-3"/>
                            <nd ref="-4"/>
                            <nd ref="-9"/>
                            <nd ref="-1"/>
                            <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                            <tag k="bauwerksfunktion" v="1800"/>
                        </way>
                        <way id="-2" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <nd ref="-8"/>
                            <nd ref="-9"/>
                            <nd ref="-1"/>
                            <tag k="object_type" v="AX_Wohnbauflaeche"/>
                        </way>
                        <way id="-3" version="0">
                            <nd ref="-9"/>
                            <nd ref="-4"/>
                            <nd ref="-3"/>
                            <nd ref="-8"/>
                            <nd ref="-9"/>
                            <tag k="object_type" v="AX_Wald"/>
                        </way>
                        <relation id="-1" version="0">
                            <member type="way" role="under" ref="-1"/>
                            <member type="way" role="over" ref="-2"/>
                            <member type="way" role="over" ref="-3"/>
                            <tag k="object_type" v="AA_hatDirektUnten"/>
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
    void createHatDirektUntenNichtUeberschneidungsfrei() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.317446189523055" lat="49.87283851843256" version="0"/>
                        <node id="-2" lon="12.318295913469939" lat="49.872866176770636" version="0"/>
                        <node id="-3" lon="12.318338828814177" lat="49.87161600393924" version="0"/>
                        <node id="-4" lon="12.317497687805174" lat="49.871610472214115" version="0"/>
                        <node id="-8" lon="12.318319452614706" lat="49.87218045869448" version="0"/>
                        <node id="-9" lon="12.317474482245547" lat="49.8721638440477" version="0"/>
                        <node id="-23" lon="12.31830647425666" lat="49.8725585320333" version="0"/>
                        <node id="-24" lon="12.317458495128848" lat="49.87254507745128" version="0"/>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <nd ref="-23"/>
                            <nd ref="-8"/>
                            <nd ref="-3"/>
                            <nd ref="-4"/>
                            <nd ref="-9"/>
                            <nd ref="-24"/>
                            <nd ref="-1"/>
                            <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                            <tag k="bauwerksfunktion" v="1800"/>
                        </way>
                        <way id="-2" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <nd ref="-23"/>
                            <nd ref="-8"/>
                            <nd ref="-9"/>
                            <nd ref="-24"/>
                            <nd ref="-1"/>
                            <tag k="object_type" v="AX_Wohnbauflaeche"/>
                        </way>
                        <way id="-3" version="0">
                            <nd ref="-4"/>
                            <nd ref="-3"/>
                            <nd ref="-8"/>
                            <nd ref="-23"/>
                            <nd ref="-24"/>
                            <nd ref="-9"/>
                            <nd ref="-4"/>
                            <tag k="object_type" v="AX_Wald"/>
                        </way>
                        <relation id="-1" version="0">
                            <member type="way" role="under" ref="-1"/>
                            <member type="way" role="over" ref="-2"/>
                            <member type="way" role="over" ref="-3"/>
                            <tag k="object_type" v="AA_hatDirektUnten"/>
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
                .contains("Members auf einem Bauwerk im Verkehrsbereich oder Gewässerbereich müssen überschneidungsfrei sein.");
    }
}