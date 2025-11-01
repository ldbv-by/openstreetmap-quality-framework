INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.75009.G.b.003',
    'geometry-check',
    'AX_Gebietsgrenze',
    '{
        "conditions": { "type": "tag_in", "tag_key": "artDerGebietsgrenze", "values": [ "7101", "7102" ] },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by_boundary",
            "data_set_filter": { "aggregator": "union", "featureFilter": { "tags": { "object_type": "AX_Weg|AX_Strassenverkehr|AX_Schiffsverkehr|AX_Platz|AX_Flugverkehr|AX_Bahnverkehr|AX_Wald|AX_UnlandVegetationsloseFlaeche|AX_Sumpf|AX_Moor|AX_Landwirtschaft|AX_Heide|AX_Gehoelz|AX_FlaecheZurZeitUnbestimmbar|AX_TagebauGrubeSteinbruch|AX_Siedlungsflaeche|AX_IndustrieUndGewerbeflaeche|AX_Halde|AX_Friedhof|AX_Fliessgewaesser|AX_Hafenbecken|AX_Meer|AX_StehendesGewaesser|AX_Bergbaubetrieb|AX_FlaecheBesondererFunktionalerPraegung|AX_FlaecheGemischterNutzung|AX_SportFreizeitUndErholungsflaeche|AX_Wohnbauflaeche"} } }
        }
    }',
    'An der Landesgrenze muss eine lückenlose und überschneidungsfreie Flächendeckung Objekten der Objektart ''Tatsächliche Nutzung'' bestehen.')
ON CONFLICT (id) DO NOTHING;
