package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.attribute_check;

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
 * Es ist zu prüfen, dass die Relation hatDirektUnten nur zu AX_BauwerkImVerkehrsbereich, AX_EinrichtungenFuerDenSchiffsverkehr,
 * AX_BauwerkImGewaesserbereich, AX_Gebaeude oder AX_DammWallDeich mit FKT 3002, 3003 und 3004 vorhanden ist.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_40001_R_a_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("attribute-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.40001.R.a.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createStrassenachseAufBruecke() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-20" lon="12.326848805302411" lat="49.878325604179174" version="0"/>
                        <node id="-21" lon="12.326878383674492" lat="49.8769341681193" version="0"/>
                        <way id="-7" version="0">
                            <nd ref="-20"/>
                            <nd ref="-21"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-8" version="0">
                            <nd ref="-20"/>
                            <nd ref="-21"/>
                            <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                            <tag k="bauwerksfunktion" v="1800"/>
                        </way>
                        <relation id="-1" version="0">
                            <member type="way" role="under" ref="-8"/>
                            <member type="way" role="over" ref="-7"/>
                            <tag k="object_type" v="AA_hatDirektUnten"/>
                        </relation>
                        <relation id='-3' changeset='-1'>
                            <member type='way' ref='-7' role='' />
                            <tag k='object_type' v='AX_Strasse' />
                            <tag k='widmung' v='1301' />
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
    void createStrassenachseAufWegPfadSteig() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-20" lon="12.326848805302411" lat="49.878325604179174" version="0"/>
                        <node id="-21" lon="12.326878383674492" lat="49.8769341681193" version="0"/>
                        <way id="-7" version="0">
                            <nd ref="-20"/>
                            <nd ref="-21"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-8" version="0">
                            <nd ref="-20"/>
                            <nd ref="-21"/>
                            <tag k="object_type" v="AX_WegPfadSteig"/>
                        </way>
                        <relation id="-1" version="0">
                            <member type="way" role="under" ref="-8"/>
                            <member type="way" role="over" ref="-7"/>
                            <tag k="object_type" v="AA_hatDirektUnten"/>
                        </relation>
                        <relation id='-3' changeset='-1'>
                            <member type='way' ref='-7' role='' />
                            <tag k='object_type' v='AX_Strasse' />
                            <tag k='widmung' v='1301' />
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
                .contains("Die Relation hatDirektUnten darf nur zu AX_BauwerkImVerkehrsbereich, AX_EinrichtungenFuerDenSchiffsverkehr, AX_BauwerkImGewaesserbereich, AX_Gebaeude oder AX_DammWallDeich mit FKT 3002, 3003 und 3004 vorhanden sein.");
    }
}