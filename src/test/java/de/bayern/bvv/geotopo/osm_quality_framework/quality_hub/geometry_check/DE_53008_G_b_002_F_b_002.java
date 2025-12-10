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
 * Linienförmige Objekte der Werteart 1460 der Attributart 'Art' liegen immer mit dem Anfangs- bzw. Endpunkt
 * auf der Umrissgeometrie eines Objekts 44001 'Fließgewässer', 44005 'Hafenbecken', 44006 'StehendesGewässer'
 * oder 44007 Meer oder berühren deren Umrissgeometrie oder berühren bzw. kreuzen ein weiteres
 * Objekt der Werteart 1460 oder 53009 'BauwerkImGewaesserbereich' der Bauwerksfunktion 2133 Hafendamm, Mole.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_53008_G_b_002_F_b_002 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.53008.G.b.002_F.b.002"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createAnlegerStartpunktAnHafenbecken() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25497' changeset='-1' lat='49.88701683125' lon='12.33093764471' />
                  <node id='-25496' changeset='-1' lat='49.88134976967' lon='12.33118195619' />
                  <node id='-25495' changeset='-1' lat='49.88128679858' lon='12.32038338894' />
                  <node id='-25494' changeset='-1' lat='49.88685942186' lon='12.32023680205' />
                  <node id='-25364' changeset='-1' lat='49.88391203301' lon='12.32100867214' />
                  <node id='-25363' changeset='-1' lat='49.88344292461' lon='12.31757273382' />
                  <node id='-25361' changeset='-1' lat='49.88225125607' lon='12.32115595486' />
                  <node id='-25360' changeset='-1' lat='49.88220160258' lon='12.32481623442' />
                  <node id='-25359' changeset='-1' lat='49.88582617344' lon='12.32489329293' />
                  <node id='-25358' changeset='-1' lat='49.88572687375' lon='12.32084772079' />
                  <way id='-667' changeset='-1'>
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <tag k='object_type' v='AX_EinrichtungenFuerDenSchiffsverkehr' />
                    <tag k='art' v='1460' />
                  </way>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25358' />
                    <nd ref='-25359' />
                    <nd ref='-25360' />
                    <nd ref='-25361' />
                    <nd ref='-25364' />
                    <nd ref='-25358' />
                    <tag k='object_type' v='AX_Hafenbecken' />
                  </way>
                  <way id='-793' changeset='-1'>
                    <nd ref='-25494' />
                    <nd ref='-25495' />
                    <nd ref='-25496' />
                    <nd ref='-25497' />
                    <nd ref='-25494' />
                    <tag k='object_type' v='AX_Hafen' />
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
    void createAnlegerSchneidetAnleger() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25497' changeset='-1' lat='49.88701683125' lon='12.33093764471' />
                  <node id='-25496' changeset='-1' lat='49.88134976967' lon='12.33118195619' />
                  <node id='-25495' changeset='-1' lat='49.88128679858' lon='12.32038338894' />
                  <node id='-25494' changeset='-1' lat='49.88685942186' lon='12.32023680205' />
                  <node id='-25374' changeset='-1' lat='49.88287190803' lon='12.31955700989' />
                  <node id='-25373' changeset='-1' lat='49.88462213856' lon='12.31930656971' />
                  <node id='-25364' changeset='-1' lat='49.88391203301' lon='12.32100867214' />
                  <node id='-25363' changeset='-1' lat='49.88344292461' lon='12.31757273382' />
                  <node id='-25361' changeset='-1' lat='49.88225125607' lon='12.32115595486' />
                  <node id='-25360' changeset='-1' lat='49.88220160258' lon='12.32481623442' />
                  <node id='-25359' changeset='-1' lat='49.88582617344' lon='12.32489329293' />
                  <node id='-25358' changeset='-1' lat='49.88572687375' lon='12.32084772079' />
                  <way id='-700' changeset='-1'>
                    <nd ref='-25363' />
                    <nd ref='-25364' />
                    <tag k='object_type' v='AX_EinrichtungenFuerDenSchiffsverkehr' />
                    <tag k='art' v='1460' />
                  </way>
                  <way id='-667' changeset='-1'>
                    <nd ref='-25373' />
                    <nd ref='-25374' />
                    <tag k='object_type' v='AX_EinrichtungenFuerDenSchiffsverkehr' />
                    <tag k='art' v='1460' />
                  </way>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25358' />
                    <nd ref='-25359' />
                    <nd ref='-25360' />
                    <nd ref='-25361' />
                    <nd ref='-25364' />
                    <nd ref='-25358' />
                    <tag k='object_type' v='AX_Hafenbecken' />
                  </way>
                  <way id='-793' changeset='-1'>
                    <nd ref='-25494' />
                    <nd ref='-25495' />
                    <nd ref='-25496' />
                    <nd ref='-25497' />
                    <nd ref='-25494' />
                    <tag k='object_type' v='AX_Hafen' />
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
    void createAnlegerStartpunktInHafenbecken() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25497' changeset='-1' lat='49.88701683125' lon='12.33093764471' />
                  <node id='-25496' changeset='-1' lat='49.88134976967' lon='12.33118195619' />
                  <node id='-25495' changeset='-1' lat='49.88128679858' lon='12.32038338894' />
                  <node id='-25494' changeset='-1' lat='49.88685942186' lon='12.32023680205' />
                  <node id='-25371' changeset='-1' lat='49.88393943207' lon='12.32283199687' />
                  <node id='-25370' changeset='-1' lat='49.88385254146' lon='12.31953774526' />
                  <node id='-25361' changeset='-1' lat='49.88225125607' lon='12.32115595486' />
                  <node id='-25360' changeset='-1' lat='49.88220160258' lon='12.32481623442' />
                  <node id='-25359' changeset='-1' lat='49.88582617344' lon='12.32489329293' />
                  <node id='-25358' changeset='-1' lat='49.88572687375' lon='12.32084772079' />
                  <way id='-667' changeset='-1'>
                    <nd ref='-25370' />
                    <nd ref='-25371' />
                    <tag k='object_type' v='AX_EinrichtungenFuerDenSchiffsverkehr' />
                    <tag k='art' v='1460' />
                  </way>
                  <way id='-663' changeset='-1'>
                    <nd ref='-25358' />
                    <nd ref='-25359' />
                    <nd ref='-25360' />
                    <nd ref='-25361' />
                    <nd ref='-25358' />
                    <tag k='object_type' v='AX_Hafenbecken' />
                  </way>
                  <way id='-793' changeset='-1'>
                    <nd ref='-25494' />
                    <nd ref='-25495' />
                    <nd ref='-25496' />
                    <nd ref='-25497' />
                    <nd ref='-25494' />
                    <tag k='object_type' v='AX_Hafen' />
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
                .contains("Ein linienförmiger Anleger liegt immer auf der Umrissgeometrie eines Objekts 'AX_Fliessgewaesser', 'AX_Hafenbecken', 'AX_StehendesGewaesser' oder 'AX_Meer' oder berühren 'AX_BauwerkImGewaesserbereich' mit 'bauwerksfunktion' 2133 oder einen weiteren Anleger.");
    }
}