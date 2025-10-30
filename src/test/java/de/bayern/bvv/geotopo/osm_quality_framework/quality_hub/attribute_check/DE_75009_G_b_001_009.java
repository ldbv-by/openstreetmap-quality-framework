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
 * Die 'Gebietsgrenze' liegt auf dem Rand eines Gebiets.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_75009_G_b_001_009 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createGrenzlinieMitKommunalenGebiet() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-24" lon="12.328489371106599" lat="49.87960236551823" version="0"/>
                    <node id="-25" lon="12.328473277852508" lat="49.8787727242272" version="0"/>
                    <node id="-27" lon="12.329750009343597" lat="49.878800379166556" version="0"/>
                    <node id="-30" lon="12.329744644925567" lat="49.8796611312357" version="0"/>
                    <way id="-5" version="0">
                        <nd ref="-24"/>
                        <nd ref="-25"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-6" version="0">
                        <nd ref="-27"/>
                        <nd ref="-25"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-7" version="0">
                        <nd ref="-30"/>
                        <nd ref="-27"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM22222222' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM2222222220251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-8" version="0">
                        <nd ref="-24"/>
                        <nd ref="-30"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM33333333' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM3333333320251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-9" version="0">
                        <nd ref="-24"/>
                        <nd ref="-30"/>
                        <nd ref="-27"/>
                        <nd ref="-25"/>
                        <nd ref="-24"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM55555555' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM5555555520251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <relation id="-1" version="0">
                        <member type="way" role="outer" ref="-5"/>
                        <member type="way" role="outer" ref="-6"/>
                        <member type="way" role="outer" ref="-7"/>
                        <member type="way" role="outer" ref="-8"/>
                        <tag k="object_type" v="AX_KommunalesGebiet"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="type" v="boundary"/>
                        <tag k="schluesselGesamt" v="09172116"/>
                        <tag k="gemeindekennzeichen:land" v="09"/>
                        <tag k="gemeindekennzeichen:regierungsbezirk" v="1"/>
                        <tag k="gemeindekennzeichen:kreis" v="72"/>
                        <tag k="gemeindekennzeichen:gemeinde" v="116"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM44444444' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM4444444420251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </relation>
                    <relation id='-2' changeset='-1'>
                        <member type='way' ref='-5' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-3' changeset='-1'>
                        <member type='way' ref='-6' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-4' changeset='-1'>
                        <member type='way' ref='-7' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-5' changeset='-1'>
                        <member type='way' ref='-8' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-6' changeset='-1'>
                        <member type='relation' ref='-1' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-7' changeset='-1'>
                        <member type='way' ref='-9' role='' />
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
    void createGrenzlinieOhneKommunalesGebiet() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-24" lon="12.328489371106599" lat="49.87960236551823" version="0"/>
                    <node id="-25" lon="12.328473277852508" lat="49.8787727242272" version="0"/>
                    <way id="-5" version="0">
                        <nd ref="-24"/>
                        <nd ref="-25"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <relation id='-1' changeset='-1'>
                        <member type='way' ref='-5' role='' />
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
                .as("Error text of 'attribute-check'")
                .contains("Die Objektart 'AX_Gebietsgrenze' erwartet mindestens 1 Relation/en 'AX_KommunalesGebiet'.");
    }
}
