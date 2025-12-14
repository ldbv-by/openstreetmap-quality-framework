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
 * Es ist zu prüfen, ob die OID von AX_SchutzgebietNachNaturUmweltOderBodenschutzrecht in der Relation istTeilVon bei AX_Schutzzone vorhanden ist
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_71007_R_a_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("attribute-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.71007.R.a.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createSchutzgebietNachNaturUmweltOderBodenschutzrechtMitSchutzzone() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                	<create>
                		<node id="-1" lon="12.325865715063836" lat="49.87780317275801" version="0"/>
                		<node id="-2" lon="12.326963391596292" lat="49.87778745354166" version="0"/>
                		<node id="-3" lon="12.326963391596292" lat="49.876854770878346" version="0"/>
                		<node id="-4" lon="12.325833191314729" lat="49.87686525055891" version="0"/>
                		<way id="-1" version="0">
                			<nd ref="-1"/>
                			<nd ref="-2"/>
                			<nd ref="-3"/>
                			<nd ref="-4"/>
                			<nd ref="-1"/>
                			<tag k="object_type" v="AX_Schutzzone"/>
                			<tag k="zone" v="1010" />
                		</way>
                		<relation id="-1" version="0">
                            <member type="way" role="" ref="-1"/>
                            <tag k="object_type" v="AX_SchutzgebietNachNaturUmweltOderBodenschutzrecht"/>
                            <tag k="artDerFestlegung" v="1670"/>
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
    void createSchutzgebietNachNaturUmweltOderBodenschutzrechtOhneSchutzzone() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                	<create>
                		<node id="-1" lon="12.325865715063836" lat="49.87780317275801" version="0"/>
                		<node id="-2" lon="12.326963391596292" lat="49.87778745354166" version="0"/>
                		<node id="-3" lon="12.326963391596292" lat="49.876854770878346" version="0"/>
                		<node id="-4" lon="12.325833191314729" lat="49.87686525055891" version="0"/>
                		<way id="-1" version="0">
                			<nd ref="-1"/>
                			<nd ref="-2"/>
                			<nd ref="-3"/>
                			<nd ref="-4"/>
                			<nd ref="-1"/>
                			<tag k="object_type" v="AX_Wohnbauflaeche"/>
                			<tag k="artDerBebauung" v="1000" />
                		</way>
                		<relation id="-1" version="0">
                            <member type="way" role="outer" ref="-1"/>
                            <tag k="object_type" v="AX_SchutzgebietNachNaturUmweltOderBodenschutzrecht"/>
                            <tag k="artDerFestlegung" v="1670"/>
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
                .contains("Die Objektart 'AX_Wohnbauflaeche' darf keine Relation 'AX_SchutzgebietNachNaturUmweltOderBodenschutzrecht' haben.");
    }
}