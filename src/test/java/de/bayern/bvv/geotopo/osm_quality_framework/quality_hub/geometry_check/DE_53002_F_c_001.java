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
 * Die Werteart 2000 'Furt' der Attributart 'Art' darf kein Gewässer überlagern,
 * dass durch ein Objekt der Objektart 53009 'Bauwerk im Gewässerbereich' mit BWF 2010 – 2013 fließt.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_53002_F_c_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.53002.F.c.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createFurtAufFliessgewaesserOhneDurchlass() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.318052582543244" lat="49.87674360433969" version="0"/>
                    <node id="-2" lon="12.319949591606477" lat="49.8768074660941" version="0"/>
                    <node id="-3" lon="12.320133629799182" lat="49.87512878619528" version="0"/>
                    <node id="-4" lon="12.318194150383786" lat="49.87507404565088" version="0"/>
                    <node id="-7" lon="12.318119811356977" lat="49.875950758798155" version="0"/>
                    <node id="-8" lon="12.320035604107295" lat="49.87602292177052" version="0"/>
                    <way id="-1" version="0">
                        <nd ref="-1"/>
                        <nd ref="-2"/>
                        <nd ref="-8"/>
                        <nd ref="-3"/>
                        <nd ref="-4"/>
                        <nd ref="-7"/>
                        <nd ref="-1"/>
                        <tag k="object_type" v="AX_Fliessgewaesser"/>
                    </way>
                    <way id="-2" version="0">
                        <nd ref="-7"/>
                        <nd ref="-8"/>
                        <tag k="object_type" v="AX_Strassenverkehrsanlage"/>
                        <tag k="art" v="2000"/>
                    </way>
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
    void createFurtAufFliessgewaesserMitDurchlass() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.318052582543244" lat="49.87674360433969" version="0"/>
                    <node id="-2" lon="12.319949591606477" lat="49.8768074660941" version="0"/>
                    <node id="-3" lon="12.320133629799182" lat="49.87512878619528" version="0"/>
                    <node id="-4" lon="12.318194150383786" lat="49.87507404565088" version="0"/>
                    <node id="-7" lon="12.318119811356977" lat="49.875950758798155" version="0"/>
                    <node id="-8" lon="12.320035604107295" lat="49.87602292177052" version="0"/>
                    <way id="-1" version="0">
                        <nd ref="-7"/>
                        <nd ref="-8"/>
                        <tag k="object_type" v="AX_Strassenverkehrsanlage"/>
                        <tag k="art" v="2000"/>
                    </way>
                    <way id="-2" version="0">
                        <nd ref="-1"/>
                        <nd ref="-2"/>
                        <nd ref="-8"/>
                        <nd ref="-3"/>
                        <nd ref="-4"/>
                        <nd ref="-7"/>
                        <nd ref="-1"/>
                        <tag k="object_type" v="AX_Fliessgewaesser"/>
                    </way>
                    <way id="-3" version="0">
                        <nd ref="-1"/>
                        <nd ref="-2"/>
                        <nd ref="-8"/>
                        <nd ref="-3"/>
                        <nd ref="-4"/>
                        <nd ref="-7"/>
                        <nd ref="-1"/>
                        <tag k="object_type" v="AX_BauwerkImGewaesserbereich"/>
                        <tag k="bauwerksfunktion" v="2010"/>
                    </way>
                    <relation id='-1' changeset='-1'>
                        <member type='way' ref='-2' role='over' />
                        <member type='way' ref='-3' role='under' />
                        <tag k='object_type' v='AA_hatDirektUnten' />
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
                .contains("Furt darf ein unter der Erdoberfläche verlaufendes Gewässer nicht überlagern.");
    }
}
