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
 * 44001 'Fließgewässer' mit Relation hatDirektUnten zu einem flächenförmigen 53009 'Bauwerk im Gewässerbereich'
 * BWF 2010 bis 2012, 2070 und 2090 muss innerhalb der Umrissgeometrie dieses Bauwerkes liegen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_53009_F_b_005_009 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.53009.F.b.005_009"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createFliessgewaesserAufDurchlass() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-10" lon="12.315374118827418" lat="49.87856636810196" version="0"/>
                    <node id="-11" lon="12.3173787199679" lat="49.87864664829476" version="0"/>
                    <node id="-12" lon="12.317514624749192" lat="49.87713589663989" version="0"/>
                    <node id="-13" lon="12.315476047845417" lat="49.8770483147669" version="0"/>
                    <way id="-3" version="0">
                        <nd ref="-10"/>
                        <nd ref="-11"/>
                        <nd ref="-12"/>
                        <nd ref="-13"/>
                        <nd ref="-10"/>
                        <tag k="object_type" v="AX_Fliessgewaesser"/>
                    </way>
                    <way id="-4" version="0">
                        <nd ref="-10"/>
                        <nd ref="-11"/>
                        <nd ref="-12"/>
                        <nd ref="-13"/>
                        <nd ref="-10"/>
                        <tag k="object_type" v="AX_BauwerkImGewaesserbereich"/>
                        <tag k="bauwerksfunktion" v="2010"/>
                    </way>
                    <relation id='-1' changeset='-1'>
                        <member type='way' ref='-3' role='over' />
                        <member type='way' ref='-4' role='under' />
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
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result to be valid, but it was invalid.").isTrue();
    }

    @Test
    void createFliessgewaesserAusserhalbDurchlass() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-10" lon="12.315374118827418" lat="49.87856636810196" version="0"/>
                        <node id="-11" lon="12.3173787199679" lat="49.87864664829476" version="0"/>
                        <node id="-12" lon="12.317514624749192" lat="49.87713589663989" version="0"/>
                        <node id="-13" lon="12.315476047845417" lat="49.8770483147669" version="0"/>
                        <node id="-23" lon="12.31826210277443" lat="49.87661770503797" version="0"/>
                        <way id="-3" version="0">
                            <nd ref="-10"/>
                            <nd ref="-11"/>
                            <nd ref="-12"/>
                            <nd ref="-13"/>
                            <nd ref="-10"/>
                            <tag k="object_type" v="AX_BauwerkImGewaesserbereich"/>
                            <tag k="bauwerksfunktion" v="2010"/>
                        </way>
                        <way id="-6" version="0">
                            <nd ref="-23"/>
                            <nd ref="-13"/>
                            <nd ref="-10"/>
                            <nd ref="-11"/>
                            <nd ref="-23"/>
                            <tag k="object_type" v="AX_Fliessgewaesser"/>
                        </way>
                        <relation id='-1' changeset='-1'>
                            <member type='way' ref='-6' role='over' />
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
                .contains("Ein Objekt 'AX_Fliessgewaesser' das unter einem Objekt 'AX_BauwerkImGewaesserbereich' mit 'bauwerksfunktion' 2010, 2011, 2012, 2070 oder 2090 liegt, muss innerhalb der Umrissgemetrie des Bauwerks liegen.");
    }
}
