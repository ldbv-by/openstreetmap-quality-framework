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
 * Für jedes linienförmige AA_REO A, bestimme alle linienförmigen AA_REO B die auf A per Relation hatDirektUnten verweisen.
 * Sind keine solchen REOs B vorhanden ist REO A nicht weiter zu prüfen. Ansonsten bilde die Vereinigung der Geometrien der REOs B und prüfe,
 * dass sie geometrisch identisch (equals) zur Geometrie des REO A ist. Ist das nicht der Fall ist REO A als fehlerhaft anzusehen.
 * <p>
 * Ausnahmen:
 * Eine geometrische Identität zwischen sich kreuzenden Bauwerken (BIV über BIV, BIG über BIG, sowie BIV über BIG und umgekehrt) und deren jeweiligen geometrieidentischen REO wird nicht geprüft, da diese in der Realität im Normalfall nicht vorkommen.
 * Bauwerke im Verkehrsbereich und Bauwerke im Gewässerbereich werden daher nicht als AA_REO B eingesetzt und werden auch nicht als Fehler gemeldet.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_02000_R_a_005 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.02000.R.a.005"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createIdentischeStrassenachseAufBruecke() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.317446189523055" lat="49.87283851843256" version="0"/>
                        <node id="-2" lon="12.318295913469939" lat="49.872866176770636" version="0"/>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-2" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                            <tag k="bauwerksfunktion" v="1800"/>
                        </way>
                        <relation id="-1" version="0">
                            <member type="way" role="under" ref="-2"/>
                            <member type="way" role="over" ref="-1"/>
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
    void createIdentischeZusammengesetzteStrassenachseAufBruecke() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.317446189523055" lat="49.87283851843256" version="0"/>
                        <node id="-2" lon="12.318295913469939" lat="49.872866176770636" version="0"/>
                        <node id="-3" lon="12.31821562672491" lat="49.87220937301096" version="0"/>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-2" version="0">
                            <nd ref="-2"/>
                            <nd ref="-3"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-3" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <nd ref="-3"/>
                            <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                            <tag k="bauwerksfunktion" v="1800"/>
                        </way>
                        <relation id="-1" version="0">
                            <member type="way" role="under" ref="-3"/>
                            <member type="way" role="over" ref="-1"/>
                            <member type="way" role="over" ref="-2"/>
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
    void createNichtIdentischeStrassenachseAufBruecke() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.317446189523055" lat="49.87283851843256" version="0"/>
                        <node id="-2" lon="12.318295913469939" lat="49.872866176770636" version="0"/>
                        <node id="-3" lon="12.31821562672491" lat="49.87220937301096" version="0"/>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-2" version="0">
                            <nd ref="-1"/>
                            <nd ref="-2"/>
                            <nd ref="-3"/>
                            <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                            <tag k="bauwerksfunktion" v="1800"/>
                        </way>
                        <relation id="-1" version="0">
                            <member type="way" role="under" ref="-2"/>
                            <member type="way" role="over" ref="-1"/>
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
                .contains("HDU hat keine Geometrieidentität.");
    }

    @Test
    void createIdentischeZusammengesetzteStrassenachseMitZweiUebereinanderliegendenBruecken() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-1" lon="12.317604976165768" lat="49.874351406388456" version="0"/>
                        <node id="-2" lon="12.317647891510006" lat="49.87261586836055" version="0"/>
                        <node id="-4" lon="12.317617120655887" lat="49.87386027778987" version="0"/>
                        <node id="-8" lon="12.315695243347164" lat="49.87354241833531" version="0"/>
                        <node id="-9" lon="12.319418149459837" lat="49.873507845894345" version="0"/>
                        <way id="-1" version="0">
                            <nd ref="-1"/>
                            <nd ref="-4"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                            <tag k="bauwerksfunktion" v="1800"/>
                        </way>
                        <way id="-2" version="0">
                            <nd ref="-1"/>
                            <nd ref="-4"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-3" version="0">
                            <nd ref="-4"/>
                            <nd ref="-2"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-4" version="0">
                            <nd ref="-8"/>
                            <nd ref="-9"/>
                            <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                            <tag k="bauwerksfunktion" v="1800"/>
                        </way>
                        <way id="-5" version="0">
                            <nd ref="-8"/>
                            <nd ref="-9"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <relation id="-1" version="0">
                            <member type="way" role="over" ref="-3"/>
                            <member type="way" role="over" ref="-2"/>
                            <member type="way" role="under" ref="-1"/>
                            <tag k="object_type" v="AA_hatDirektUnten"/>
                        </relation>
                        <relation id="-2" version="0">
                            <member type="way" role="over" ref="-4"/>
                            <member type="way" role="under" ref="-3"/>
                            <member type="way" role="under" ref="-2"/>
                            <tag k="object_type" v="AA_hatDirektUnten"/>
                        </relation>
                        <relation id="-3" version="0">
                            <member type="way" role="over" ref="-5"/>
                            <member type="way" role="under" ref="-4"/>
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
}