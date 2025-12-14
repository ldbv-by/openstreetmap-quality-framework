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
 * Für jedes AA_REO untersuche alle Features, die durch adv:hatDirektUnten/@xlink:href identifiziert werden und prüfe,
 * dass es sich um ein REO handelt.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_02000_R_a_004 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("attribute-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.02000.R.a.004"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createHatDirektUntenMitReos() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-19" lon="12.324354388416007" lat="49.87831289713437" version="0"/>
                        <node id="-20" lon="12.326848805302411" lat="49.878325604179174" version="0"/>
                        <node id="-21" lon="12.326878383674492" lat="49.8769341681193" version="0"/>
                        <node id="-22" lon="12.32426565405197" lat="49.876965936510956" version="0"/>
                        <node id="-25" lon="12.325261002609867" lat="49.8783175156038" version="0"/>
                        <relation id="-2" version="0">
                            <member type="way" role="" ref="-7"/>
                            <tag k="object_type" v="AX_Strasse"/>
                            <tag k="widmung" v="1301"/>
                        </relation>
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
    void createHatDirektUntenMitZuso() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-19" lon="12.324354388416007" lat="49.87831289713437" version="0"/>
                        <node id="-20" lon="12.326848805302411" lat="49.878325604179174" version="0"/>
                        <node id="-21" lon="12.326878383674492" lat="49.8769341681193" version="0"/>
                        <node id="-22" lon="12.32426565405197" lat="49.876965936510956" version="0"/>
                        <node id="-25" lon="12.325261002609867" lat="49.8783175156038" version="0"/>
                        <relation id="-2" version="0">
                            <member type="way" role="" ref="-7"/>
                            <tag k="object_type" v="AX_Strasse"/>
                            <tag k="widmung" v="1301"/>
                        </relation>
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
                            <member type="relation" role="under" ref="-2"/>
                            <member type="way" role="over" ref="-7"/>
                            <tag k="object_type" v="AA_hatDirektUnten"/>
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
                .contains("Die Objektart 'AX_Strasse' darf keine Relation 'AA_hatDirektUnten' haben.");
    }
}