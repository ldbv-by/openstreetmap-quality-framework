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
 * Bei linienförmiger Modellierung überlagert die Werteart 1890 'Schleusenkammer' der Attributart 'Bauwerksfunktion' immer ein
 * Objekt 44004 'Gewässerachse' mit identischer Geometrie,
 * bei punktförmiger Modellierung liegt die 'Schleusenkammer' immer auf der Geometrie eines
 * Objekts 44004 'Gewässerachse' und
 * bei flächenförmiger Modellierung überlagert die Schleusenkammer immer ein Objekt 44001 'Fließgewässer'.
 *
 * 53001 'BauwerkImVerkehrsbereich' mit BWF 1890 'Schleusenkammer' befindet sich innerhalb von
 * (einer oder mehreren benachbarten) 52003 'Schleuse' flächenförmig.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_53001_G_b_004_006 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.53001.G.b.004_006"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createSchleusenkammerAufGewaesserachseMitSchleuse() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25362' changeset='-1' lat='49.88064989274' lon='12.32196506929' />
                  <node id='-25361' changeset='-1' lat='49.88237537717' lon='12.32196506929' />
                  <node id='-25370' changeset='-1' lat='49.88054989274' lon='12.32166506929' />
                  <node id='-25371' changeset='-1' lat='49.88247537717' lon='12.32166506929' />
                  <node id='-25372' changeset='-1' lat='49.88247537717' lon='12.32226506929' />
                  <node id='-25373' changeset='-1' lat='49.88054989274' lon='12.32226506929' />
                  <way id='-740' changeset='-1'>
                    <nd ref='-25370' />
                    <nd ref='-25371' />
                    <nd ref='-25372' />
                    <nd ref='-25373' />
                    <nd ref='-25370' />
                    <tag k='object_type' v='AX_Schleuse' />
                  </way>
                  <way id='-736' changeset='-1'>
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <tag k='object_type' v='AX_BauwerkImVerkehrsbereich' />
                    <tag k='bauwerksfunktion' v='1890' />
                  </way>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <tag k='object_type' v='AX_Gewaesserachse' />
                    <tag k='breiteDesGewaessers' v='9' />
                    <tag k='fliessrichtung' v='TRUE' />
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
    void createSchleusenkammerAufWegPfadSteigMitSchleuse() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25362' changeset='-1' lat='49.88064989274' lon='12.32196506929' />
                  <node id='-25361' changeset='-1' lat='49.88237537717' lon='12.32196506929' />
                  <node id='-25370' changeset='-1' lat='49.88054989274' lon='12.32166506929' />
                  <node id='-25371' changeset='-1' lat='49.88247537717' lon='12.32166506929' />
                  <node id='-25372' changeset='-1' lat='49.88247537717' lon='12.32226506929' />
                  <node id='-25373' changeset='-1' lat='49.88054989274' lon='12.32226506929' />
                  <way id='-740' changeset='-1'>
                    <nd ref='-25370' />
                    <nd ref='-25371' />
                    <nd ref='-25372' />
                    <nd ref='-25373' />
                    <nd ref='-25370' />
                    <tag k='object_type' v='AX_Schleuse' />
                  </way>
                  <way id='-736' changeset='-1'>
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <tag k='object_type' v='AX_BauwerkImVerkehrsbereich' />
                    <tag k='bauwerksfunktion' v='1890' />
                  </way>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <tag k='object_type' v='AX_WegPfadSteig' />
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
                .contains("Ein Objekt mit der 'bauwerksfunktion' 1890 befindet sich immer innerhalb einer 'AX_Schleue' und bei linienförmiger Modellierung muss geometrieidentisch mit einem Objekt 'AX_Gewaesserachse' sein. Bei punktförmiger Modellierung muss dieser auf einer 'AX_Gewaesserachse' liegen. Bei flächenförmiger Modellierung wird ein 'AX_Fliessgewaesser' überlagert.");
    }

    @Test
    void createSchleusenkammerAufWegPfadSteigOhneSchleuse() throws Exception {
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
                    <tag k='bauwerksfunktion' v='1890' />
                  </way>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <tag k='object_type' v='AX_WegPfadSteig' />
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
                .contains("Ein Objekt mit der 'bauwerksfunktion' 1890 befindet sich immer innerhalb einer 'AX_Schleue' und bei linienförmiger Modellierung muss geometrieidentisch mit einem Objekt 'AX_Gewaesserachse' sein. Bei punktförmiger Modellierung muss dieser auf einer 'AX_Gewaesserachse' liegen. Bei flächenförmiger Modellierung wird ein 'AX_Fliessgewaesser' überlagert.");
    }

    @Test
    void createSchleusenkammerOhneGewaesserachseUndOhneSchleuse() throws Exception {
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
                    <tag k='bauwerksfunktion' v='1890' />
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
                .contains("Ein Objekt mit der 'bauwerksfunktion' 1890 befindet sich immer innerhalb einer 'AX_Schleue' und bei linienförmiger Modellierung muss geometrieidentisch mit einem Objekt 'AX_Gewaesserachse' sein. Bei punktförmiger Modellierung muss dieser auf einer 'AX_Gewaesserachse' liegen. Bei flächenförmiger Modellierung wird ein 'AX_Fliessgewaesser' überlagert.");
    }
}
