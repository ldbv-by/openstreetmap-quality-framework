package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.geometry_check;

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
 * 57003 'Gewässerstationierungsachse' darf nur auf einem oder mehreren räumlich aneinandergrenzenden Objekten
 * 44001 'Fließgewässer', 44006 'Stehendes Gewässer', 44005 'Hafenbecken', 44007 'Meer', 74004 'Insel',
 * 53009 'Bauwerk im Gewässerbereich' mit 'Bauwerksfunktion' 2131 'Wellenbrecher, Buhne', flächenförmiger Durchlass
 * ('AX_BauwerkImGewaesserbereich' mit 'bauwerksfunktion' 2010 Durchlass, 2012 Düker, 2070 Siel, 2090 Schöpfwerk) oder
 * 75009 'Gebietsgrenze' mit 'artDerGebietsgrenze' 7102 'Grenze des Bundeslandes' liegen (DE-9IM Matrix: **F**F***).
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_57003_F_b_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.57003.F.b.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createGewaesserstationierungsachseUeberZweiFliessgewaesser() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-35" lon="12.314264227476022" lat="49.878734226433444" version="0"/>
                        <node id="-36" lon="12.315895089171853" lat="49.87874152492932" version="0"/>
                        <node id="-37" lon="12.315940390189576" lat="49.877362147382755" version="0"/>
                        <node id="-38" lon="12.314298203239312" lat="49.877362147382755" version="0"/>
                        <node id="-41" lon="12.31768450615784" lat="49.87874882314566" version="0"/>
                        <node id="-42" lon="12.317729808039626" lat="49.87738404251477" version="0"/>
                        <node id="-46" lon="12.31510230926483" lat="49.87812847318412" version="0"/>
                        <node id="-47" lon="12.316891726250818" lat="49.878135771353925" version="0"/>
                        <way id="-8" version="0">
                            <nd ref="-35"/>
                            <nd ref="-36"/>
                            <nd ref="-37"/>
                            <nd ref="-38"/>
                            <nd ref="-35"/>
                            <tag k="object_type" v="AX_Fliessgewaesser"/>
                        </way>
                        <way id="-9" version="0">
                            <nd ref="-36"/>
                            <nd ref="-41"/>
                            <nd ref="-42"/>
                            <nd ref="-37"/>
                            <nd ref="-36"/>
                            <tag k="object_type" v="AX_Fliessgewaesser"/>
                        </way>
                        <way id="-10" version="0">
                            <nd ref="-46"/>
                            <nd ref="-47"/>
                            <tag k="object_type" v="AX_Gewaesserstationierungsachse"/>
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
    void createGewaesserstationierungsachseUeberFliessgewaesserUndWohnbauflaeche() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-35" lon="12.314264227476022" lat="49.878734226433444" version="0"/>
                        <node id="-36" lon="12.315895089171853" lat="49.87874152492932" version="0"/>
                        <node id="-37" lon="12.315940390189576" lat="49.877362147382755" version="0"/>
                        <node id="-38" lon="12.314298203239312" lat="49.877362147382755" version="0"/>
                        <node id="-41" lon="12.31768450615784" lat="49.87874882314566" version="0"/>
                        <node id="-42" lon="12.317729808039626" lat="49.87738404251477" version="0"/>
                        <node id="-46" lon="12.31510230926483" lat="49.87812847318412" version="0"/>
                        <node id="-47" lon="12.316891726250818" lat="49.878135771353925" version="0"/>
                        <way id="-8" version="0">
                            <nd ref="-35"/>
                            <nd ref="-36"/>
                            <nd ref="-37"/>
                            <nd ref="-38"/>
                            <nd ref="-35"/>
                            <tag k="object_type" v="AX_Fliessgewaesser"/>
                        </way>
                        <way id="-9" version="0">
                            <nd ref="-36"/>
                            <nd ref="-41"/>
                            <nd ref="-42"/>
                            <nd ref="-37"/>
                            <nd ref="-36"/>
                            <tag k="object_type" v="AX_Wohnbauflaeche"/>
                        </way>
                        <way id="-10" version="0">
                            <nd ref="-46"/>
                            <nd ref="-47"/>
                            <tag k="object_type" v="AX_Gewaesserstationierungsachse"/>
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
                .contains("Eine Gewässerstationierungsachse darf nur in einem oder mehreren räumlich aneinandergrenzenden Objekten Fließgewässer, stehendem Gewässer, Hafenbecken, Meer, Insel, Bauwerk im Gewässerbereich liegen oder auf Grenze des Bundeslandes verlaufen.");
    }
}