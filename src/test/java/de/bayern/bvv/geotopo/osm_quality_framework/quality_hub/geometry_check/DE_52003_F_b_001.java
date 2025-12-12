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
 * Innerhalb von 'Schleuse' darf nur 44001 'Fließgewässer', 44005 'Hafenbecken', 44006 'Stehendes Gewässer',
 * 44007 'Meer' oder 42016 'Schiffsverkehr' mit FKT 5620 liegen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_52003_F_b_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.52003.F.b.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createSchleuseAufSchiffsverkehrMitFunktionSchleuse() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                    <node id="-8" lon="12.330094720996826" lat="49.88193386812974" version="0"/>
                    <node id="-11" lon="12.330085381002549" lat="49.88104314212553" version="0"/>
                    <node id="-18" lon="12.329216761534958" lat="49.881049160599645" version="0"/>
                    <node id="-19" lon="12.329258791509199" lat="49.88196095075758" version="0"/>
                    <node id="-30" lon="12.329237982349571" lat="49.881509523012845" version="0"/>
                    <node id="-31" lon="12.330090145657472" lat="49.881497534397276" version="0"/>
                    <node id="-32" lon="12.330088788682724" lat="49.88136812381623" version="0"/>
                    <node id="-33" lon="12.329232711793546" lat="49.88139518446176" version="0"/>
                    <way id="-4" version="0">
                        <nd ref="-11"/>
                        <nd ref="-18"/>
                        <nd ref="-33"/>
                        <nd ref="-30"/>
                        <nd ref="-19"/>
                        <nd ref="-8"/>
                        <nd ref="-31"/>
                        <nd ref="-32"/>
                        <nd ref="-11"/>
                        <tag k="object_type" v="AX_Schiffsverkehr"/>
                        <tag k="funktion" v="5620"/>
                    </way>
                    <way id="-6" version="0">
                        <nd ref="-30"/>
                        <nd ref="-31"/>
                        <nd ref="-32"/>
                        <nd ref="-33"/>
                        <nd ref="-30"/>
                        <tag k='object_type' v='AX_Schleuse' />
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
    void createSchleuseAufSchiffsverkehrOhneFunktion() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                    <node id="-8" lon="12.330094720996826" lat="49.88193386812974" version="0"/>
                    <node id="-11" lon="12.330085381002549" lat="49.88104314212553" version="0"/>
                    <node id="-18" lon="12.329216761534958" lat="49.881049160599645" version="0"/>
                    <node id="-19" lon="12.329258791509199" lat="49.88196095075758" version="0"/>
                    <node id="-30" lon="12.329237982349571" lat="49.881509523012845" version="0"/>
                    <node id="-31" lon="12.330090145657472" lat="49.881497534397276" version="0"/>
                    <node id="-32" lon="12.330088788682724" lat="49.88136812381623" version="0"/>
                    <node id="-33" lon="12.329232711793546" lat="49.88139518446176" version="0"/>
                    <way id="-4" version="0">
                        <nd ref="-11"/>
                        <nd ref="-18"/>
                        <nd ref="-33"/>
                        <nd ref="-30"/>
                        <nd ref="-19"/>
                        <nd ref="-8"/>
                        <nd ref="-31"/>
                        <nd ref="-32"/>
                        <nd ref="-11"/>
                        <tag k="object_type" v="AX_Schiffsverkehr"/>
                    </way>
                    <way id="-6" version="0">
                        <nd ref="-30"/>
                        <nd ref="-31"/>
                        <nd ref="-32"/>
                        <nd ref="-33"/>
                        <nd ref="-30"/>
                        <tag k='object_type' v='AX_Schleuse' />
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
                .contains("Ein Objekt 'AX_Schleuse' muss auf 'AX_Fliessgewaesser', 'AX_Hafenbecken', 'AX_StehendesGewaesser', 'AX_Meer' oder 'AX_Schiffsverkehr' mit 'funktion' 5620 liegen.");
    }
}