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
 * Untersuche die komplett auf der Landesgrenze (AX_Gebietsgrenze mit artDerGebietsgrenze = 7102) verlaufenden linienförmigen REOs.
 * Prüfe dass das ZUSO, das von solch einem REO R per istTeilVon referenziert wird, dieselbe Länderkennung im Objektidentifikator wie R führt.
 * Prüfe außerdem, dass das ZUSO nicht von anderen linienförmigen REOs referenziert wird, die ganz oder teilweise außerhalb einer Landesgrenze liegen.
 * Hinweis: Bei diesen Landesgrenzen kann es sich um die Grenze handeln, auf der das REO R verläuft, aber auch um andere Landesgrenzen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_75009_G_b_008 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.75009.G.b.008"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createLandesgrenzeMitStrassenachseUndStrasseImSelbenBundesland() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.327319499952107" lat="49.878070268874175" version="0"/>
                    <node id="-2" lon="12.330740984227374" lat="49.87807055563436" version="0"/>
                    <way id="-1" version="0">
                        <nd ref="-1"/>
                        <nd ref="-2"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7102"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM12345678"/>
                    </way>
                    <way id='-2' changeset='-1'>
                        <nd ref='-1' />
                        <nd ref='-2' />
                        <tag k='object_type' v='AX_Strassenachse' />
                        <tag k='breiteDesVerkehrsweges' v='18' />
                        <tag k='breiteDerFahrbahn' v='9' />
                        <tag k="identifikator:UUID" v="DEBYBDLM11112222"/>
                     </way>
                      <relation id='-1' changeset='-1'>
                        <member type='way' ref='-2' role='' />
                        <tag k='widmung' v='1301' />
                        <tag k='object_type' v='AX_Strasse' />
                        <tag k="identifikator:UUID" v="DEBYBDLM22223333"/>
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
    void createLandesgrenzeMitStrassenachseUndStrasseInUnterschiedlichenBundesland() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.327319499952107" lat="49.878070268874175" version="0"/>
                    <node id="-2" lon="12.330740984227374" lat="49.87807055563436" version="0"/>
                    <way id="-1" version="0">
                        <nd ref="-1"/>
                        <nd ref="-2"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7102"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM12345678"/>
                    </way>
                    <way id='-2' changeset='-1'>
                        <nd ref='-1' />
                        <nd ref='-2' />
                        <tag k='object_type' v='AX_Strassenachse' />
                        <tag k='breiteDesVerkehrsweges' v='18' />
                        <tag k='breiteDerFahrbahn' v='9' />
                        <tag k="identifikator:UUID" v="DEBYBDLM11112222"/>
                     </way>
                      <relation id='-1' changeset='-1'>
                        <member type='way' ref='-2' role='' />
                        <tag k='widmung' v='1301' />
                        <tag k='object_type' v='AX_Strasse' />
                        <tag k="identifikator:UUID" v="DEBWBDLM22223333"/>
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
                .contains("Auf der Landesgrenze gibt es falsche ZUSO-Bildungen.");
    }
}
