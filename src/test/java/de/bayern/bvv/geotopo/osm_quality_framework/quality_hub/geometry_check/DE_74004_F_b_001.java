package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.geometry_check;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.config.JacksonConfiguration;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.dto.QualityHubResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceErrorDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
@Import(JacksonConfiguration.class)
class DE_74004_F_b_001 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createInselMitMeer() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
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
                  <way id='-755' changeset='-1'>
                    <nd ref='-25473' />
                    <nd ref='-25474' />
                    <nd ref='-25475' />
                    <nd ref='-25476' />
                    <nd ref='-25473' />
                  </way>
                  <way id='-724' changeset='-1'>
                    <nd ref='-25462' />
                    <nd ref='-25463' />
                    <nd ref='-25464' />
                    <nd ref='-25467' />
                    <nd ref='-25465' />
                    <nd ref='-25462' />
                    <tag k='object_type' v='AX_Insel' />
                    <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                  </way>
                  <relation id='-114' changeset='-1'>
                    <member type='way' ref='-755' role='outer' />
                    <member type='way' ref='-724' role='inner' />
                    <tag k='object_type' v='AX_Meer' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='type' v='multipolygon' />
                    <tag k='name:unverschluesselt' v='Meer' />
                    <tag k='name:verschluesselt:land' v='09' />
                    <tag k='name:verschluesselt:kreis' v='72' />
                    <tag k='name:verschluesselt:gemeinde' v='116' />
                    <tag k='name:verschluesselt:lage' v='01' />
                  </relation>
                  <relation id='-60' changeset='-1'>
                    <member type='way' ref='-724' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-50' changeset='-1'>
                    <member type='relation' ref='-114' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                </create>
                </osmChange>
            """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML))
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
        final Long CHANGESET_ID = 1L;
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
                    <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                  </way>
                  <relation id='-60' changeset='-1'>
                    <member type='way' ref='-724' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                </create>
                </osmChange>
            """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML))
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
