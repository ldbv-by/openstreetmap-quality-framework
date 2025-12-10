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
 * Punktförmige Objekte der Werteart 1460 der Attributart 'Art' liegen immer auf der Umrissgeometrie eines
 * Objekts 44001 'Fließgewässer', 44005 'Hafenbecken', 44006 'StehendesGewässer', 44007 'Meer'
 * oder berühren ein Objekt 53009 'BauwerkImGewaesserbereich' der Bauwerksfunktion 2133 Hafendamm, Mole.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_53008_G_b_001_F_b_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.53008.G.b.001_F.b.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createAnlegerAufHafenbecken() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25361' changeset='-1' lat='49.87977158487' lon='12.31859812646' />
                  <node id='-25360' changeset='-1' lat='49.87977158487' lon='12.32451384954' />
                  <node id='-25359' changeset='-1' lat='49.88413518675' lon='12.32447493031' />
                  <node id='-25358' changeset='-1' lat='49.8841101097' lon='12.31855920723'>
                    <tag k='object_type' v='AX_EinrichtungenFuerDenSchiffsverkehr' />
                    <tag k='art' v='1460' />
                  </node>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25358' />
                    <nd ref='-25359' />
                    <nd ref='-25360' />
                    <nd ref='-25361' />
                    <nd ref='-25358' />
                    <tag k='object_type' v='AX_Hafenbecken' />
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

    @Test
    void createAnlegerOhneFliessgewaesser() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25358' changeset='-1' lat='49.8841101097' lon='12.31855920723'>
                    <tag k='object_type' v='AX_EinrichtungenFuerDenSchiffsverkehr' />
                    <tag k='art' v='1460' />
                  </node>
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
                .contains("Ein Anleger liegt immer auf der Umrissgeometrie eines Objekts 'AX_Fliessgewaesser', 'AX_Hafenbecken', 'AX_StehendesGewaesser' oder 'AX_Meer' oder berühren 'AX_BauwerkImGewaesserbereich' mit 'bauwerksfunktion' 2133.");
    }
}