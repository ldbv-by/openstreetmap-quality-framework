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
 * REOs gleicher Objekt-, Attribut- und Werteart dürfen sich nicht geometrieidentisch überlagern, es sei denn ein Objekt besitzt eine hDU Relation.
 * Folgende Attribute und Relationen (aus AA_Objekt) sind beim Vergleich zu ignorieren:
 * anlass, identifikator, lebenszeitintervall, modellart, zeigtAufExternes, quellobjektID, istTeilVon.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_02000_G_a_019 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.02000.G.a.019"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createUeberlagerndeWohnbauflaecheMitIstWeitererNutzung() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-61" lon="12.321564232224558" lat="49.876163909014714" version="0"/>
                    <node id="-62" lon="12.321547065693961" lat="49.875029981600335" version="0"/>
                    <node id="-71" lon="12.32028535483529" lat="49.87619156544826" version="0"/>
                    <node id="-72" lon="12.320242439491052" lat="49.8749857302768" version="0"/>
                    <node id="-76" lon="12.320663009995553" lat="49.87588181225545" version="0"/>
                    <node id="-77" lon="12.321143661720056" lat="49.875887343807825" version="0"/>
                    <node id="-78" lon="12.321083580500057" lat="49.875328677131925" version="0"/>
                    <node id="-79" lon="12.320508514494362" lat="49.87535080274713" version="0"/>
                    <way id="-15" version="0">
                        <nd ref="-61"/>
                        <nd ref="-71"/>
                        <nd ref="-72"/>
                        <nd ref="-62"/>
                        <nd ref="-61"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
                    </way>
                    <way id="-16" version="0">
                        <nd ref="-61"/>
                        <nd ref="-71"/>
                        <nd ref="-72"/>
                        <nd ref="-62"/>
                        <nd ref="-61"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
                        <tag k="funktion" v="1200"/>
                        <tag k="istWeitereNutzung" v="1000"/>
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
    void createUeberlagerndeWohnbauflaecheOhneIstWeitererNutzung() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-61" lon="12.321564232224558" lat="49.876163909014714" version="0"/>
                    <node id="-62" lon="12.321547065693961" lat="49.875029981600335" version="0"/>
                    <node id="-71" lon="12.32028535483529" lat="49.87619156544826" version="0"/>
                    <node id="-72" lon="12.320242439491052" lat="49.8749857302768" version="0"/>
                    <node id="-76" lon="12.320663009995553" lat="49.87588181225545" version="0"/>
                    <node id="-77" lon="12.321143661720056" lat="49.875887343807825" version="0"/>
                    <node id="-78" lon="12.321083580500057" lat="49.875328677131925" version="0"/>
                    <node id="-79" lon="12.320508514494362" lat="49.87535080274713" version="0"/>
                    <way id="-15" version="0">
                        <nd ref="-61"/>
                        <nd ref="-71"/>
                        <nd ref="-72"/>
                        <nd ref="-62"/>
                        <nd ref="-61"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
                    </way>
                    <way id="-16" version="0">
                        <nd ref="-61"/>
                        <nd ref="-71"/>
                        <nd ref="-72"/>
                        <nd ref="-62"/>
                        <nd ref="-61"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
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
                .contains("Doppeltes REO kann nur in Zusammenhang mit hDU-Relation vorkommen.");
    }
}