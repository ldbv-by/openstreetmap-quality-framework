package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.attribute_check;

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
 * Die Attributart 'Sportart' kann nur in Verbindung mit der
 * Attributart 'Bauwerksfunktion' und den Wertearten 1410, 1420 und 1440 vorkommen.
 * <p>
 * AdV-Beschreibung:
 * Die Attributart 'Sportart' kann nur in Verbindung mit der
 * Attributart 'Bauwerksfunktion' und den Wertearten 1410, 1420, 1441 und 1442 vorkommen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_51006_A_b_001_002 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("attribute-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.51006.A.b.001_002"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createSpielfeldMitSportart() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25365' changeset='-1' lat='49.88318223722' lon='12.34785675027' />
                  <node id='-25364' changeset='-1' lat='49.88324430282' lon='12.34855027693' />
                  <node id='-25363' changeset='-1' lat='49.88352980354' lon='12.34847321841' />
                  <node id='-25362' changeset='-1' lat='49.88345532526' lon='12.3477411625' />
                  <way id='-665' changeset='-1'>
                    <nd ref='-25362' />
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <nd ref='-25365' />
                    <nd ref='-25362' />
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerSportFreizeitUndErholung' />
                    <tag k='bauwerksfunktion' v='1410' />
                    <tag k='sportart' v='1010;1011' />
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
    void createZuschauertribueneMitSportart() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25365' changeset='-1' lat='49.88318223722' lon='12.34785675027' />
                  <node id='-25364' changeset='-1' lat='49.88324430282' lon='12.34855027693' />
                  <node id='-25363' changeset='-1' lat='49.88352980354' lon='12.34847321841' />
                  <node id='-25362' changeset='-1' lat='49.88345532526' lon='12.3477411625' />
                  <way id='-665' changeset='-1'>
                    <nd ref='-25362' />
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <nd ref='-25365' />
                    <nd ref='-25362' />
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerSportFreizeitUndErholung' />
                    <tag k='bauwerksfunktion' v='1431' />
                    <tag k='sportart' v='1010;1011' />
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

        QualityServiceResultDto attributeCheck = qualityHubResultDto.qualityServiceResults().stream()
                .filter(s -> "attribute-check".equals(s.qualityServiceId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("QualityService 'attribute-check' not found"));

        assertThat(attributeCheck.isValid()).withFailMessage("Expected the result is not valid, but it was valid.").isFalse();

        assertThat(attributeCheck.errors())
                .as("Errors of 'attribute-check' must not be empty")
                .isNotEmpty();

        assertThat(attributeCheck.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .as("Error text of 'attribut-check'")
                .contains("Das Tag 'sportart' darf nur bei der 'bauwerksfunktion' 1410, 1420, 1440, 1441 und 1442 vorkommen.");
    }
}