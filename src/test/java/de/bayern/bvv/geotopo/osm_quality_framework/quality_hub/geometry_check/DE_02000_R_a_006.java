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
 * Für jedes flächenförmige AA_REO A, bestimme alle AA_REO B die auf A per Relation hatDirektUnten verweisen. Prüfe, ob eines der REOs B über REO A hinausragt. Ist das der Fall ist REO A als fehlerhaft anzusehen.
 * Ausnahmen:
 * Eine geometrische Relation zwischen sich kreuzenden Bauwerken (BIV über BIV, BIG über BIG, sowie BIV über BIG und umgekehrt) und deren jeweiligen geometrieidentischen REO wird nicht geprüft, da diese in der Realität im Normalfall nicht vorkommen.
 * Bauwerke im Verkehrsbereich und Bauwerke im Gewässerbereich werden daher nicht als AA_REO B eingesetzt und werden auch nicht als Fehler gemeldet.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_02000_R_a_006 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.02000.R.a.006"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createFlaechenhafteBrueckeMitStrassenverkehr() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-13" lon="12.316617923248286" lat="49.87458649439249" version="0"/>
                        <node id="-14" lon="12.317883925903317" lat="49.87459340872822" version="0"/>
                        <node id="-15" lon="12.317948298919674" lat="49.87232545351535" version="0"/>
                        <node id="-16" lon="12.316639380920408" lat="49.87233236817482" version="0"/>
                        <node id="-21" lon="12.317733823900914" lat="49.874592588938825" version="0"/>
                        <node id="-22" lon="12.317798193241154" lat="49.872326246483084" version="0"/>
                        <node id="-26" lon="12.317122233246671" lat="49.87458924870646" version="0"/>
                        <node id="-27" lon="12.31718660273495" lat="49.87232947735021" version="0"/>
                        <way id="-6" version="0">
                            <nd ref="-13"/>
                            <nd ref="-26"/>
                            <nd ref="-21"/>
                            <nd ref="-14"/>
                            <nd ref="-15"/>
                            <nd ref="-22"/>
                            <nd ref="-27"/>
                            <nd ref="-16"/>
                            <nd ref="-13"/>
                            <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                            <tag k="bauwerksfunktion" v="1800"/>
                        </way>
                        <way id="-7" version="0">
                            <nd ref="-13"/>
                            <nd ref="-16"/>
                            <tag k="object_type" v="AX_Fahrbahnachse"/>
                        </way>
                        <way id="-8" version="0">
                            <nd ref="-21"/>
                            <nd ref="-22"/>
                            <tag k="object_type" v="AX_Fahrbahnachse"/>
                        </way>
                        <way id="-10" version="0">
                            <nd ref="-26"/>
                            <nd ref="-27"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-11" version="0">
                            <nd ref="-13"/>
                            <nd ref="-26"/>
                            <nd ref="-27"/>
                            <nd ref="-16"/>
                            <nd ref="-13"/>
                            <tag k="object_type" v="AX_Strassenverkehr"/>
                            <tag k="funktion" v="1000"/>
                        </way>
                        <way id="-12" version="0">
                            <nd ref="-21"/>
                            <nd ref="-26"/>
                            <nd ref="-27"/>
                            <nd ref="-22"/>
                            <nd ref="-21"/>
                            <tag k="funktion" v="1000"/>
                            <tag k="object_type" v="AX_Strassenverkehr"/>
                        </way>
                        <relation id="-4" version="0">
                            <member type="way" role="over" ref="-7"/>
                            <member type="way" role="under" ref="-6"/>
                            <member type="way" role="over" ref="-11"/>
                            <member type="way" role="over" ref="-8"/>
                            <member type="way" role="over" ref="-12"/>
                            <member type="way" role="over" ref="-10"/>
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
    void createFlaechenhafteBrueckeMitAusserhalbliegenderFahrbahnachse() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-13" lon="12.316617923248286" lat="49.87458649439249" version="0"/>
                        <node id="-14" lon="12.317883925903317" lat="49.87459340872822" version="0"/>
                        <node id="-15" lon="12.317948298919674" lat="49.87232545351535" version="0"/>
                        <node id="-16" lon="12.316639380920408" lat="49.87233236817482" version="0"/>
                        <node id="-26" lon="12.317122233246671" lat="49.87458924870646" version="0"/>
                        <node id="-27" lon="12.31718660273495" lat="49.87232947735021" version="0"/>
                        <node id="-44" lon="12.317716017645338" lat="49.87459249168906" version="0"/>
                        <node id="-45" lon="12.317805387455577" lat="49.872213555085004" version="0"/>
                        <node id="-49" lon="12.31780102891136" lat="49.872329577971065" version="0"/>
                        <way id="-6" version="0">
                            <nd ref="-13"/>
                            <nd ref="-26"/>
                            <nd ref="-44"/>
                            <nd ref="-14"/>
                            <nd ref="-15"/>
                            <nd ref="-27"/>
                            <nd ref="-16"/>
                            <nd ref="-13"/>
                            <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                            <tag k="bauwerksfunktion" v="1800"/>
                        </way>
                        <way id="-7" version="0">
                            <nd ref="-13"/>
                            <nd ref="-16"/>
                            <tag k="object_type" v="AX_Fahrbahnachse"/>
                        </way>
                        <way id="-10" version="0">
                            <nd ref="-26"/>
                            <nd ref="-27"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-11" version="0">
                            <nd ref="-13"/>
                            <nd ref="-26"/>
                            <nd ref="-27"/>
                            <nd ref="-16"/>
                            <nd ref="-13"/>
                            <tag k="object_type" v="AX_Strassenverkehr"/>
                            <tag k="funktion" v="1000"/>
                        </way>
                        <way id="-14" version="0">
                            <nd ref="-44"/>
                            <nd ref="-49"/>
                            <nd ref="-45"/>
                            <tag k="object_type" v="AX_Fahrbahnachse"/>
                        </way>
                        <way id="-15" version="0">
                            <nd ref="-44"/>
                            <nd ref="-26"/>
                            <nd ref="-27"/>
                            <nd ref="-49"/>
                            <nd ref="-44"/>
                            <tag k="bauwerksfunktion" v="1800"/>
                            <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                        </way>
                        <relation id="-4" version="0">
                            <member type="way" role="over" ref="-7"/>
                            <member type="way" role="under" ref="-6"/>
                            <member type="way" role="" ref="-11"/>
                            <member type="way" role="over" ref="-10"/>
                            <member type="way" role="over" ref="-14"/>
                            <member type="way" role="over" ref="-15"/>
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
                .contains("Objekt wird per hatDirektUnten referenziert, welches jedoch ganz oder teilweise außerhalb des Objekts liegt.");
    }

    @Test
    void createFlaechenhafteBrueckeMitAusserhalbliegendenStrassenverkehr() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-13" lon="12.316617923248286" lat="49.87458649439249" version="0"/>
                        <node id="-14" lon="12.317883925903317" lat="49.87459340872822" version="0"/>
                        <node id="-15" lon="12.317948298919674" lat="49.87232545351535" version="0"/>
                        <node id="-16" lon="12.316639380920408" lat="49.87233236817482" version="0"/>
                        <node id="-21" lon="12.317733823900914" lat="49.874592588938825" version="0"/>
                        <node id="-22" lon="12.317798193241154" lat="49.872326246483084" version="0"/>
                        <node id="-26" lon="12.317122233246671" lat="49.87458924870646" version="0"/>
                        <node id="-27" lon="12.31718660273495" lat="49.87232947735021" version="0"/>
                        <node id="-40" lon="12.318227248657223" lat="49.87216641607438" version="0"/>
                        <way id="-6" version="0">
                            <nd ref="-13"/>
                            <nd ref="-26"/>
                            <nd ref="-21"/>
                            <nd ref="-14"/>
                            <nd ref="-15"/>
                            <nd ref="-22"/>
                            <nd ref="-27"/>
                            <nd ref="-16"/>
                            <nd ref="-13"/>
                            <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                            <tag k="bauwerksfunktion" v="1800"/>
                        </way>
                        <way id="-7" version="0">
                            <nd ref="-13"/>
                            <nd ref="-16"/>
                            <tag k="object_type" v="AX_Fahrbahnachse"/>
                        </way>
                        <way id="-8" version="0">
                            <nd ref="-21"/>
                            <nd ref="-22"/>
                            <tag k="object_type" v="AX_Fahrbahnachse"/>
                        </way>
                        <way id="-10" version="0">
                            <nd ref="-26"/>
                            <nd ref="-27"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-11" version="0">
                            <nd ref="-13"/>
                            <nd ref="-26"/>
                            <nd ref="-27"/>
                            <nd ref="-16"/>
                            <nd ref="-13"/>
                            <tag k="object_type" v="AX_Strassenverkehr"/>
                            <tag k="funktion" v="1000"/>
                        </way>
                        <way id="-13" version="0">
                            <nd ref="-26"/>
                            <nd ref="-21"/>
                            <nd ref="-40"/>
                            <nd ref="-27"/>
                            <nd ref="-26"/>
                            <tag k="funktion" v="1000"/>
                            <tag k="object_type" v="AX_Strassenverkehr"/>
                        </way>
                        <relation id="-4" version="0">
                            <member type="way" role="over" ref="-7"/>
                            <member type="way" role="under" ref="-6"/>
                            <member type="way" role="over" ref="-11"/>
                            <member type="way" role="over" ref="-8"/>
                            <member type="way" role="over" ref="-10"/>
                            <member type="way" role="over" ref="-13"/>
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
                .contains("Objekt wird per hatDirektUnten referenziert, welches jedoch ganz oder teilweise außerhalb des Objekts liegt.");
    }
}