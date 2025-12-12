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
 * Die Vereinigung von geometrisch benachbarten REO's 74004 'Insel' muss vollständig von 44001 'Fließgewässer',
 * 44005 'Hafenbecken' oder 44006 'Stehendes Gewässer' oder 44007 'Meer' umgeben sein oder an
 * 75009 'Gebietsgrenze' mit AGZ 7102 grenzen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_74004_F_b_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.74004.F.b.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createInselMitMeer() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25476' changeset='-1' lat='49.8850032831' lon='12.32620924235' />
                  <node id='-25475' changeset='-1' lat='49.88018008377' lon='12.32670355475' />
                  <node id='-25474' changeset='-1' lat='49.8801345796' lon='12.31642891846' />
                  <node id='-25473' changeset='-1' lat='49.88504878269' lon='12.31642891846' />
                  <node id='-25467' changeset='-1' lat='49.88247837964' lon='12.32297647066' />
                  <node id='-25465' changeset='-1' lat='49.88331976827' lon='12.3229255957' />
                  <node id='-25464' changeset='-1' lat='49.8815679405' lon='12.32303151979' />
                  <node id='-25463' changeset='-1' lat='49.8815679405' lon='12.31943010088' />
                  <node id='-25462' changeset='-1' lat='49.88336526944' lon='12.31946540891' />
                  <way id='-724' changeset='-1'>
                    <nd ref='-25462' />
                    <nd ref='-25463' />
                    <nd ref='-25464' />
                    <nd ref='-25467' />
                    <nd ref='-25465' />
                    <nd ref='-25462' />
                    <tag k='object_type' v='AX_Insel' />
                  </way>
                  <way id='-755' changeset='-1'>
                    <nd ref='-25473' />
                    <nd ref='-25474' />
                    <nd ref='-25475' />
                    <nd ref='-25476' />
                    <nd ref='-25473' />
                  </way>
                  <relation id='-114' changeset='-1'>
                    <member type='way' ref='-755' role='outer' />
                    <member type='way' ref='-724' role='inner' />
                    <tag k='object_type' v='AX_Meer' />
                    <tag k='type' v='multipolygon' />
                    <tag k='name:unverschluesselt' v='Meer' />
                    <tag k='name:verschluesselt:land' v='09' />
                    <tag k='name:verschluesselt:kreis' v='72' />
                    <tag k='name:verschluesselt:gemeinde' v='116' />
                    <tag k='name:verschluesselt:lage' v='01' />
                  </relation>
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
    void createInselOhneMeer() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25467' changeset='-1' lat='49.88247837964' lon='12.32297647066' />
                  <node id='-25465' changeset='-1' lat='49.88331976827' lon='12.3229255957' />
                  <node id='-25464' changeset='-1' lat='49.8815679405' lon='12.32303151979' />
                  <node id='-25463' changeset='-1' lat='49.8815679405' lon='12.31943010088' />
                  <node id='-25462' changeset='-1' lat='49.88336526944' lon='12.31946540891' />
                  <way id='-724' changeset='-1'>
                    <nd ref='-25462' />
                    <nd ref='-25463' />
                    <nd ref='-25464' />
                    <nd ref='-25467' />
                    <nd ref='-25465' />
                    <nd ref='-25462' />
                    <tag k='object_type' v='AX_Insel' />
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
                .contains("Ein Objekt 'AX_Insel' muss vollständig an ein 'AX_Fliessgewaesser', 'AX_Hafenbecken', 'AX_StehendesGewaesser', 'AX_Meer', 'AX_Insel' oder an eine 'AX_Gebietsgrenze' mit 'artDerGebietsgrenze' 7102 grenzen.");
    }
}
