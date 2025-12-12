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
 * Schließt sich an einem Ende einer 75009 'Gebietsgrenze' genau eine andere Gebietsgrenze an, so muss diese identische
 * (gleiche, keine zusätzliche) 'Art der Gebietsgrenze'-Werte enthalten. Der Wert 'Art der Gebietsgrenze' 7105 Grenze der Verwaltungsgemeinschaft
 * bleibt bei dem Wertevergleich immer unberücksichtigt. Schließen sich an einem Ende einer 75009 'Gebietsgrenze' mehr
 * als eine andere Gebietsgrenze an, so findet keine weitere Prüfung statt.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_75009_F_a_003 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.75009.F.a.003"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createIdentischeGrenzlinien() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-49" lon="12.316200875361794" lat="49.87837661409732" version="0"/>
                        <node id="-50" lon="12.318182825129353" lat="49.878383912368825" version="0"/>
                        <node id="-53" lon="12.317271128754717" lat="49.87713954559129" version="0"/>
                        <way id="-11" version="0">
                            <nd ref="-49"/>
                            <nd ref="-50"/>
                            <tag k="artDerGebietsgrenze" v="7102"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                        </way>
                        <way id="-12" version="0">
                            <nd ref="-50"/>
                            <nd ref="-53"/>
                            <tag k="artDerGebietsgrenze" v="7102"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                        </way>
                        <way id="-13" version="0">
                            <nd ref="-49"/>
                            <nd ref="-53"/>
                            <tag k="artDerGebietsgrenze" v="7102"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
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
    void createNichtIdentischeGrenzlinien() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-49" lon="12.316200875361794" lat="49.87837661409732" version="0"/>
                        <node id="-50" lon="12.318182825129353" lat="49.878383912368825" version="0"/>
                        <node id="-53" lon="12.317271128754717" lat="49.87713954559129" version="0"/>
                        <way id="-11" version="0">
                            <nd ref="-49"/>
                            <nd ref="-50"/>
                            <tag k="artDerGebietsgrenze" v="7101"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                        </way>
                        <way id="-12" version="0">
                            <nd ref="-50"/>
                            <nd ref="-53"/>
                            <tag k="artDerGebietsgrenze" v="7102"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                        </way>
                        <way id="-13" version="0">
                            <nd ref="-49"/>
                            <nd ref="-53"/>
                            <tag k="artDerGebietsgrenze" v="7102"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
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
                .contains("Die AGZ-Werte der angrenzenden Gebietsgrenze sind nicht gleichartig.");
    }
}
