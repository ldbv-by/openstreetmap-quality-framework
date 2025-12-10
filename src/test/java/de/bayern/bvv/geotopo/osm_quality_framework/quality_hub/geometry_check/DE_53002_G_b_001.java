package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.geometry_check;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.config.JtsJackson3Module;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.dto.QualityHubResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceErrorDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
 * Die Werteart 2000 der Attributart 'Art' überlagert bei linienförmiger Modellierung immer ein
 * Objekt 42003 'Straßenachse', 42008 'Fahrwegachse' oder 53003 'WegPfadSteig' mit identischer Geometrie
 * auf der Umrissgeometrie zweier REO 44001 'Fließgewässer'.
 * AX_Fliessgewaesser wird durch die Maschenbildner der TN Verkehrsachsen in zwei REOs aufgetrennt.
 * Ausnahme, bei 53003 'WegPfadSteig' ist keine REO-Bildung des Fließgewässers notwendig.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_53002_G_b_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.53002.G.b.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createFurtDurchZweiFliessgewaesserMitStrassenachse() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25405' changeset='-1' lat='49.88233813715' lon='12.32595286681' />
                  <node id='-25404' changeset='-1' lat='49.88606199696' lon='12.32602992532' />
                  <node id='-25361' changeset='-1' lat='49.88235056291' lon='12.31869008231' />
                  <node id='-25360' changeset='-1' lat='49.88232573622' lon='12.32288977149' />
                  <node id='-25359' changeset='-1' lat='49.88604959699' lon='12.32288977149' />
                  <node id='-25358' changeset='-1' lat='49.88604959699' lon='12.3186130238' />
                  <way id='-684' changeset='-1'>
                    <nd ref='-25360' />
                    <nd ref='-25359' />
                    <tag k='object_type' v='AX_Strassenverkehrsanlage' />
                    <tag k='art' v='2000' />
                  </way>
                  <way id='-784' changeset='-1'>
                    <nd ref='-25360' />
                    <nd ref='-25359' />
                    <tag k='object_type' v='AX_Strassenachse' />
                    <tag k='breiteDerFahrbahn' v='9' />
                  </way>
                  <way id='-733' changeset='-1'>
                    <nd ref='-25404' />
                    <nd ref='-25359' />
                    <nd ref='-25360' />
                    <nd ref='-25405' />
                    <nd ref='-25404' />
                    <tag k='object_type' v='AX_Fliessgewaesser' />
                  </way>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25358' />
                    <nd ref='-25359' />
                    <nd ref='-25360' />
                    <nd ref='-25361' />
                    <nd ref='-25358' />
                    <tag k='object_type' v='AX_Fliessgewaesser' />
                  </way>
                  <relation id='-65' changeset='-1'>
                    <member type='way' ref='-784' role='' />
                    <tag k='object_type' v='AX_Strasse' />
                    <tag k='widmung' v='1303' />
                  </relation>
                </create>
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
    void createFurtDurchEinFliessgewaesserMitStrassenachse() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25426' changeset='-1' lat='49.88233826332' lon='12.32077068333' />
                  <node id='-25425' changeset='-1' lat='49.88604959699' lon='12.32075141691' />
                  <node id='-25361' changeset='-1' lat='49.88235056291' lon='12.31869008231' />
                  <node id='-25360' changeset='-1' lat='49.88232573622' lon='12.32288977149' />
                  <node id='-25359' changeset='-1' lat='49.88604959699' lon='12.32288977149' />
                  <node id='-25358' changeset='-1' lat='49.88604959699' lon='12.3186130238' />
                  <way id='-733' changeset='-1'>
                    <nd ref='-25425' />
                    <nd ref='-25426' />
                    <tag k='object_type' v='AX_Strassenachse' />
                    <tag k='breiteDerFahrbahn' v='9' />
                  </way>
                  <way id='-766' changeset='-1'>
                    <nd ref='-25425' />
                    <nd ref='-25426' />
                    <tag k='object_type' v='AX_Strassenverkehrsanlage' />
                    <tag k='art' v='2000' />
                  </way>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25358' />
                    <nd ref='-25425' />
                    <nd ref='-25359' />
                    <nd ref='-25360' />
                    <nd ref='-25426' />
                    <nd ref='-25361' />
                    <nd ref='-25358' />
                    <tag k='object_type' v='AX_Fliessgewaesser' />
                  </way>
                  <relation id='-50' changeset='-1'>
                    <member type='way' ref='-773' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AX_Strasse' />
                  </relation>
                </create>
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
                .contains("Das Objekt mit 'art' 2000 überlagert bei linienförmiger Modellierung immer ein Objekt 'AX_Strassenachse', 'AX_Fahrwegachse' oder 'AX_WegPfadSteig' mit identischer Geometrie auf der Umrissgeometrie 'AX_Fliessgewaesser'. 'AX_Fliessgewaesser' wird durch die Maschenbildner getrennt (Ausnahme bei 'AX_WegPfadSteig').");
    }

    @Test
    void createFurtDurchEinFliessgewaesserMitWegPfadSteig() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25426' changeset='-1' lat='49.88233826332' lon='12.32077068333' />
                  <node id='-25425' changeset='-1' lat='49.88604959699' lon='12.32075141691' />
                  <node id='-25361' changeset='-1' lat='49.88235056291' lon='12.31869008231' />
                  <node id='-25360' changeset='-1' lat='49.88232573622' lon='12.32288977149' />
                  <node id='-25359' changeset='-1' lat='49.88604959699' lon='12.32288977149' />
                  <node id='-25358' changeset='-1' lat='49.88604959699' lon='12.3186130238' />
                  <way id='-733' changeset='-1'>
                    <nd ref='-25425' />
                    <nd ref='-25426' />
                    <tag k='object_type' v='AX_WegPfadSteig' />
                  </way>
                  <way id='-766' changeset='-1'>
                    <nd ref='-25425' />
                    <nd ref='-25426' />
                    <tag k='object_type' v='AX_Strassenverkehrsanlage' />
                    <tag k='art' v='2000' />
                  </way>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25358' />
                    <nd ref='-25425' />
                    <nd ref='-25359' />
                    <nd ref='-25360' />
                    <nd ref='-25426' />
                    <nd ref='-25361' />
                    <nd ref='-25358' />
                    <tag k='object_type' v='AX_Fliessgewaesser' />
                  </way>
                </create>
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
