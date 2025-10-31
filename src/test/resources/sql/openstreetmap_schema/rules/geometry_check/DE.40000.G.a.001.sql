INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.40000.G.a.001',
    'geometry-check',
    'AX_TatsaechlicheNutzung-TODO',
    '{
        "conditions": {
            "all": [
                { "not": { "type": "tag_exists", "tag_key": "istWeitereNutzung" } },
                { "not": { "relations": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" } } }
            ]
        },
        "checks": {
            "type": "spatial_compare",
                    "operator": "surrounded_by",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Wohnbauflaeche|AX_Weg|AX_Wald|AX_UnlandVegetationsloseFlaeche|AX_TagebauGrubeSteinbruch|AX_Sumpf|AX_Strassenverkehr|AX_StehendesGewaesser|AX_SportFreizeitUndErholungsflaeche|AX_Siedlungsflaeche|AX_Schiffsverkehr|AX_Platz|AX_Moor|AX_Meer|AX_Landwirtschaft|AX_IndustrieUndGewerbeflaeche|AX_Heide|AX_Halde|AX_Hafenbecken|AX_Gehoelz|AX_Friedhof|AX_Flugverkehr|AX_Fliessgewaesser|AX_FlaecheZurZeitUnbestimmbar|AX_FlaecheGemischterNutzung|AX_Bahnverkehr|AX_Bergbaubetrieb|AX_FlaecheBesondererFunktionalerPraegung|AX_Gebietsgrenze", "artDerGebietsgrenze": "not_exists|7101|7102", "istWeitereNutzung": "not_exists" } } }
                }
            ]
        }
    }',
    'Die Members von ''AX_KommunalesGebiet'' dürfen nur ''AX_Gebietsgrenze'' mit ''artDerGebietsgrenze'' 7101, 7102, 7104, 7105, 7106 oder 7107 sein. Zudem muss ''AX_KommunalesGebiet'' muss lückenlos und flächendeckend sein.')
ON CONFLICT (id) DO NOTHING;
