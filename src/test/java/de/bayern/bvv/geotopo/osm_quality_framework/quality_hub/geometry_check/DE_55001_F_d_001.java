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
 * 55001 'Gewässermerkmal' mit 'Art' 1630 'Stromschnelle' darf nur innerhalb
 * von 44001 'Fließgewässer' ohne 'Funktion 8300 'Kanal' liegen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_55001_F_d_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.55001.F.d.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createStromschnelleInnerhalbFliessgewaesser() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25366' changeset='-1' lat='49.88418770251' lon='12.32312094704' />
                  <node id='-25365' changeset='-1' lat='49.88279744114' lon='12.32304388852' />
                  <node id='-25364' changeset='-1' lat='49.88282226758' lon='12.32104036708' />
                  <node id='-25363' changeset='-1' lat='49.88416287676' lon='12.32119448412' />
                  <node id='-25361' changeset='-1' lat='49.88083611148' lon='12.31946066748' />
                  <node id='-25360' changeset='-1' lat='49.8806126638' lon='12.32512446848' />
                  <node id='-25359' changeset='-1' lat='49.88565239886' lon='12.32481623442' />
                  <node id='-25358' changeset='-1' lat='49.88585099833' lon='12.31976890155' />
                  <way id='-667' changeset='-1'>
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <nd ref='-25365' />
                    <nd ref='-25363' />
                    <tag k='object_type' v='AX_Gewaessermerkmal' />
                    <tag k='art' v='1630' />
                  </way>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25358' />
                    <nd ref='-25359' />
                    <nd ref='-25360' />
                    <nd ref='-25361' />
                    <nd ref='-25358' />
                    <tag k='object_type' v='AX_Fliessgewaesser' />
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
    void createStromschnelleOhneFliessgewaesser() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25365' changeset='-1' lat='49.88001679828' lon='12.32562534885' />
                  <node id='-25364' changeset='-1' lat='49.88004162616' lon='12.3236218274' />
                  <node id='-25363' changeset='-1' lat='49.88138231255' lon='12.32377594444' />
                  <way id='-667' changeset='-1'>
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <nd ref='-25365' />
                    <nd ref='-25363' />
                    <tag k='object_type' v='AX_Gewaessermerkmal' />
                    <tag k='art' v='1630' />
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
                .contains("Ein 'AX_Gewaessermerkmal' mit 'art' 1630 darf nur innerhalb 'AX_Fliessgewaesser' ohne 'funktion' 8300 liegen.");
    }
}
