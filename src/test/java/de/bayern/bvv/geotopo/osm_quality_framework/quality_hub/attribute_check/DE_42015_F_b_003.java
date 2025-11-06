package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.attribute_check;

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
 * Die Attributart 'Name' muss immer belegt sein. Objekte mit Funktion 1200 'Parken' sind von der Prüfung auszuschließen.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_42015_F_b_003 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createInternationalenFlughafenMitName() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25361' changeset='-1' lat='49.87977158487' lon='12.31859812646' />
                  <node id='-25360' changeset='-1' lat='49.87977158487' lon='12.32451384954' />
                  <node id='-25359' changeset='-1' lat='49.88413518675' lon='12.32447493031' />
                  <node id='-25358' changeset='-1' lat='49.8841101097' lon='12.31855920723' />
                  <way id='-663' changeset='-1'>
                    <nd ref='-25358' />
                    <nd ref='-25359' />
                    <nd ref='-25360' />
                    <nd ref='-25361' />
                    <nd ref='-25358' />
                    <tag k='identifikator:UUID' v='DEBYBDLM12345678' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1234567820251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_Flugverkehr' />
                    <tag k='art' v='5511' />
                    <tag k='nutzung' v='1000' />
                    <tag k='name:unverschluesselt' v='Flughafen' />
                    <tag k='name:verschluesselt:land' v='09' />
                    <tag k='name:verschluesselt:kreis' v='72' />
                    <tag k='name:verschluesselt:gemeinde' v='116' />
                    <tag k='name:verschluesselt:lage' v='01' />
                  </way>
                  <relation id='-70' changeset='-1'>
                    <member type='way' ref='-663' role='' />
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
    void createFlughafenOhneNameUndMitFunktionParken() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25497' changeset='-1' lat='49.88701683125' lon='12.33093764471' />
                  <node id='-25496' changeset='-1' lat='49.88134976967' lon='12.33118195619' />
                  <node id='-25495' changeset='-1' lat='49.88128679858' lon='12.32038338894' />
                  <node id='-25494' changeset='-1' lat='49.88685942186' lon='12.32023680205' />
                  <node id='-25361' changeset='-1' lat='49.88225125607' lon='12.32115595486' />
                  <node id='-25360' changeset='-1' lat='49.88220160258' lon='12.32481623442' />
                  <node id='-25359' changeset='-1' lat='49.88582617344' lon='12.32489329293' />
                  <node id='-25358' changeset='-1' lat='49.88572687375' lon='12.32084772079' />
                  <way id='-663' changeset='-1'>
                    <nd ref='-25358' />
                    <nd ref='-25359' />
                    <nd ref='-25360' />
                    <nd ref='-25361' />
                    <nd ref='-25358' />
                    <tag k='identifikator:UUID' v='DEBYBDLM12345678' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1234567820251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_Flugverkehr' />
                    <tag k='istWeitereNutzung' v='1000' />
                    <tag k='funktion' v='1200' />
                  </way>
                  <way id='-793' changeset='-1'>
                    <nd ref='-25494' />
                    <nd ref='-25495' />
                    <nd ref='-25496' />
                    <nd ref='-25497' />
                    <nd ref='-25494' />
                    <tag k='identifikator:UUID' v='DEBYBDLM12345677' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1234567720251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_Flugverkehr' />
                    <tag k='art' v='5511' />
                    <tag k='nutzung' v='1000' />
                    <tag k='name:unverschluesselt' v='Flughafen' />
                    <tag k='name:verschluesselt:land' v='09' />
                    <tag k='name:verschluesselt:kreis' v='72' />
                    <tag k='name:verschluesselt:gemeinde' v='116' />
                    <tag k='name:verschluesselt:lage' v='01' />
                  </way>
                  <relation id='-70' changeset='-1'>
                    <member type='way' ref='-663' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-65' changeset='-1'>
                    <member type='way' ref='-793' role='' />
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
    void createFlughafenOhneNameUndOhneFunktion() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25361' changeset='-1' lat='49.87977158487' lon='12.31859812646' />
                  <node id='-25360' changeset='-1' lat='49.87977158487' lon='12.32451384954' />
                  <node id='-25359' changeset='-1' lat='49.88413518675' lon='12.32447493031' />
                  <node id='-25358' changeset='-1' lat='49.8841101097' lon='12.31855920723' />
                  <way id='-663' changeset='-1'>
                    <nd ref='-25358' />
                    <nd ref='-25359' />
                    <nd ref='-25360' />
                    <nd ref='-25361' />
                    <nd ref='-25358' />
                    <tag k='identifikator:UUID' v='DEBYBDLM12345678' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1234567820251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_Flugverkehr' />
                    <tag k='art' v='5511' />
                  </way>
                  <relation id='-70' changeset='-1'>
                    <member type='way' ref='-663' role='' />
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
                .contains("Das komplexe Tag 'name' muss belegt sein, außer bei der 'funktion' 1200.");
    }
}