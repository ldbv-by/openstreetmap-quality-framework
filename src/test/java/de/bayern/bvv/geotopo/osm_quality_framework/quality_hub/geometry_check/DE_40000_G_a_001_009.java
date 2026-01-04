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
 * Lückenlose und Überschneidungsfreie Flächendeckung der Tatsächlichen Nutzung der Grundfläche
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_40000_G_a_001_009 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.40000.G.a.001_009"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createWohnbauflaechenMitBundeslandgrenze() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.320182357943636" lat="49.875184860596924" version="0"/>
                    <node id="-2" lon="12.320182357943636" lat="49.87400665839374" version="0"/>
                    <node id="-4" lon="12.32224229479449" lat="49.87399283367411" version="0"/>
                    <node id="-6" lon="12.322207962388132" lat="49.875171036003486" version="0"/>
                    <node id="-17" lon="12.320843254310388" lat="49.874764472628776" version="0"/>
                    <node id="-18" lon="12.321555649286676" lat="49.874764472628776" version="0"/>
                    <node id="-19" lon="12.321564232224558" lat="49.87433854916628" version="0"/>
                    <node id="-20" lon="12.32081750484191" lat="49.874355143824374" version="0"/>
                    <node id="-23" lon="12.321203719283238" lat="49.87517788988539" version="0"/>
                    <node id="-24" lon="12.321203742940055" lat="49.874764472628776" version="0"/>
                    <node id="-27" lon="12.321187616823943" lat="49.87434691875936" version="0"/>
                    <node id="-28" lon="12.321203274717165" lat="49.873999806782166" version="0"/>
                    <way id="-1" version="0">
                        <nd ref="-1"/>
                        <nd ref="-2"/>
                        <tag k="artDerGebietsgrenze" v="7102"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                    </way>
                    <way id="-2" version="0">
                        <nd ref="-4"/>
                        <nd ref="-28"/>
                        <nd ref="-2"/>
                        <tag k="artDerGebietsgrenze" v="7102"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                    </way>
                    <way id="-3" version="0">
                        <nd ref="-6"/>
                        <nd ref="-4"/>
                        <tag k="artDerGebietsgrenze" v="7102"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                    </way>
                    <way id="-4" version="0">
                        <nd ref="-1"/>
                        <nd ref="-23"/>
                        <nd ref="-6"/>
                        <tag k="artDerGebietsgrenze" v="7102"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                    </way>
                    <way id="-6" version="0">
                        <nd ref="-17"/>
                        <nd ref="-24"/>
                        <nd ref="-18"/>
                        <nd ref="-19"/>
                        <nd ref="-27"/>
                        <nd ref="-20"/>
                        <nd ref="-17"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
                    </way>
                    <way id="-7" version="0">
                        <nd ref="-23"/>
                        <nd ref="-24"/>
                        <nd ref="-18"/>
                        <nd ref="-19"/>
                        <nd ref="-27"/>
                        <nd ref="-28"/>
                        <nd ref="-4"/>
                        <nd ref="-6"/>
                        <nd ref="-23"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
                    </way>
                    <way id="-8" version="0">
                        <nd ref="-1"/>
                        <nd ref="-23"/>
                        <nd ref="-24"/>
                        <nd ref="-17"/>
                        <nd ref="-20"/>
                        <nd ref="-27"/>
                        <nd ref="-28"/>
                        <nd ref="-2"/>
                        <nd ref="-1"/>
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
    void createWohnbauflaecheOhneBundeslandgrenze() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-17" lon="12.320843254310388" lat="49.874764472628776" version="0"/>
                    <node id="-18" lon="12.321555649286676" lat="49.874764472628776" version="0"/>
                    <node id="-19" lon="12.321564232224558" lat="49.87433854916628" version="0"/>
                    <node id="-20" lon="12.32081750484191" lat="49.874355143824374" version="0"/>
                    <node id="-24" lon="12.321203742940055" lat="49.874764472628776" version="0"/>
                    <node id="-27" lon="12.321187616823943" lat="49.87434691875936" version="0"/>
                    <way id="-6" version="0">
                        <nd ref="-17"/>
                        <nd ref="-24"/>
                        <nd ref="-18"/>
                        <nd ref="-19"/>
                        <nd ref="-27"/>
                        <nd ref="-20"/>
                        <nd ref="-17"/>
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
                .contains("Im Bereich der Objekte \"Tatsächliche Nutzung\" existiert eine Lücke bzw. Überschneidung in der Flächendeckung.");
    }

    @Test
    void createWohnbauflaechenAufWohnbauflaecheMitBundeslandgrenze() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.320182357943636" lat="49.875184860596924" version="0"/>
                    <node id="-2" lon="12.320182357943636" lat="49.87400665839374" version="0"/>
                    <node id="-4" lon="12.32224229479449" lat="49.87399283367411" version="0"/>
                    <node id="-6" lon="12.322207962388132" lat="49.875171036003486" version="0"/>
                    <node id="-17" lon="12.320843254310388" lat="49.874764472628776" version="0"/>
                    <node id="-18" lon="12.321555649286676" lat="49.874764472628776" version="0"/>
                    <node id="-19" lon="12.321564232224558" lat="49.87433854916628" version="0"/>
                    <node id="-20" lon="12.32081750484191" lat="49.874355143824374" version="0"/>
                    <node id="-23" lon="12.321203719283238" lat="49.87517788988539" version="0"/>
                    <node id="-24" lon="12.321203742940055" lat="49.874764472628776" version="0"/>
                    <node id="-27" lon="12.321187616823943" lat="49.87434691875936" version="0"/>
                    <node id="-28" lon="12.321203274717165" lat="49.873999806782166" version="0"/>
                    <way id="-1" version="0">
                        <nd ref="-1"/>
                        <nd ref="-2"/>
                        <tag k="artDerGebietsgrenze" v="7102"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                    </way>
                    <way id="-2" version="0">
                        <nd ref="-4"/>
                        <nd ref="-28"/>
                        <nd ref="-2"/>
                        <tag k="artDerGebietsgrenze" v="7102"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                    </way>
                    <way id="-3" version="0">
                        <nd ref="-6"/>
                        <nd ref="-4"/>
                        <tag k="artDerGebietsgrenze" v="7102"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                    </way>
                    <way id="-4" version="0">
                        <nd ref="-1"/>
                        <nd ref="-23"/>
                        <nd ref="-6"/>
                        <tag k="artDerGebietsgrenze" v="7102"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                    </way>
                    <way id="-6" version="0">
                        <nd ref="-17"/>
                        <nd ref="-24"/>
                        <nd ref="-18"/>
                        <nd ref="-19"/>
                        <nd ref="-27"/>
                        <nd ref="-20"/>
                        <nd ref="-17"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
                    </way>
                    <way id="-7" version="0">
                        <nd ref="-23"/>
                        <nd ref="-24"/>
                        <nd ref="-18"/>
                        <nd ref="-19"/>
                        <nd ref="-27"/>
                        <nd ref="-28"/>
                        <nd ref="-4"/>
                        <nd ref="-6"/>
                        <nd ref="-23"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
                    </way>
                    <way id="-8" version="0">
                        <nd ref="-1"/>
                        <nd ref="-23"/>
                        <nd ref="-24"/>
                        <nd ref="-17"/>
                        <nd ref="-20"/>
                        <nd ref="-27"/>
                        <nd ref="-28"/>
                        <nd ref="-2"/>
                        <nd ref="-1"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
                    </way>
                    <way id="-9" version="0">
                        <nd ref="-1"/>
                        <nd ref="-23"/>
                        <nd ref="-24"/>
                        <nd ref="-17"/>
                        <nd ref="-20"/>
                        <nd ref="-27"/>
                        <nd ref="-28"/>
                        <nd ref="-2"/>
                        <nd ref="-1"/>
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
                .contains("Im Bereich der Objekte \"Tatsächliche Nutzung\" existiert eine Lücke bzw. Überschneidung in der Flächendeckung.");
    }

    @Test
    void deleteTatsaechlicheNutzungsFlaeche() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                    <delete>
                      <relation id='13701' version='1' changeset='-1'/>
                      <way id='11449' version='1' changeset='-1'/>
                    </delete>
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
                .contains("Im Bereich der Objekte \"Tatsächliche Nutzung\" existiert eine Lücke bzw. Überschneidung in der Flächendeckung.");
    }

    @Test
    void createWohnbauflaecheAufBestehenderFlaecherGemischterNutzung() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                	<create>
                		<node id="-15" lon="12.339650347214857" lat="49.87671787635957" version="0"/>
                		<node id="-16" lon="12.340035292377584" lat="49.87671787635957" version="0"/>
                		<node id="-17" lon="12.340042840209037" lat="49.87647953212283" version="0"/>
                		<node id="-18" lon="12.339676764912875" lat="49.87648682837445" version="0"/>
                		<way id="-3" version="0">
                			<nd ref="-15"/>
                			<nd ref="-16"/>
                			<nd ref="-17"/>
                			<nd ref="-18"/>
                			<nd ref="-15"/>
                			<tag k="object_type" v="AX_Wohnbauflaeche"/>
                			<tag k="artDerBebauung" v="1000"/>
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
                .contains("Im Bereich der Objekte \"Tatsächliche Nutzung\" existiert eine Lücke bzw. Überschneidung in der Flächendeckung.");
    }
}
