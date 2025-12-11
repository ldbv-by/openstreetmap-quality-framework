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
 * Start- oder Endpunkt von 'SchifffahrtslinieFährverkehr' kann nur an einem Objekt
 * 53008 'Einrichtung für den Schiffsverkehr' mit ART 1460 'Anleger' oder 75009 'Gebietsgrenze' mit AGZ 7101
 * 'Grenze der Bundesrepublik Deutschland' oder 7102 'Grenze des Bundeslandes' oder mit einem weiteren
 * Objekt 'SchifffahrtslinieFährverkehr' vorkommen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_57002_G_b_002_F_b_002 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.57002.G.b.002_F.b.002"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createSchifffahrtslinieMitGebietsgrenzeUndMitAnleger() throws Exception {
        // Arrange
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
                            <tag k='admin_level' v='4' />
                            <tag k="boundary" v="administrative"/>
                            <tag k="admin_level" v="4"/>
                		</way>
                		<way id="-5" version="0">
                			<nd ref="-19"/>
                			<nd ref="-20"/>
                			<tag k="object_type" v="AX_SchifffahrtslinieFaehrverkehr"/>
                            <tag k='art' v='1740' />
                		</way>
                		<way id="-6" version="0">
                			<nd ref="-25"/>
                			<nd ref="-20"/>
                			<tag k="object_type" v="AX_SchifffahrtslinieFaehrverkehr"/>
                            <tag k='art' v='1740' />
                		</way>
                		<way id="-7" version="0">
                			<nd ref="-25"/>
                			<nd ref="-26"/>
                			<nd ref="-27"/>
                			<nd ref="-28"/>
                			<nd ref="-25"/>
                			<tag k="object_type" v="AX_Hafenbecken"/>
                		</way>
                	</create>
                	<modify/>
                	<delete if-unused="true"/>
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
    void createSchifffahrtslinieMitGebietsgrenzeOhneAnleger() throws Exception {
        // Arrange
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
                            <tag k="boundary" v="administrative"/>
                            <tag k="admin_level" v="4"/>
                		</way>
                		<way id="-5" version="0">
                			<nd ref="-19"/>
                			<nd ref="-20"/>
                			<tag k="object_type" v="AX_SchifffahrtslinieFaehrverkehr"/>
                            <tag k='art' v='1740' />
                		</way>
                		<way id="-6" version="0">
                			<nd ref="-25"/>
                			<nd ref="-20"/>
                			<tag k="object_type" v="AX_SchifffahrtslinieFaehrverkehr"/>
                            <tag k='art' v='1740' />
                		</way>
                		<way id="-7" version="0">
                			<nd ref="-25"/>
                			<nd ref="-26"/>
                			<nd ref="-27"/>
                			<nd ref="-28"/>
                			<nd ref="-25"/>
                			<tag k="object_type" v="AX_Hafenbecken"/>
                		</way>
                	</create>
                	<modify/>
                	<delete if-unused="true"/>
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
                .contains("Der Start- und Endpunkt von 'AX_SchifffahrtslinieFaehrverkehr' liegt immer an 'AX_EinrichtungenFuerDenSchiffsverkehr' mit 'art' 1460 , 'AX_Gebietsgrenze' mit 'artDerGebietsgrenze' 7101 oder 7102 oder einem weiteren Objekt 'AX_SchifffahrtslinieFaehrverkehr'.");
    }
}
