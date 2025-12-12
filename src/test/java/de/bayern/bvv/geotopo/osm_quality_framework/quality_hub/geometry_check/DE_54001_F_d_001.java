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
 * 54001 'Vegetationsmerkmal' mit 'ZUS' 5000 'Nass' darf nur auf 43001 'Landwirtschaft' mit 'VEG' 1010 'Ackerland' oder 1020 'Grünland',
 * 43002 'Wald', 43003 'Gehölz', 43004 'Heide', 43005 'Moor', 43007 'UnlandVegetationsloseFlaeche' mit 'FKT' 1100 'Gewässerbegleitfläche' oder
 * 'FKT' 1300 'Naturnahe Fläche' oder 'ohne FKT' liegen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_54001_F_d_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.54001.F.d.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createVegetationsmerkmalNassAufAckerland() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-10" lon="12.315374118827418" lat="49.87856636810196" version="0"/>
                        <node id="-11" lon="12.3173787199679" lat="49.87864664829476" version="0"/>
                        <node id="-12" lon="12.317514624749192" lat="49.87713589663989" version="0"/>
                        <node id="-13" lon="12.315476047845417" lat="49.8770483147669" version="0"/>
                        <node id="-29" lon="12.317464628943638" lat="49.877691668057096" version="0"/>
                        <node id="-30" lon="12.316869075741955" lat="49.877661380922355" version="0"/>
                        <node id="-31" lon="12.31695085904742" lat="49.87711167600657" version="0"/>
                        <way id="-3" version="0">
                            <nd ref="-10"/>
                            <nd ref="-11"/>
                            <nd ref="-29"/>
                            <nd ref="-12"/>
                            <nd ref="-31"/>
                            <nd ref="-13"/>
                            <nd ref="-10"/>
                            <tag k="object_type" v="AX_Landwirtschaft"/>
                            <tag k="vegetationsmerkmal" v="1010"/>
                        </way>
                        <way id="-7" version="0">
                            <nd ref="-29"/>
                            <nd ref="-30"/>
                            <nd ref="-31"/>
                            <nd ref="-12"/>
                            <nd ref="-29"/>
                            <tag k="object_type" v="AX_Vegetationsmerkmal"/>
                            <tag k="zustand" v="5000"/>
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
    void createVegetationsmerkmalNassOhneAckerland() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-10" lon="12.315374118827418" lat="49.87856636810196" version="0"/>
                        <node id="-11" lon="12.3173787199679" lat="49.87864664829476" version="0"/>
                        <node id="-12" lon="12.317514624749192" lat="49.87713589663989" version="0"/>
                        <node id="-13" lon="12.315476047845417" lat="49.8770483147669" version="0"/>
                        <node id="-29" lon="12.317464628943638" lat="49.877691668057096" version="0"/>
                        <node id="-30" lon="12.316869075741955" lat="49.877661380922355" version="0"/>
                        <node id="-31" lon="12.31695085904742" lat="49.87711167600657" version="0"/>
                        <way id="-7" version="0">
                            <nd ref="-29"/>
                            <nd ref="-30"/>
                            <nd ref="-31"/>
                            <nd ref="-12"/>
                            <nd ref="-29"/>
                            <tag k="object_type" v="AX_Vegetationsmerkmal"/>
                            <tag k="zustand" v="5000"/>
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
                .contains("Ein 'AX_Vegetationsmerkmal' mit 'zustand' 5000 darf nur auf einem Objekt 'AX_Landwirtschaft' mit 'vegetationsmerkmal' 1010 oder 1020, 'AX_Wald', 'AX_Gehoelz', 'AX_Heide', 'AX_Moor' oder 'AX_UnlandVegetationsloseFlaeche' mit 'funktion' 1100 oder 1300 liegen.");
    }
}
