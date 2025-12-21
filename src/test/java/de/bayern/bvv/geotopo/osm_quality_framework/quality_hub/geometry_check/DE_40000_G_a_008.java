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
 * An ein TN_Liniensegment der Objektart 42003, 42005, 42008, 42014 und 44004 grenzt links und rechts geometrieidentisch ein Segment eines TN_Flächenobjektes aus dem Objektartenbereich 40000.
 * Ausnahmen:
 * 1. Liegt das TN_Liniensegment geometrisch identisch auf einem Liniensegment eines REO AX_Gebietsgrenze, AGZ 7102 'Grenze des Bundeslandes' grenzt nur ein Segment eines TN_Flächenobjektes aus dem Objektartenbereich 40000 links oder rechts an das TN_Liniensegment.
 * 2. Das REO, zu dem das TN_Liniensegment gehört, besitzt eine hatDirektUnten-Relation.
 * 3. Das REO, zu dem das TN_Liniensegment gehört, beginnt oder endet an einem REO, welches eine hatDirektUnten-Relation besitzt.
 * 4. Das REO, zu dem das TN_Liniensegment gehört, ist nur einseitig im Netz der o. a. Objektarten (42003, 42005, 42008, 42014 und 44004) angebunden.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_40000_G_a_008 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.40000.G.a.008"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createBahnstreckeNebenWohnbauflaechen() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-61" lon="12.321564232224558" lat="49.876163909014714" version="0"/>
                    <node id="-62" lon="12.321547065693961" lat="49.875029981600335" version="0"/>
                    <node id="-66" lon="12.323152099961376" lat="49.87614178387756" version="0"/>
                    <node id="-67" lon="12.323117767555017" lat="49.875002324501494" version="0"/>
                    <node id="-71" lon="12.32028535483529" lat="49.87619156544826" version="0"/>
                    <node id="-72" lon="12.320242439491052" lat="49.8749857302768" version="0"/>
                    <way id="-12" version="0">
                        <nd ref="-61"/>
                        <nd ref="-62"/>
                        <tag k="object_type" v="AX_Bahnstrecke"/>
                    </way>
                    <way id="-14" version="0">
                        <nd ref="-61"/>
                        <nd ref="-66"/>
                        <nd ref="-67"/>
                        <nd ref="-62"/>
                        <nd ref="-61"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
                    </way>
                    <way id="-15" version="0">
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
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result to be valid, but it was invalid.").isTrue();
    }

    @Test
    void createBahnstreckeNebenEinerWohnbauflaeche() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-61" lon="12.321564232224558" lat="49.876163909014714" version="0"/>
                    <node id="-62" lon="12.321547065693961" lat="49.875029981600335" version="0"/>
                    <node id="-66" lon="12.323152099961376" lat="49.87614178387756" version="0"/>
                    <node id="-67" lon="12.323117767555017" lat="49.875002324501494" version="0"/>
                    <node id="-71" lon="12.32028535483529" lat="49.87619156544826" version="0"/>
                    <node id="-72" lon="12.320242439491052" lat="49.8749857302768" version="0"/>
                    <way id="-12" version="0">
                        <nd ref="-61"/>
                        <nd ref="-62"/>
                        <tag k="object_type" v="AX_Bahnstrecke"/>
                    </way>
                    <way id="-15" version="0">
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
                .contains("'AX_Strassenachse', 'AX_Fahrbahnachse', 'AX_Fahrwegachse', 'AX_Bahnstrecke', 'AX_Gewaesserachse' muss links und rechts jeweils eine eigenständige TN-Fläche haben.");
    }

    @Test
    void createBahnstreckeAufGebietsgrenzeNebenEinerWohnbauflaeche() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-61" lon="12.321564232224558" lat="49.876163909014714" version="0"/>
                    <node id="-62" lon="12.321547065693961" lat="49.875029981600335" version="0"/>
                    <node id="-66" lon="12.323152099961376" lat="49.87614178387756" version="0"/>
                    <node id="-67" lon="12.323117767555017" lat="49.875002324501494" version="0"/>
                    <node id="-71" lon="12.32028535483529" lat="49.87619156544826" version="0"/>
                    <node id="-72" lon="12.320242439491052" lat="49.8749857302768" version="0"/>
                    <way id="-12" version="0">
                        <nd ref="-61"/>
                        <nd ref="-62"/>
                        <tag k="object_type" v="AX_Bahnstrecke"/>
                    </way>
                    <way id="-13" version="0">
                        <nd ref="-61"/>
                        <nd ref="-62"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="artDerGebietsgrenze" v="7102"/>
                    </way>
                    <way id="-15" version="0">
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
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result to be valid, but it was invalid.").isTrue();
    }
}