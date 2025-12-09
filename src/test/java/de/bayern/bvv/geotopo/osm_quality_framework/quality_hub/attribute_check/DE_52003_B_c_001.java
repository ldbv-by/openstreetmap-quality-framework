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
 * Der Wert von 'objekthoehe'.'hoehe' muss größer Null sein.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_52003_B_c_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("attribute-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.52003.B.c.001"));

    @Autowired
    MockMvc mockMvc;

    tools.jackson.databind.ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createSchleuseMitGueltigerHoehe() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                    <node id="-30" lon="12.329237982349571" lat="49.881509523012845" version="0"/>
                    <node id="-31" lon="12.330090145657472" lat="49.881497534397276" version="0"/>
                    <node id="-32" lon="12.330088788682724" lat="49.88136812381623" version="0"/>
                    <node id="-33" lon="12.329232711793546" lat="49.88139518446176" version="0"/>
                    <way id="-6" version="0">
                        <nd ref="-30"/>
                        <nd ref="-31"/>
                        <nd ref="-32"/>
                        <nd ref="-33"/>
                        <nd ref="-30"/>
                        <tag k='object_type' v='AX_Schleuse' />
                    </way>
                  <relation id='-2' changeset='-1'>
                    <member type='way' ref='-6' role='' />
                    <tag k='object_type' v='AX_objekthoehe' />
                    <tag k='hoehe' v='50' />
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
    void createSchleuseOhneHoehe() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                    <node id="-30" lon="12.329237982349571" lat="49.881509523012845" version="0"/>
                    <node id="-31" lon="12.330090145657472" lat="49.881497534397276" version="0"/>
                    <node id="-32" lon="12.330088788682724" lat="49.88136812381623" version="0"/>
                    <node id="-33" lon="12.329232711793546" lat="49.88139518446176" version="0"/>
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
    void createSchleuseMitUngueltigerHoehe() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id="-30" lon="12.329237982349571" lat="49.881509523012845" version="0"/>
                  <node id="-31" lon="12.330090145657472" lat="49.881497534397276" version="0"/>
                  <node id="-32" lon="12.330088788682724" lat="49.88136812381623" version="0"/>
                  <node id="-33" lon="12.329232711793546" lat="49.88139518446176" version="0"/>
                  <way id="-6" version="0">
                    <nd ref="-30"/>
                    <nd ref="-31"/>
                    <nd ref="-32"/>
                    <nd ref="-33"/>
                    <nd ref="-30"/>
                    <tag k='object_type' v='AX_Schleuse' />
                  </way>
                  <relation id='-2' changeset='-1'>
                    <member type='way' ref='-6' role='' />
                    <tag k='object_type' v='AX_objekthoehe' />
                    <tag k='hoehe' v='0' />
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
                .contains("Das Tag 'hoehe' der Relation 'AX_objekthoehe' muss größer Null sein.");
    }
}