package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.controller;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.service.QualityHubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(QualityHubController.class)
class QualityHubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    QualityHubService qualityHubService;

    @Test
    void testValidComplexChangesetFormat() throws Exception {
        String validChangeset = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25363' changeset='-1' lat='49.8794750352' lon='12.34310948726' />
                  <node id='-25362' changeset='-1' lat='49.87928346479' lon='12.34322434755' />
                  <node id='-25361' changeset='-1' lat='49.87933571134' lon='12.3434135292' />
                  <node id='-25360' changeset='-1' lat='49.87951421997' lon='12.34333920784' />
                  <way id='-582' changeset='-1'>
                    <nd ref='-25360' />
                    <nd ref='-25361' />
                    <nd ref='-25362' />
                    <nd ref='-25363' />
                    <nd ref='-25360' />
                    <tag k='BEB_artDerBebauung' v='10000' />
                    <tag k='FKT_funktion' v='1000' />
                    <tag k='IWN_istWeitereNutzung' v='1000' />
                    <tag k='LGK_lage' v='1000' />
                    <tag k='ONR_objektgenauigkeit_BYnummer' v='-' />
                    <tag k='SYS_arbeitsmappenIds' v='-' />
                    <tag k='object_type' v='A_41001_Wohnbauflaeche' />
                    <tag k='type' v='-' />
                  </way>
                  <relation id='-59' changeset='-1'>
                    <member type='way' ref='1494620' role='outer' />
                    <member type='way' ref='-582' role='inner' />
                    <tag k='BEB_artDerBebauung' v='10000' />
                    <tag k='FKT_funktion' v='11100' />
                    <tag k='IWN_istWeitereNutzung' v='1000' />
                    <tag k='LGK_lagegenauigkeit_BY' v='1000' />
                    <tag k='NAM_name' v='-' />
                    <tag k='ONR_objektnummer' v='DEBYBDLMJW00026k' />
                    <tag k='SYS_arbeitsmappenIds' v='6140' />
                    <tag k='ZUS_zustand' v='1000' />
                    <tag k='object_type' v='A_41007_FlBesFunkPraegung' />
                    <tag k='type' v='multipolygon' />
                  </relation>
                </create>
                <modify>
                  <node id='9097702' version='1' changeset='-1' lat='49.87867325483' lon='12.34283803006' />
                  <node id='9106300' version='2' changeset='-1' lat='49.87909123565' lon='12.34031459592' />
                  <way id='1494620' version='1' changeset='-1'>
                    <nd ref='9049206' />
                    <nd ref='9120611' />
                    <nd ref='9120610' />
                    <nd ref='9120609' />
                    <nd ref='9120608' />
                    <nd ref='9048960' />
                    <nd ref='9048961' />
                    <nd ref='9048962' />
                    <nd ref='9048963' />
                    <nd ref='9082465' />
                    <nd ref='9082464' />
                    <nd ref='9082463' />
                    <nd ref='9082462' />
                    <nd ref='9082461' />
                    <nd ref='9047960' />
                    <nd ref='9048250' />
                    <nd ref='9048251' />
                    <nd ref='9048273' />
                    <nd ref='9048272' />
                    <nd ref='9048271' />
                    <nd ref='9048270' />
                    <nd ref='9048269' />
                    <nd ref='9048268' />
                    <nd ref='9047920' />
                    <nd ref='9049212' />
                    <nd ref='9049211' />
                    <nd ref='9049210' />
                    <nd ref='9049209' />
                    <nd ref='9049208' />
                    <nd ref='9049207' />
                    <nd ref='9049206' />
                  </way>
                </modify>
                <delete>
                  <way id='1484375' version='1' changeset='-1'/>
                  <node id='9043306' version='1' changeset='-1'/>
                  <node id='9048826' version='1' changeset='-1'/>
                  <node id='9048945' version='1' changeset='-1'/>
                  <node id='9048946' version='1' changeset='-1'/>
                  <node id='9048947' version='1' changeset='-1'/>
                  <node id='9048948' version='1' changeset='-1'/>
                  <node id='9048949' version='1' changeset='-1'/>
                  <node id='9048950' version='1' changeset='-1'/>
                  <node id='9048951' version='1' changeset='-1'/>
                  <node id='9048952' version='1' changeset='-1'/>
                  <node id='9048953' version='1' changeset='-1'/>
                  <node id='9048954' version='1' changeset='-1'/>
                  <node id='9048955' version='1' changeset='-1'/>
                  <node id='9048956' version='1' changeset='-1'/>
                  <node id='9048957' version='1' changeset='-1'/>
                  <node id='9048958' version='1' changeset='-1'/>
                  <node id='9048959' version='1' changeset='-1'/>
                </delete>
                </osmChange>
                """;

        this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", 1L)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(validChangeset))
                .andExpect(status().isOk());
    }

    @Test
    void testInvalidChangesetFormat() throws Exception {
        String invalidChangeset = "<osm></osm>";

        this.mockMvc.perform(
                post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", 1L)
                        .contentType(MediaType.APPLICATION_XML)
                        .content(invalidChangeset))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Changeset has invalid format")));
    }

    @Test
    void testEmptyChangesetFormat() throws Exception {
        String emptyChangeset = "";

        this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", 1L)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(emptyChangeset))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Changeset has invalid format")));
    }
}