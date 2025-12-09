package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.attribute_check;

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
 * Wenn 'Gemeinde' Bestandteil einer 'Verwaltungsgemeinschaft' ist, darf 'Gemeindeteil' nicht belegt sein.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_73005_A_b_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("attribute-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.73005.A.b.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createGemeindeMitIstTeilVonVerwaltungsgemeinschaftOhneGemeindeteil() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-1' changeset='-1' lat='49.87977158487' lon='12.31859812646' />
                  <node id='-2' changeset='-1' lat='49.87977158487' lon='12.32451384954' />
                  <node id='-3' changeset='-1' lat='49.88413518675' lon='12.32447493031' />
                  <node id='-4' changeset='-1' lat='49.8841101097' lon='12.31855920723' />
                  <way id='-1' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <nd ref='-3' />
                    <nd ref='-4' />
                    <nd ref='-1' />
                    <tag k='object_type' v='AX_Gebietsgrenze' />
                    <tag k='artDerGebietsgrenze' v='7106' />
                    <tag k='admin_level' v='8' />
                    <tag k='boundary' v='administrative' />
                  </way>
                  <relation id='-1' changeset='-1'>
                    <member type='way' ref='-1' role='outer' />
                    <tag k='object_type' v='AX_KommunalesGebiet' />
                    <tag k='gemeindekennzeichen:gemeinde' v='00' />
                    <tag k='gemeindekennzeichen:kreis' v='00' />
                    <tag k='gemeindekennzeichen:land' v='00' />
                    <tag k='gemeindekennzeichen:regierungsbezirk' v='00' />
                    <tag k='admin_level' v='8' />
                    <tag k='boundary' v='administrative' />
                    <tag k='schluesselGesamt' v='0000000000' />
                  </relation>
                  <relation id='-2' changeset='-1'>
                    <member type='relation' ref='-1' role='' />
                    <tag k='object_type' v='AX_Gemeinde' />
                    <tag k='gemeindekennzeichen:gemeinde' v='00' />
                    <tag k='gemeindekennzeichen:kreis' v='00' />
                    <tag k='gemeindekennzeichen:land' v='00' />
                    <tag k='gemeindekennzeichen:regierungsbezirk' v='00' />
                    <tag k='istTeilVonVerwaltungsgemeinschaft:rolle' v='5000' />
                    <tag k='istTeilVonVerwaltungsgemeinschaft:schluessel:kreis' v='00' />
                    <tag k='istTeilVonVerwaltungsgemeinschaft:schluessel:land' v='00' />
                    <tag k='istTeilVonVerwaltungsgemeinschaft:schluessel:regierungsbezirk' v='00' />
                    <tag k='istTeilVonVerwaltungsgemeinschaft:schluessel:verwaltungsgemeinschaft' v='00' />
                    <tag k='bezeichnung' v='Test' />
                    <tag k='schluesselGesamt' v='00000000' />
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
    void createGemeindeMitIstTeilVonVerwaltungsgemeinschaftMitGemeindeteil() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-1' changeset='-1' lat='49.87977158487' lon='12.31859812646' />
                  <node id='-2' changeset='-1' lat='49.87977158487' lon='12.32451384954' />
                  <node id='-3' changeset='-1' lat='49.88413518675' lon='12.32447493031' />
                  <node id='-4' changeset='-1' lat='49.8841101097' lon='12.31855920723' />
                  <way id='-1' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <nd ref='-3' />
                    <nd ref='-4' />
                    <nd ref='-1' />
                    <tag k='object_type' v='AX_Gebietsgrenze' />
                    <tag k='artDerGebietsgrenze' v='7106' />
                    <tag k='admin_level' v='8' />
                    <tag k='boundary' v='administrative' />
                  </way>
                  <relation id='-1' changeset='-1'>
                    <member type='way' ref='-1' role='outer' />
                    <tag k='object_type' v='AX_KommunalesGebiet' />
                    <tag k='gemeindekennzeichen:gemeinde' v='00' />
                    <tag k='gemeindekennzeichen:kreis' v='00' />
                    <tag k='gemeindekennzeichen:land' v='00' />
                    <tag k='gemeindekennzeichen:regierungsbezirk' v='00' />
                    <tag k='gemeindekennzeichen:gemeindeteil' v='00' />
                    <tag k='admin_level' v='8' />
                    <tag k='boundary' v='administrative' />
                    <tag k='schluesselGesamt' v='0000000000' />
                  </relation>
                  <relation id='-2' changeset='-1'>
                    <member type='relation' ref='-1' role='' />
                    <tag k='object_type' v='AX_Gemeinde' />
                    <tag k='gemeindekennzeichen:gemeinde' v='00' />
                    <tag k='gemeindekennzeichen:kreis' v='00' />
                    <tag k='gemeindekennzeichen:land' v='00' />
                    <tag k='gemeindekennzeichen:regierungsbezirk' v='00' />
                    <tag k='gemeindekennzeichen:gemeindeteil' v='00' />
                    <tag k='istTeilVonVerwaltungsgemeinschaft:rolle' v='5000' />
                    <tag k='istTeilVonVerwaltungsgemeinschaft:schluessel:kreis' v='00' />
                    <tag k='istTeilVonVerwaltungsgemeinschaft:schluessel:land' v='00' />
                    <tag k='istTeilVonVerwaltungsgemeinschaft:schluessel:regierungsbezirk' v='00' />
                    <tag k='istTeilVonVerwaltungsgemeinschaft:schluessel:verwaltungsgemeinschaft' v='00' />
                    <tag k='bezeichnung' v='Test' />
                    <tag k='schluesselGesamt' v='0000000000' />
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
                .contains("Das Tag 'gemeindekennzeichen:gemeindeteil' darf nicht belegt sein, da die Gemeinde ein Teil einer Verwaltungsgemeinschaft ist.");
    }

    @Test
    void createGemeindeOhneIstTeilVonVerwaltungsgemeinschaftMitGemeindeteil() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-1' changeset='-1' lat='49.87977158487' lon='12.31859812646' />
                  <node id='-2' changeset='-1' lat='49.87977158487' lon='12.32451384954' />
                  <node id='-3' changeset='-1' lat='49.88413518675' lon='12.32447493031' />
                  <node id='-4' changeset='-1' lat='49.8841101097' lon='12.31855920723' />
                  <way id='-1' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <nd ref='-3' />
                    <nd ref='-4' />
                    <nd ref='-1' />
                    <tag k='object_type' v='AX_Gebietsgrenze' />
                    <tag k='artDerGebietsgrenze' v='7106' />
                    <tag k='admin_level' v='8' />
                    <tag k='boundary' v='administrative' />
                  </way>
                  <relation id='-1' changeset='-1'>
                    <member type='way' ref='-1' role='outer' />
                    <tag k='object_type' v='AX_KommunalesGebiet' />
                    <tag k='gemeindekennzeichen:gemeinde' v='00' />
                    <tag k='gemeindekennzeichen:kreis' v='00' />
                    <tag k='gemeindekennzeichen:land' v='00' />
                    <tag k='gemeindekennzeichen:regierungsbezirk' v='00' />
                    <tag k='gemeindekennzeichen:gemeindeteil' v='00' />
                    <tag k='admin_level' v='8' />
                    <tag k='boundary' v='administrative' />
                    <tag k='schluesselGesamt' v='0000000000' />
                  </relation>
                  <relation id='-2' changeset='-1'>
                    <member type='relation' ref='-1' role='' />
                    <tag k='object_type' v='AX_Gemeinde' />
                    <tag k='gemeindekennzeichen:gemeinde' v='00' />
                    <tag k='gemeindekennzeichen:kreis' v='00' />
                    <tag k='gemeindekennzeichen:land' v='00' />
                    <tag k='gemeindekennzeichen:regierungsbezirk' v='00' />
                    <tag k='gemeindekennzeichen:gemeindeteil' v='00' />
                    <tag k='bezeichnung' v='Test' />
                    <tag k='schluesselGesamt' v='0000000000' />
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
}