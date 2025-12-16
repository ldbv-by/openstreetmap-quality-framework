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
 * Ein Objekt der Objektart 53001 AX_BauwerkImVerkehrsbereich mit der Werteart 1880 bei der Attributart "bauwerksfunktion",
 * muss bei linienförmiger Modellierung immer auf der Geometrie eines oder mehrerer verketteter Objekte der
 * Objektart 42003 AX_Strassenachse,42005 AX_Fahrbahnachse, 42008 AX_Fahrwegachse oder
 * 53003 AX_WegPfadSteig oder 42014 Bahnstrecke liegen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_53001_G_b_002 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.53001.G.b.002"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createSchutzgalerieAufWegPfadSteig() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25362' changeset='-1' lat='49.88064989274' lon='12.32196506929' />
                  <node id='-25361' changeset='-1' lat='49.88237537717' lon='12.32196506929' />
                  <way id='-736' changeset='-1'>
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <tag k='bauwerksfunktion' v='1880' />
                    <tag k='object_type' v='AX_BauwerkImVerkehrsbereich' />
                  </way>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <tag k='object_type' v='AX_WegPfadSteig' />
                  </way>
                  <relation id='-100' changeset='-1'>
                    <member type='way' ref='-663' role='over' />
                    <member type='way' ref='-736' role='under' />
                    <tag k='object_type' v='AA_hatDirektUnten' />
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
    void createSchutzgalerieOhneWegPfadSteig() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25362' changeset='-1' lat='49.88064989274' lon='12.32196506929' />
                  <node id='-25361' changeset='-1' lat='49.88237537717' lon='12.32196506929' />
                  <way id='-736' changeset='-1'>
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <tag k='object_type' v='AX_BauwerkImVerkehrsbereich' />
                    <tag k='bauwerksfunktion' v='1880' />
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
                .contains("Ein Objekt mit der 'bauwerksfunktion' 1880 bei linienförmiger Modellierung muss ein Objekt 'AX_Strassenachse', 'AX_Fahrbahnachse', 'AX_Fahrwegachse', 'AX_WegPfadSteig' oder'AX_Bahnstrecke' überlagern.");
    }
}
