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
 * Es ist zu prüfen, ob die OID von Strasse mit FTR 2000 in der Relation istTeilVon bei AX_Strassenachse und mehreren REO AX_Fahrbahnachse vorhanden ist.
 * Ein ZUSO AX_Strasse, das nur aus einer AX_Fahrbahnachse besteht, ist bei der Prüfung zu ignorieren, wenn die Fahrbahnachse entweder vollständig
 * auf einem oder mehreren REO AX_Gebietsgrenze, AGZ 7102 'Grenze des Bundeslandes' liegt oder die Fahrbahnachse ein (oder mehrere) REO AX_Gebietsgrenze mit
 * AGZ 7102 berührt (dabei ist ein Schnitt mit Gebietsgrenzen nur in einem oder beiden Endpunkten der Fahrbahnachse erlaubt, nicht im Innern der Fahrbahnachse).
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_42002_R_a_003 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("attribute-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.42002.R.a.003"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createStrasseMitFahrbahntrennungStrassenachseUndZweiFahrbahnachse() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-1' changeset='-1' lat='49.87494356474' lon='12.32280142285' />
                  <node id='-2' changeset='-1' lat='49.87777772641' lon='12.32285980169' />
                  <way id='-1' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <tag k='object_type' v='AX_Strassenachse' />
                    <tag k='breiteDesVerkehrsweges' v='6' />
                  </way>
                  <way id='-2' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <tag k='object_type' v='AX_Fahrbahnachse' />
                  </way>
                  <way id='-3' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <tag k='object_type' v='AX_Fahrbahnachse' />
                  </way>
                  <relation id='-3' changeset='-1'>
                    <member type='way' ref='-1' role='' />
                    <member type='way' ref='-2' role='' />
                    <member type='way' ref='-3' role='' />
                    <tag k='object_type' v='AX_Strasse' />
                    <tag k='widmung' v='1301' />
                    <tag k='fahrbahntrennung' v='2000' />
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
    void createStrasseMitFahrbahntrennungStrassenachseUndEinerFahrbahnachse() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-1' changeset='-1' lat='49.87494356474' lon='12.32280142285' />
                  <node id='-2' changeset='-1' lat='49.87777772641' lon='12.32285980169' />
                  <way id='-1' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <tag k='object_type' v='AX_Strassenachse' />
                    <tag k='breiteDesVerkehrsweges' v='6' />
                  </way>
                  <way id='-2' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <tag k='object_type' v='AX_Fahrbahnachse' />
                  </way>
                  <relation id='-3' changeset='-1'>
                    <member type='way' ref='-1' role='' />
                    <member type='way' ref='-2' role='' />
                    <tag k='object_type' v='AX_Strasse' />
                    <tag k='widmung' v='1301' />
                    <tag k='fahrbahntrennung' v='2000' />
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
                .contains("Strasse mit FTR 2000 muss aus Strassenachse und mehreren Fahrbahnachsen bestehen.");
    }

    @Test
    void createStrasseMitFahrbahntrennungStrassenachseUndEinerFahrbahnachseAufGebietsgrenze() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-1' changeset='-1' lat='49.87494356474' lon='12.32280142285' />
                  <node id='-2' changeset='-1' lat='49.87777772641' lon='12.32285980169' />
                  <way id='-1' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <tag k='object_type' v='AX_Strassenachse' />
                    <tag k='breiteDesVerkehrsweges' v='6' />
                  </way>
                  <way id='-2' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <tag k='object_type' v='AX_Fahrbahnachse' />
                  </way>
                  <relation id='-3' changeset='-1'>
                    <member type='way' ref='-1' role='' />
                    <member type='way' ref='-2' role='' />
                    <tag k='object_type' v='AX_Strasse' />
                    <tag k='widmung' v='1301' />
                    <tag k='fahrbahntrennung' v='2000' />
                  </relation>
                  <way id='-3' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <tag k='object_type' v='AX_Gebietsgrenze' />
                    <tag k='artDerGebietsgrenze' v='7102' />
                    <tag k='admin_level' v='8' />
                    <tag k='boundary' v='administrative' />
                  </way>
                  <relation id='-1' changeset='-1'>
                    <member type='way' ref='-3' role='outer' />
                    <tag k='object_type' v='AX_KommunalesGebiet' />
                    <tag k='gemeindekennzeichen:gemeinde' v='00' />
                    <tag k='gemeindekennzeichen:kreis' v='00' />
                    <tag k='gemeindekennzeichen:land' v='00' />
                    <tag k='gemeindekennzeichen:regierungsbezirk' v='00' />
                    <tag k='admin_level' v='8' />
                    <tag k='boundary' v='administrative' />
                    <tag k='schluesselGesamt' v='0000000000' />
                  </relation>
                  <relation id='-4' changeset='-1'>
                    <member type='relation' ref='-1' role='' />
                    <tag k='object_type' v='AX_Gemeinde' />
                    <tag k='gemeindekennzeichen:gemeinde' v='00' />
                    <tag k='gemeindekennzeichen:kreis' v='00' />
                    <tag k='gemeindekennzeichen:land' v='00' />
                    <tag k='gemeindekennzeichen:gemeindeteil' v='00' />
                    <tag k='gemeindekennzeichen:regierungsbezirk' v='00' />
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