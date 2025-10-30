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
 * Die Positionen der Knoten der Kanten jeweils benachbarter Objekte AX_KommunalesGebiet müssen identisch sein.
 * Zusammenhängende Objekte der Objektart AX_KommunalesGebiet müssen vollständig durch Objekte der Objektart
 * AX_Gebietsgrenze mit AGZ (Basis-DLM) '7102','7104','7105','7106' oder '7107' oder durch
 * AX_BesondereFlurstuecksgrenze mit ARF (DLKM) '7102','7104','7105','7106' oder '7107' umschlossen sein.
 *
 * Lückenlose und überschneidungsfreie Flächendeckung der Objekte der Objektart AX_KommunalesGebiet.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_75003_G_a_003 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createKommunalesGebietMitBundesrepublikGrenze() throws Exception {
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
    void createKommunalesGebietMitGemeindeGrenze() throws Exception {
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
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-6" version="0">
                        <nd ref="-27"/>
                        <nd ref="-25"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-7" version="0">
                        <nd ref="-30"/>
                        <nd ref="-27"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM22222222' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM2222222220251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-8" version="0">
                        <nd ref="-24"/>
                        <nd ref="-30"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM33333333' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM3333333320251014T125300Z' />
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
                .contains("Die Members von 'AX_KommunalesGebiet' dürfen nur 'AX_Gebietsgrenze' mit 'artDerGebietsgrenze' 7101, 7102, 7104, 7105, 7106 oder 7107 sein. Zudem muss 'AX_KommunalesGebiet' muss lückenlos und flächendeckend sein.");
    }

    @Test
    void createKommunalesGebietMitLandkreisRegierungsbezirkBundesland() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-24" lon="12.32726480928593" lat="49.87981218425693" version="0"/>
                    <node id="-25" lon="12.327319499952107" lat="49.878070268874175" version="0"/>
                    <node id="-27" lon="12.330740984227374" lat="49.87807055563436" version="0"/>
                    <node id="-30" lon="12.330643600712994" lat="49.8798390208782" version="0"/>
                    <node id="-39" lon="12.3281944363723" lat="49.879819567978465" version="0"/>
                    <node id="-49" lon="12.328358411356591" lat="49.879320606607635" version="0"/>
                    <node id="-50" lon="12.328377710062638" lat="49.8786004337551" version="0"/>
                    <node id="-52" lon="12.329446547258716" lat="49.87860955000456" version="0"/>
                    <node id="-53" lon="12.329406013376586" lat="49.879338845345494" version="0"/>
                    <node id="-58" lon="12.329114696460762" lat="49.87860671960674" version="0"/>
                    <node id="-59" lon="12.329427076243128" lat="49.8789598785675" version="0"/>
                    <node id="-60" lon="12.330691253433757" lat="49.87897366504399" version="0"/>
                    <node id="-65" lon="12.327291413642298" lat="49.87896483476299" version="0"/>
                    <node id="-66" lon="12.328368055644779" lat="49.87896071052936" version="0"/>
                    <node id="-76" lon="12.328574761569495" lat="49.87880309570676" version="0"/>
                    <node id="-77" lon="12.329074472421212" lat="49.87880319814299" version="0"/>
                    <node id="-78" lon="12.32856768317747" lat="49.87900835657607" version="0"/>
                    <node id="-79" lon="12.329067394029183" lat="49.87900845901185" version="0"/>
                    <way id="-6" version="0">
                        <nd ref="-27"/>
                        <nd ref="-25"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-8" version="0">
                        <nd ref="-24"/>
                        <nd ref="-30"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000001' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000120251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-16" version="0">
                        <nd ref="-50"/>
                        <nd ref="-58"/>
                        <nd ref="-52"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000002' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000220251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-18" version="0">
                        <nd ref="-49"/>
                        <nd ref="-53"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000003' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000320251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-19" version="0">
                        <nd ref="-59"/>
                        <nd ref="-60"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000004' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000420251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-20" version="0">
                        <nd ref="-65"/>
                        <nd ref="-66"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000005' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000520251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-22" version="0">
                        <nd ref="-76"/>
                        <nd ref="-77"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000006' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000620251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-23" version="0">
                        <nd ref="-78"/>
                        <nd ref="-79"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000007' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000720251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-24" version="0">
                        <nd ref="-78"/>
                        <nd ref="-76"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000008' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000820251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-25" version="0">
                        <nd ref="-79"/>
                        <nd ref="-77"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000009' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000920251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-26" version="0">
                        <nd ref="-24"/>
                        <nd ref="-65"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000010' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001020251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-27" version="0">
                        <nd ref="-65"/>
                        <nd ref="-25"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000011' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001120251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-28" version="0">
                        <nd ref="-60"/>
                        <nd ref="-27"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000012' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001220251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-29" version="0">
                        <nd ref="-30"/>
                        <nd ref="-60"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000013' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001320251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-30" version="0">
                        <nd ref="-50"/>
                        <nd ref="-66"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000014' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001420251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-31" version="0">
                        <nd ref="-49"/>
                        <nd ref="-66"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000015' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001520251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-32" version="0">
                        <nd ref="-59"/>
                        <nd ref="-52"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000016' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001620251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-33" version="0">
                        <nd ref="-53"/>
                        <nd ref="-59"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000017' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001720251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-50" version="0">
                        <nd ref="-65"/>
                        <nd ref="-25"/>
                        <nd ref="-27"/>
                        <nd ref="-60"/>
                        <nd ref="-30"/>
                        <nd ref="-24"/>
                        <nd ref="-65"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM11112222' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111222220251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <relation id="-2" version="0">
                        <member type="way" role="outer" ref="-8"/>
                        <member type="way" role="outer" ref="-26"/>
                        <member type="way" role="outer" ref="-20"/>
                        <member type="way" role="outer" ref="-31"/>
                        <member type="way" role="outer" ref="-18"/>
                        <member type="way" role="outer" ref="-33"/>
                        <member type="way" role="outer" ref="-19"/>
                        <member type="way" role="outer" ref="-29"/>
                        <tag k="admin_level" v="6"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="object_type" v="AX_Gebiet_Kreis"/>
                        <tag k="kreis:land" v="09"/>
                        <tag k="kreis:regierungsbezirk" v="2"/>
                        <tag k="kreis:kreis" v="03"/>
                        <tag k="type" v="boundary"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000222' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000022220251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k="schluesselGesamt" v="09203"/>
                    </relation>
                    <relation id="-1" version="0">
                        <member type="way" role="outer" ref="-27"/>
                        <member type="way" role="outer" ref="-6"/>
                        <member type="way" role="outer" ref="-28"/>
                        <member type="way" role="outer" ref="-29"/>
                        <member type="way" role="outer" ref="-8"/>
                        <member type="way" role="outer" ref="-26"/>
                        <tag k="object_type" v="AX_Gebiet_Bundesland"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="type" v="boundary"/>
                        <tag k="land:land" v="09"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000018' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001820251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k="schluesselGesamt" v="09"/>
                    </relation>
                    <relation id="-3" version="0">
                        <member type="way" role="outer" ref="-6"/>
                        <member type="way" role="outer" ref="-28"/>
                        <member type="way" role="outer" ref="-19"/>
                        <member type="way" role="outer" ref="-33"/>
                        <member type="way" role="outer" ref="-18"/>
                        <member type="way" role="outer" ref="-31"/>
                        <member type="way" role="outer" ref="-20"/>
                        <member type="way" role="outer" ref="-27"/>
                        <tag k="admin_level" v="5"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="object_type" v="AX_Gebiet_Regierungsbezirk"/>
                        <tag k="regierungsbezirk:land" v="09"/>0
                        <tag k="regierungsbezirk:regierungsbezirk" v="1"/>
                        <tag k="type" v="boundary"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000020' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000002020251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k="schluesselGesamt" v="091"/>
                    </relation>
                    <relation id="-4" version="0">
                        <member type="way" role="outer" ref="-26"/>
                        <member type="way" role="outer" ref="-8"/>
                        <member type="way" role="outer" ref="-29"/>
                        <member type="way" role="outer" ref="-19"/>
                        <member type="way" role="outer" ref="-33"/>
                        <member type="way" role="outer" ref="-18"/>
                        <member type="way" role="outer" ref="-31"/>
                        <member type="way" role="outer" ref="-20"/>
                        <tag k="admin_level" v="5"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="object_type" v="AX_Gebiet_Regierungsbezirk"/>
                        <tag k="regierungsbezirk:land" v="09"/>
                        <tag k="regierungsbezirk:regierungsbezirk" v="2"/>
                        <tag k="type" v="boundary"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000021' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000002120251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k="schluesselGesamt" v="092"/>
                    </relation>
                    <relation id="-5" version="0">
                        <member type="way" role="outer" ref="-18"/>
                        <member type="way" role="outer" ref="-31"/>
                        <member type="way" role="outer" ref="-30"/>
                        <member type="way" role="outer" ref="-16"/>
                        <member type="way" role="outer" ref="-32"/>
                        <member type="way" role="outer" ref="-33"/>
                        <tag k="admin_level" v="6"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="object_type" v="AX_Gebiet_Kreis"/>
                        <tag k="kreis:land" v="09"/>
                        <tag k="kreis:regierungsbezirk" v="1"/>
                        <tag k="kreis:kreis" v="02"/>
                        <tag k="type" v="boundary"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000022' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000002220251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k="schluesselGesamt" v="09102"/>
                    </relation>
                    <relation id="-6" version="0">
                        <member type="way" role="outer" ref="-19"/>
                        <member type="way" role="outer" ref="-32"/>
                        <member type="way" role="outer" ref="-16"/>
                        <member type="way" role="outer" ref="-30"/>
                        <member type="way" role="outer" ref="-20"/>
                        <member type="way" role="outer" ref="-27"/>
                        <member type="way" role="outer" ref="-6"/>
                        <member type="way" role="outer" ref="-28"/>
                        <tag k="admin_level" v="6"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="object_type" v="AX_Gebiet_Kreis"/>
                        <tag k="kreis:kreis" v="01"/>
                        <tag k="kreis:land" v="09"/>
                        <tag k="kreis:regierungsbezirk" v="1"/>
                        <tag k="type" v="boundary"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000023' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000002320251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k="schluesselGesamt" v="09101"/>
                    </relation>
                    <relation id="-7" version="0">
                        <member type="way" role="outer" ref="-26"/>
                        <member type="way" role="outer" ref="-8"/>
                        <member type="way" role="outer" ref="-29"/>
                        <member type="way" role="outer" ref="-19"/>
                        <member type="way" role="outer" ref="-33"/>
                        <member type="way" role="outer" ref="-18"/>
                        <member type="way" role="outer" ref="-31"/>
                        <member type="way" role="outer" ref="-20"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="object_type" v="AX_KommunalesGebiet"/>
                        <tag k="type" v="boundary"/>
                        <tag k="gemeindekennzeichen:gemeinde" v="001"/>
                        <tag k="gemeindekennzeichen:kreis" v="03"/>
                        <tag k="gemeindekennzeichen:land" v="09"/>
                        <tag k="gemeindekennzeichen:regierungsbezirk" v="2"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM00000024"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM0000002420251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="schluesselGesamt" v="0901001"/>
                    </relation>
                    <relation id="-8" version="0">
                        <member type="way" role="outer" ref="-23"/>
                        <member type="way" role="outer" ref="-25"/>
                        <member type="way" role="outer" ref="-22"/>
                        <member type="way" role="outer" ref="-24"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="gemeindekennzeichen:gemeinde" v="002"/>
                        <tag k="gemeindekennzeichen:kreis" v="02"/>
                        <tag k="gemeindekennzeichen:land" v="09"/>
                        <tag k="gemeindekennzeichen:regierungsbezirk" v="1"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM00000025"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM0000002520251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="object_type" v="AX_KommunalesGebiet"/>
                        <tag k="schluesselGesamt" v="09102002"/>
                        <tag k="type" v="boundary"/>
                    </relation>
                    <relation id="-9" version="0">
                        <member type="way" role="outer" ref="-18"/>
                        <member type="way" role="outer" ref="-33"/>
                        <member type="way" role="outer" ref="-32"/>
                        <member type="way" role="outer" ref="-16"/>
                        <member type="way" role="outer" ref="-30"/>
                        <member type="way" role="outer" ref="-31"/>
                        <member type="way" role="inner" ref="-23"/>
                        <member type="way" role="inner" ref="-25"/>
                        <member type="way" role="inner" ref="-22"/>
                        <member type="way" role="inner" ref="-24"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="gemeindekennzeichen:gemeinde" v="001"/>
                        <tag k="gemeindekennzeichen:kreis" v="02"/>
                        <tag k="gemeindekennzeichen:land" v="09"/>
                        <tag k="gemeindekennzeichen:regierungsbezirk" v="1"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM00000026"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM0000002620251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="object_type" v="AX_KommunalesGebiet"/>
                        <tag k="schluesselGesamt" v="09102001"/>
                        <tag k="type" v="boundary"/>
                    </relation>
                    <relation id="-10" version="0">
                        <member type="way" role="outer" ref="-6"/>
                        <member type="way" role="outer" ref="-27"/>
                        <member type="way" role="outer" ref="-20"/>
                        <member type="way" role="outer" ref="-30"/>
                        <member type="way" role="outer" ref="-16"/>
                        <member type="way" role="outer" ref="-32"/>
                        <member type="way" role="outer" ref="-19"/>
                        <member type="way" role="outer" ref="-28"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="gemeindekennzeichen:gemeinde" v="001"/>
                        <tag k="gemeindekennzeichen:kreis" v="01"/>
                        <tag k="gemeindekennzeichen:land" v="09"/>
                        <tag k="gemeindekennzeichen:regierungsbezirk" v="1"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM00000027"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM0000002720251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="object_type" v="AX_KommunalesGebiet"/>
                        <tag k="schluesselGesamt" v="09101001"/>
                        <tag k="type" v="boundary"/>
                    </relation>
                    <relation id='-11' changeset='-1'>
                        <member type='way' ref='-6' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-12' changeset='-1'>
                        <member type='way' ref='-8' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-13' changeset='-1'>
                        <member type='way' ref='-16' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-14' changeset='-1'>
                        <member type='way' ref='-18' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-15' changeset='-1'>
                        <member type='way' ref='-19' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-16' changeset='-1'>
                        <member type='way' ref='-20' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-17' changeset='-1'>
                        <member type='way' ref='-21' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-18' changeset='-1'>
                        <member type='way' ref='-22' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-19' changeset='-1'>
                        <member type='way' ref='-23' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-20' changeset='-1'>
                        <member type='way' ref='-24' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-21' changeset='-1'>
                        <member type='way' ref='-25' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-22' changeset='-1'>
                        <member type='way' ref='-26' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-23' changeset='-1'>
                        <member type='way' ref='-27' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-24' changeset='-1'>
                        <member type='way' ref='-28' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-25' changeset='-1'>
                        <member type='way' ref='-29' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-26' changeset='-1'>
                        <member type='way' ref='-30' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-27' changeset='-1'>
                        <member type='way' ref='-31' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-28' changeset='-1'>
                        <member type='way' ref='-32' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-29' changeset='-1'>
                        <member type='relation' ref='-1' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-30' changeset='-1'>
                        <member type='relation' ref='-2' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-31' changeset='-1'>
                        <member type='relation' ref='-3' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-33' changeset='-1'>
                        <member type='relation' ref='-4' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-34' changeset='-1'>
                        <member type='relation' ref='-5' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-35' changeset='-1'>
                        <member type='relation' ref='-6' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-36' changeset='-1'>
                        <member type='relation' ref='-7' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-37' changeset='-1'>
                        <member type='relation' ref='-8' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-38' changeset='-1'>
                        <member type='relation' ref='-9' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-39' changeset='-1'>
                        <member type='relation' ref='-10' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-40' changeset='-1'>
                        <member type='way' ref='-33' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-41' changeset='-1'>
                        <member type='way' ref='-50' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
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
    void createFehlendesInnerKommunalesGebietMitLandkreisRegierungsbezirkBundesland() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-24" lon="12.32726480928593" lat="49.87981218425693" version="0"/>
                    <node id="-25" lon="12.327319499952107" lat="49.878070268874175" version="0"/>
                    <node id="-27" lon="12.330740984227374" lat="49.87807055563436" version="0"/>
                    <node id="-30" lon="12.330643600712994" lat="49.8798390208782" version="0"/>
                    <node id="-39" lon="12.3281944363723" lat="49.879819567978465" version="0"/>
                    <node id="-49" lon="12.328358411356591" lat="49.879320606607635" version="0"/>
                    <node id="-50" lon="12.328377710062638" lat="49.8786004337551" version="0"/>
                    <node id="-52" lon="12.329446547258716" lat="49.87860955000456" version="0"/>
                    <node id="-53" lon="12.329406013376586" lat="49.879338845345494" version="0"/>
                    <node id="-58" lon="12.329114696460762" lat="49.87860671960674" version="0"/>
                    <node id="-59" lon="12.329427076243128" lat="49.8789598785675" version="0"/>
                    <node id="-60" lon="12.330691253433757" lat="49.87897366504399" version="0"/>
                    <node id="-65" lon="12.327291413642298" lat="49.87896483476299" version="0"/>
                    <node id="-66" lon="12.328368055644779" lat="49.87896071052936" version="0"/>
                    <node id="-76" lon="12.328574761569495" lat="49.87880309570676" version="0"/>
                    <node id="-77" lon="12.329074472421212" lat="49.87880319814299" version="0"/>
                    <node id="-78" lon="12.32856768317747" lat="49.87900835657607" version="0"/>
                    <node id="-79" lon="12.329067394029183" lat="49.87900845901185" version="0"/>
                    <way id="-6" version="0">
                        <nd ref="-27"/>
                        <nd ref="-25"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-8" version="0">
                        <nd ref="-24"/>
                        <nd ref="-30"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000001' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000120251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-16" version="0">
                        <nd ref="-50"/>
                        <nd ref="-58"/>
                        <nd ref="-52"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000002' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000220251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-18" version="0">
                        <nd ref="-49"/>
                        <nd ref="-53"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000003' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000320251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-19" version="0">
                        <nd ref="-59"/>
                        <nd ref="-60"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000004' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000420251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-20" version="0">
                        <nd ref="-65"/>
                        <nd ref="-66"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000005' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000520251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-22" version="0">
                        <nd ref="-76"/>
                        <nd ref="-77"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000006' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000620251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-23" version="0">
                        <nd ref="-78"/>
                        <nd ref="-79"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000007' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000720251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-24" version="0">
                        <nd ref="-78"/>
                        <nd ref="-76"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000008' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000820251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-25" version="0">
                        <nd ref="-79"/>
                        <nd ref="-77"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000009' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000920251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-26" version="0">
                        <nd ref="-24"/>
                        <nd ref="-65"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000010' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001020251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-27" version="0">
                        <nd ref="-65"/>
                        <nd ref="-25"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000011' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001120251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-28" version="0">
                        <nd ref="-60"/>
                        <nd ref="-27"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000012' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001220251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-29" version="0">
                        <nd ref="-30"/>
                        <nd ref="-60"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000013' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001320251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-30" version="0">
                        <nd ref="-50"/>
                        <nd ref="-66"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000014' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001420251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-31" version="0">
                        <nd ref="-49"/>
                        <nd ref="-66"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000015' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001520251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-32" version="0">
                        <nd ref="-59"/>
                        <nd ref="-52"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000016' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001620251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-33" version="0">
                        <nd ref="-53"/>
                        <nd ref="-59"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000017' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001720251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-50" version="0">
                        <nd ref="-65"/>
                        <nd ref="-25"/>
                        <nd ref="-27"/>
                        <nd ref="-60"/>
                        <nd ref="-30"/>
                        <nd ref="-24"/>
                        <nd ref="-65"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM11112222' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111222220251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <relation id="-2" version="0">
                        <member type="way" role="outer" ref="-8"/>
                        <member type="way" role="outer" ref="-26"/>
                        <member type="way" role="outer" ref="-20"/>
                        <member type="way" role="outer" ref="-31"/>
                        <member type="way" role="outer" ref="-18"/>
                        <member type="way" role="outer" ref="-33"/>
                        <member type="way" role="outer" ref="-19"/>
                        <member type="way" role="outer" ref="-29"/>
                        <tag k="admin_level" v="6"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="object_type" v="AX_Gebiet_Kreis"/>
                        <tag k="kreis:land" v="09"/>
                        <tag k="kreis:regierungsbezirk" v="2"/>
                        <tag k="kreis:kreis" v="03"/>
                        <tag k="type" v="boundary"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000222' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000022220251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k="schluesselGesamt" v="09203"/>
                    </relation>
                    <relation id="-1" version="0">
                        <member type="way" role="outer" ref="-27"/>
                        <member type="way" role="outer" ref="-6"/>
                        <member type="way" role="outer" ref="-28"/>
                        <member type="way" role="outer" ref="-29"/>
                        <member type="way" role="outer" ref="-8"/>
                        <member type="way" role="outer" ref="-26"/>
                        <tag k="object_type" v="AX_Gebiet_Bundesland"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="type" v="boundary"/>
                        <tag k="land:land" v="09"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000018' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000001820251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k="schluesselGesamt" v="09"/>
                    </relation>
                    <relation id="-3" version="0">
                        <member type="way" role="outer" ref="-6"/>
                        <member type="way" role="outer" ref="-28"/>
                        <member type="way" role="outer" ref="-19"/>
                        <member type="way" role="outer" ref="-33"/>
                        <member type="way" role="outer" ref="-18"/>
                        <member type="way" role="outer" ref="-31"/>
                        <member type="way" role="outer" ref="-20"/>
                        <member type="way" role="outer" ref="-27"/>
                        <tag k="admin_level" v="5"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="object_type" v="AX_Gebiet_Regierungsbezirk"/>
                        <tag k="regierungsbezirk:land" v="09"/>0
                        <tag k="regierungsbezirk:regierungsbezirk" v="1"/>
                        <tag k="type" v="boundary"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000020' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000002020251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k="schluesselGesamt" v="091"/>
                    </relation>
                    <relation id="-4" version="0">
                        <member type="way" role="outer" ref="-26"/>
                        <member type="way" role="outer" ref="-8"/>
                        <member type="way" role="outer" ref="-29"/>
                        <member type="way" role="outer" ref="-19"/>
                        <member type="way" role="outer" ref="-33"/>
                        <member type="way" role="outer" ref="-18"/>
                        <member type="way" role="outer" ref="-31"/>
                        <member type="way" role="outer" ref="-20"/>
                        <tag k="admin_level" v="5"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="object_type" v="AX_Gebiet_Regierungsbezirk"/>
                        <tag k="regierungsbezirk:land" v="09"/>
                        <tag k="regierungsbezirk:regierungsbezirk" v="2"/>
                        <tag k="type" v="boundary"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000021' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000002120251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k="schluesselGesamt" v="092"/>
                    </relation>
                    <relation id="-5" version="0">
                        <member type="way" role="outer" ref="-18"/>
                        <member type="way" role="outer" ref="-31"/>
                        <member type="way" role="outer" ref="-30"/>
                        <member type="way" role="outer" ref="-16"/>
                        <member type="way" role="outer" ref="-32"/>
                        <member type="way" role="outer" ref="-33"/>
                        <tag k="admin_level" v="6"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="object_type" v="AX_Gebiet_Kreis"/>
                        <tag k="kreis:land" v="09"/>
                        <tag k="kreis:regierungsbezirk" v="1"/>
                        <tag k="kreis:kreis" v="02"/>
                        <tag k="type" v="boundary"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000022' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000002220251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k="schluesselGesamt" v="09102"/>
                    </relation>
                    <relation id="-6" version="0">
                        <member type="way" role="outer" ref="-19"/>
                        <member type="way" role="outer" ref="-32"/>
                        <member type="way" role="outer" ref="-16"/>
                        <member type="way" role="outer" ref="-30"/>
                        <member type="way" role="outer" ref="-20"/>
                        <member type="way" role="outer" ref="-27"/>
                        <member type="way" role="outer" ref="-6"/>
                        <member type="way" role="outer" ref="-28"/>
                        <tag k="admin_level" v="6"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="object_type" v="AX_Gebiet_Kreis"/>
                        <tag k="kreis:kreis" v="01"/>
                        <tag k="kreis:land" v="09"/>
                        <tag k="kreis:regierungsbezirk" v="1"/>
                        <tag k="type" v="boundary"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM00000023' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000002320251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k="schluesselGesamt" v="09101"/>
                    </relation>
                    <relation id="-7" version="0">
                        <member type="way" role="outer" ref="-26"/>
                        <member type="way" role="outer" ref="-8"/>
                        <member type="way" role="outer" ref="-29"/>
                        <member type="way" role="outer" ref="-19"/>
                        <member type="way" role="outer" ref="-33"/>
                        <member type="way" role="outer" ref="-18"/>
                        <member type="way" role="outer" ref="-31"/>
                        <member type="way" role="outer" ref="-20"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="object_type" v="AX_KommunalesGebiet"/>
                        <tag k="type" v="boundary"/>
                        <tag k="gemeindekennzeichen:gemeinde" v="001"/>
                        <tag k="gemeindekennzeichen:kreis" v="03"/>
                        <tag k="gemeindekennzeichen:land" v="09"/>
                        <tag k="gemeindekennzeichen:regierungsbezirk" v="2"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM00000024"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM0000002420251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="schluesselGesamt" v="0901001"/>
                    </relation>
                    <relation id="-9" version="0">
                        <member type="way" role="outer" ref="-18"/>
                        <member type="way" role="outer" ref="-33"/>
                        <member type="way" role="outer" ref="-32"/>
                        <member type="way" role="outer" ref="-16"/>
                        <member type="way" role="outer" ref="-30"/>
                        <member type="way" role="outer" ref="-31"/>
                        <member type="way" role="inner" ref="-23"/>
                        <member type="way" role="inner" ref="-25"/>
                        <member type="way" role="inner" ref="-22"/>
                        <member type="way" role="inner" ref="-24"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="gemeindekennzeichen:gemeinde" v="001"/>
                        <tag k="gemeindekennzeichen:kreis" v="02"/>
                        <tag k="gemeindekennzeichen:land" v="09"/>
                        <tag k="gemeindekennzeichen:regierungsbezirk" v="1"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM00000026"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM0000002620251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="object_type" v="AX_KommunalesGebiet"/>
                        <tag k="schluesselGesamt" v="09102001"/>
                        <tag k="type" v="boundary"/>
                    </relation>
                    <relation id="-10" version="0">
                        <member type="way" role="outer" ref="-6"/>
                        <member type="way" role="outer" ref="-27"/>
                        <member type="way" role="outer" ref="-20"/>
                        <member type="way" role="outer" ref="-30"/>
                        <member type="way" role="outer" ref="-16"/>
                        <member type="way" role="outer" ref="-32"/>
                        <member type="way" role="outer" ref="-19"/>
                        <member type="way" role="outer" ref="-28"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="gemeindekennzeichen:gemeinde" v="001"/>
                        <tag k="gemeindekennzeichen:kreis" v="01"/>
                        <tag k="gemeindekennzeichen:land" v="09"/>
                        <tag k="gemeindekennzeichen:regierungsbezirk" v="1"/>
                        <tag k="identifikator:UUID" v="DEBYBDLM00000027"/>
                        <tag k="identifikator:UUIDundZeit" v="DEBYBDLM0000002720251014T125300Z"/>
                        <tag k="lebenszeitintervall:beginnt" v="2025-10-14T12:53:00Z"/>
                        <tag k="object_type" v="AX_KommunalesGebiet"/>
                        <tag k="schluesselGesamt" v="09101001"/>
                        <tag k="type" v="boundary"/>
                    </relation>
                    <relation id='-11' changeset='-1'>
                        <member type='way' ref='-6' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-12' changeset='-1'>
                        <member type='way' ref='-8' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-13' changeset='-1'>
                        <member type='way' ref='-16' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-14' changeset='-1'>
                        <member type='way' ref='-18' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-15' changeset='-1'>
                        <member type='way' ref='-19' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-16' changeset='-1'>
                        <member type='way' ref='-20' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-17' changeset='-1'>
                        <member type='way' ref='-21' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-18' changeset='-1'>
                        <member type='way' ref='-22' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-19' changeset='-1'>
                        <member type='way' ref='-23' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-20' changeset='-1'>
                        <member type='way' ref='-24' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-21' changeset='-1'>
                        <member type='way' ref='-25' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-22' changeset='-1'>
                        <member type='way' ref='-26' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-23' changeset='-1'>
                        <member type='way' ref='-27' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-24' changeset='-1'>
                        <member type='way' ref='-28' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-25' changeset='-1'>
                        <member type='way' ref='-29' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-26' changeset='-1'>
                        <member type='way' ref='-30' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-27' changeset='-1'>
                        <member type='way' ref='-31' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-28' changeset='-1'>
                        <member type='way' ref='-32' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-29' changeset='-1'>
                        <member type='relation' ref='-1' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-30' changeset='-1'>
                        <member type='relation' ref='-2' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-31' changeset='-1'>
                        <member type='relation' ref='-3' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-33' changeset='-1'>
                        <member type='relation' ref='-4' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-34' changeset='-1'>
                        <member type='relation' ref='-5' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-35' changeset='-1'>
                        <member type='relation' ref='-6' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-36' changeset='-1'>
                        <member type='relation' ref='-7' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-37' changeset='-1'>
                        <member type='relation' ref='-8' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-38' changeset='-1'>
                        <member type='relation' ref='-9' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-39' changeset='-1'>
                        <member type='relation' ref='-10' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-40' changeset='-1'>
                        <member type='way' ref='-33' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
                    </relation>
                    <relation id='-41' changeset='-1'>
                        <member type='way' ref='-50' role='' />
                        <tag k='advStandardModell' v='Basis-DLM' />
                        <tag k='object_type' v='AA_modellart' />
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
                .contains("Die Members von 'AX_KommunalesGebiet' dürfen nur 'AX_Gebietsgrenze' mit 'artDerGebietsgrenze' 7101, 7102, 7104, 7105, 7106 oder 7107 sein. Zudem muss 'AX_KommunalesGebiet' muss lückenlos und flächendeckend sein.");
    }

}
