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
 * SchifffahrtslinieFährverkehr' liegt immer innerhalb eines Objektes 44001 'Fließgewässer',
 * 44005 'Hafenbecken', 44006 'StehendesGewässer' oder 44007 'Meer'.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_57002_G_b_001_F_b_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.57002.G.b.001_F.b.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createSchifffahrtslinieAufHafenbecken() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                	<create>
                		<node id="-20" lon="12.330616245215207" lat="49.87932471306288" version="0"/>
                		<node id="-26" lon="12.331190490657235" lat="49.87970510663869" version="0"/>
                		<node id="-27" lon="12.33118579739293" lat="49.879026158918776" version="0"/>
                		<node id="-28" lon="12.3301343014536" lat="49.879045445183195" version="0"/>
                		<node id="-25" lon="12.33021649328921" lat="49.87970990764771" version="0">
                            <tag k='object_type' v='AX_EinrichtungenFuerDenSchiffsverkehr' />
                            <tag k='art' v='1460' />
                		</node>
                		<way id="-6" version="0">
                			<nd ref="-25"/>
                			<nd ref="-20"/>
                			<tag k="object_type" v="AX_SchifffahrtslinieFaehrverkehr"/>
                		    <tag k='art' v='1740' />
                		</way>
                		<way id="-7" version="0">
                			<nd ref="-25"/>
                			<nd ref="-26"/>
                			<nd ref="-27"/>
                			<nd ref="-28"/>
                			<nd ref="-25"/>
                			<tag k="object_type" v="AX_Hafenbecken"/>
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
    void createSchifffahrtslinieOhneHafenbecken() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                	<create>
                		<node id="-20" lon="12.330616245215207" lat="49.87932471306288" version="0"/>
                		<node id="-26" lon="12.331190490657235" lat="49.87970510663869" version="0"/>
                		<node id="-27" lon="12.33118579739293" lat="49.879026158918776" version="0"/>
                		<node id="-28" lon="12.3301343014536" lat="49.879045445183195" version="0"/>
                		<node id="-25" lon="12.33021649328921" lat="49.87970990764771" version="0">
                            <tag k='object_type' v='AX_EinrichtungenFuerDenSchiffsverkehr' />
                            <tag k='art' v='1460' />
                		</node>
                		<way id="-6" version="0">
                			<nd ref="-25"/>
                			<nd ref="-20"/>
                			<tag k="object_type" v="AX_SchifffahrtslinieFaehrverkehr"/>
                            <tag k='art' v='1740' />
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
                .contains("Ein Objekt 'AX_SchifffahrtslinieFaehrverkehr' liegt immer innerhalb einem Objekt 'AX_Fliessgewaesser', 'AX_Hafenbecken', 'AX_StehendesGewaesser' oder 'AX_Meer'.");
    }
}
