package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.geometry_check;

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
 * Die 'Gebietsgrenze' ist identisch mit 1 bis n Kanten der Masche, die zur Vermittlung des Raumbezugs der entsprechenden 75003 'KommunalesGebiet' beitragen.
 * Von der Prüfung sind folgende Objekte 75009 'Gebietsgrenze' ausgenommen:
 * 1. Objekte 75009 'Gebietsgrenze', welche für die Attributart 'Art der Gebietsgrenze' nur die Werteart 7107 'Grenze des Gemeindeteils' besitzen.
 * 2. Objekte 75009 'Gebietsgrenze', welche für die Attributart 'Art der Gebietsgrenze' nur die Wertearten 7101 'Grenze der Bundesrepublik Deutschland' und/oder
 *    7102 'Grenze des Bundeslandes' besitzen und für die gilt:
 *     a) Die Grenze befindet sich vollständig auf dem Rand der Vereinigung der Objekte AX_StehendesGewaesser mit NAM = 'Bodensee'
 * 	   (Hinweis: im Basis-DLM und DLM50 hat NAM bei AX_StehendesGewaesser stets eine unverschlüsselte Lagebezeichnung als Wert),
 *     b) Die Grenze befindet sich vollständig auf dem Rand der Vereinigung der Objekte AX_Meer, oder
 *     c) Die Grenze befindet sich vollständig auf dem Rand der Vereinigung aller AX_Fliessgewaesser mit zeigtAufExternes;
 * 	   AA_Fachdatenverbindung; art 1900: Fachunterlage; AA_Fachdatenobjekt; name beginnt mit 'FKT8230' (Flussmündungstrichter).
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_75009_G_b_001_009 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.75009.G.b.001_009"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createGebietsgrenzenMitKommunalenGebiet() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
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
                    </way>
                    <way id="-8" version="0">
                        <nd ref="-24"/>
                        <nd ref="-30"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                    </way>
                    <way id="-16" version="0">
                        <nd ref="-50"/>
                        <nd ref="-58"/>
                        <nd ref="-52"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                    </way>
                    <way id="-18" version="0">
                        <nd ref="-49"/>
                        <nd ref="-53"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                    </way>
                    <way id="-19" version="0">
                        <nd ref="-59"/>
                        <nd ref="-60"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                    </way>
                    <way id="-20" version="0">
                        <nd ref="-65"/>
                        <nd ref="-66"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                    </way>
                    <way id="-22" version="0">
                        <nd ref="-76"/>
                        <nd ref="-77"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                    </way>
                    <way id="-23" version="0">
                        <nd ref="-78"/>
                        <nd ref="-79"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                    </way>
                    <way id="-24" version="0">
                        <nd ref="-78"/>
                        <nd ref="-76"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                    </way>
                    <way id="-25" version="0">
                        <nd ref="-79"/>
                        <nd ref="-77"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                    </way>
                    <way id="-26" version="0">
                        <nd ref="-24"/>
                        <nd ref="-65"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                    </way>
                    <way id="-27" version="0">
                        <nd ref="-65"/>
                        <nd ref="-25"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                    </way>
                    <way id="-28" version="0">
                        <nd ref="-60"/>
                        <nd ref="-27"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                    </way>
                    <way id="-29" version="0">
                        <nd ref="-30"/>
                        <nd ref="-60"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
                    </way>
                    <way id="-30" version="0">
                        <nd ref="-50"/>
                        <nd ref="-66"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                    </way>
                    <way id="-31" version="0">
                        <nd ref="-49"/>
                        <nd ref="-66"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                    </way>
                    <way id="-32" version="0">
                        <nd ref="-59"/>
                        <nd ref="-52"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                    </way>
                    <way id="-33" version="0">
                        <nd ref="-53"/>
                        <nd ref="-59"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="8"/>
                        <tag k="artDerGebietsgrenze" v="7106"/>
                    </way>
                    <way id="-50" version="0">
                        <nd ref="-65"/>
                        <nd ref="-25"/>
                        <nd ref="-27"/>
                        <nd ref="-60"/>
                        <nd ref="-30"/>
                        <nd ref="-65"/>
                        <tag k="object_type" v="AX_Wohnbauflaeche"/>
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
                        <tag k="object_type" v="AX_KommunalesGebiet"/>
                        <tag k="schluesselGesamt" v="09101001"/>
                        <tag k="type" v="boundary"/>
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
    void createGebietsgrenzeOhneKommunalesGebiet() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.327319499952107" lat="49.878070268874175" version="0"/>
                    <node id="-2" lon="12.330740984227374" lat="49.87807055563436" version="0"/>
                    <way id="-1" version="0">
                        <nd ref="-1"/>
                        <nd ref="-2"/>
                        <tag k="object_type" v="AX_Gebietsgrenze"/>
                        <tag k="boundary" v="administrative"/>
                        <tag k="admin_level" v="4"/>
                        <tag k="artDerGebietsgrenze" v="7101;7106"/>
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
                .contains("Im Bereich der Gebietsgrenze stimmt der Verlauf nicht mit den Kommunalen Gebieten überein.");
    }

    @Test
    void createGebietsgrenzeOhneKommunalesGebietAmBodensee() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
            <osmChange version="0.6" generator="iD">
            <create>
                <node id="-25" lon="12.327319499952107" lat="49.878070268874175" version="0"/>
                <node id="-27" lon="12.330740984227374" lat="49.87807055563436" version="0"/>
                <node id="-30" lon="12.330643600712994" lat="49.8798390208782" version="0"/>
                <node id="-60" lon="12.330691253433757" lat="49.87897366504399" version="0"/>
                <node id="-65" lon="12.327291413642298" lat="49.87896483476299" version="0"/>
                <way id="-6" version="0">
                    <nd ref="-27"/>
                    <nd ref="-25"/>
                    <tag k="object_type" v="AX_Gebietsgrenze"/>
                    <tag k="boundary" v="administrative"/>
                    <tag k="admin_level" v="4"/>
                    <tag k="artDerGebietsgrenze" v="7101;7106"/>
                </way>
                <way id="-50" version="0">
                    <nd ref="-65"/>
                    <nd ref="-25"/>
                    <nd ref="-27"/>
                    <nd ref="-60"/>
                    <nd ref="-30"/>
                    <nd ref="-65"/>
                    <tag k="object_type" v="AX_StehendesGewaesser"/>
                    <tag k="name" v="Bodensee"/>
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
}
