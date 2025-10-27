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
 * Start- oder Endpunkt von 'SchifffahrtslinieFährverkehr' kann nur an einem Objekt
 * 53008 'Einrichtung für den Schiffsverkehr' mit ART 1460 'Anleger' oder 75009 'Gebietsgrenze' mit AGZ 7101
 * 'Grenze der Bundesrepublik Deutschland' oder 7102 'Grenze des Bundeslandes' oder mit einem weiteren
 * Objekt 'SchifffahrtslinieFährverkehr' vorkommen.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_57002_G_b_002 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createSchifffahrtslinieMitGebietsgrenzeUndMitAnleger() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                	<create>
                		<node id="-13" lon="12.329850365684644" lat="49.879866392051326" version="0"/>
                		<node id="-14" lon="12.331191588919566" lat="49.87986398459362" version="0"/>
                		<node id="-15" lon="12.331184116753123" lat="49.87878302804325" version="0"/>
                		<node id="-16" lon="12.329742021694045" lat="49.87879988048421" version="0"/>
                		<node id="-19" lon="12.331187844441608" lat="49.879322294923945" version="0"/>
                		<node id="-20" lon="12.330616245215207" lat="49.87932471306288" version="0"/>
                		<node id="-26" lon="12.331190490657235" lat="49.87970510663869" version="0"/>
                		<node id="-27" lon="12.33118579739293" lat="49.879026158918776" version="0"/>
                		<node id="-28" lon="12.3301343014536" lat="49.879045445183195" version="0"/>
                		<node id="-25" lon="12.33021649328921" lat="49.87970990764771" version="0">
                            <tag k='identifikator:UUID' v='DEBYBDLM12345678' />
                            <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1234567820251014T125300Z' />
                            <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                            <tag k='object_type' v='AX_EinrichtungenFuerDenSchiffsverkehr' />
                            <tag k='art' v='1460' />
                		</node>
                		<way id="-4" version="0">
                			<nd ref="-13"/>
                			<nd ref="-14"/>
                			<nd ref="-26"/>
                			<nd ref="-19"/>
                			<nd ref="-27"/>
                			<nd ref="-15"/>
                			<nd ref="-16"/>
                			<nd ref="-13"/>
                			<tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="artDerGebietsgrenze" v="7101"/>
                            <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                            <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                            <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                		</way>
                		<way id="-5" version="0">
                			<nd ref="-19"/>
                			<nd ref="-20"/>
                			<tag k="object_type" v="AX_SchifffahrtslinieFaehrverkehr"/>
                			<tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                            <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                            <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                            <tag k='art' v='1740' />
                		</way>
                		<way id="-6" version="0">
                			<nd ref="-25"/>
                			<nd ref="-20"/>
                			<tag k="object_type" v="AX_SchifffahrtslinieFaehrverkehr"/>
                			<tag k='identifikator:UUID' v='DEBYBDLM22222222' />
                            <tag k='identifikator:UUIDundZeit' v='DEBYBDLM2222222220251014T125300Z' />
                            <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                            <tag k='art' v='1740' />
                		</way>
                		<way id="-7" version="0">
                			<nd ref="-25"/>
                			<nd ref="-26"/>
                			<nd ref="-27"/>
                			<nd ref="-28"/>
                			<nd ref="-25"/>
                			<tag k="object_type" v="AX_Hafenbecken"/>
                			<tag k='identifikator:UUID' v='DEBYBDLM33333333' />
                            <tag k='identifikator:UUIDundZeit' v='DEBYBDLM3333333320251014T125300Z' />
                            <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                		</way>
                		<relation id='-1' changeset='-1'>
                            <member type='way' ref='-7' role='' />
                            <tag k='advStandardModell' v='Basis-DLM' />
                            <tag k='object_type' v='AA_modellart' />
                        </relation>
                        <relation id='-2' changeset='-1'>
                            <member type='way' ref='-6' role='' />
                            <tag k='advStandardModell' v='Basis-DLM' />
                            <tag k='object_type' v='AA_modellart' />
                        </relation>
                        <relation id='-3' changeset='-1'>
                            <member type='way' ref='-5' role='' />
                            <tag k='advStandardModell' v='Basis-DLM' />
                            <tag k='object_type' v='AA_modellart' />
                        </relation>
                        <relation id='-4' changeset='-1'>
                            <member type='way' ref='-4' role='' />
                            <tag k='advStandardModell' v='Basis-DLM' />
                            <tag k='object_type' v='AA_modellart' />
                        </relation>
                        <relation id='-5' changeset='-1'>
                            <member type='node' ref='-25' role='' />
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
    void createSchifffahrtslinieMitGebietsgrenzeOhneAnleger() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                	<create>
                		<node id="-13" lon="12.329850365684644" lat="49.879866392051326" version="0"/>
                		<node id="-14" lon="12.331191588919566" lat="49.87986398459362" version="0"/>
                		<node id="-15" lon="12.331184116753123" lat="49.87878302804325" version="0"/>
                		<node id="-16" lon="12.329742021694045" lat="49.87879988048421" version="0"/>
                		<node id="-19" lon="12.331187844441608" lat="49.879322294923945" version="0"/>
                		<node id="-20" lon="12.330616245215207" lat="49.87932471306288" version="0"/>
                		<node id="-26" lon="12.331190490657235" lat="49.87970510663869" version="0"/>
                		<node id="-27" lon="12.33118579739293" lat="49.879026158918776" version="0"/>
                		<node id="-28" lon="12.3301343014536" lat="49.879045445183195" version="0"/>
                		<node id="-25" lon="12.33021649328921" lat="49.87970990764771" version="0" />
                		<way id="-4" version="0">
                			<nd ref="-13"/>
                			<nd ref="-14"/>
                			<nd ref="-26"/>
                			<nd ref="-19"/>
                			<nd ref="-27"/>
                			<nd ref="-15"/>
                			<nd ref="-16"/>
                			<nd ref="-13"/>
                			<tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="artDerGebietsgrenze" v="7101"/>
                            <tag k='identifikator:UUID' v='DEBYBDLM00000000' />
                            <tag k='identifikator:UUIDundZeit' v='DEBYBDLM0000000020251014T125300Z' />
                            <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                		</way>
                		<way id="-5" version="0">
                			<nd ref="-19"/>
                			<nd ref="-20"/>
                			<tag k="object_type" v="AX_SchifffahrtslinieFaehrverkehr"/>
                			<tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                            <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                            <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                            <tag k='art' v='1740' />
                		</way>
                		<way id="-6" version="0">
                			<nd ref="-25"/>
                			<nd ref="-20"/>
                			<tag k="object_type" v="AX_SchifffahrtslinieFaehrverkehr"/>
                			<tag k='identifikator:UUID' v='DEBYBDLM22222222' />
                            <tag k='identifikator:UUIDundZeit' v='DEBYBDLM2222222220251014T125300Z' />
                            <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                            <tag k='art' v='1740' />
                		</way>
                		<way id="-7" version="0">
                			<nd ref="-25"/>
                			<nd ref="-26"/>
                			<nd ref="-27"/>
                			<nd ref="-28"/>
                			<nd ref="-25"/>
                			<tag k="object_type" v="AX_Hafenbecken"/>
                			<tag k='identifikator:UUID' v='DEBYBDLM33333333' />
                            <tag k='identifikator:UUIDundZeit' v='DEBYBDLM3333333320251014T125300Z' />
                            <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                		</way>
                		<relation id='-1' changeset='-1'>
                            <member type='way' ref='-7' role='' />
                            <tag k='advStandardModell' v='Basis-DLM' />
                            <tag k='object_type' v='AA_modellart' />
                        </relation>
                        <relation id='-2' changeset='-1'>
                            <member type='way' ref='-6' role='' />
                            <tag k='advStandardModell' v='Basis-DLM' />
                            <tag k='object_type' v='AA_modellart' />
                        </relation>
                        <relation id='-3' changeset='-1'>
                            <member type='way' ref='-5' role='' />
                            <tag k='advStandardModell' v='Basis-DLM' />
                            <tag k='object_type' v='AA_modellart' />
                        </relation>
                        <relation id='-4' changeset='-1'>
                            <member type='way' ref='-4' role='' />
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
                .contains("Der Start- und Endpunkt von 'AX_SchifffahrtslinieFaehrverkehr' liegt immer an 'AX_EinrichtungenFuerDenSchiffsverkehr' mit 'art' 1460 , 'AX_Gebietsgrenze' mit 'artDerGebietsgrenze' 7101 oder 7102 oder einem weiteren Objekt 'AX_SchifffahrtslinieFaehrverkehr'.");
    }
}
