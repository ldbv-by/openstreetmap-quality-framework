INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.40001.G.b.001',
    'geometry-check',
    'AX_TatsaechlicheNutzung',
    '{
        "conditions": {
            "all": [
                { "type": "tag_equals", "tag_key": "funktion", "value": "1200" },
                { "type": "tag_exists", "tag_key": "istWeitereNutzung" }
            ]
        },
        "checks": {
            "not": {
                "type": "spatial_compare",
                "operator": "overlaps",
                "data_set_filter": {
                    "criteria": {
                        "all": [
                            {
                                "type": "tag_in", "tag_key": "object_type", "values": ["AX_Wohnbauflaeche", "AX_Weg", "AX_Wald", "AX_UnlandVegetationsloseFlaeche", "AX_TagebauGrubeSteinbruch", "AX_Sumpf", "AX_Strassenverkehr", "AX_StehendesGewaesser", "AX_SportFreizeitUndErholungsflaeche", "AX_Siedlungsflaeche",
                                                                                       "AX_Schiffsverkehr", "AX_Platz", "AX_Moor", "AX_Meer", "AX_Landwirtschaft", "AX_IndustrieUndGewerbeflaeche", "AX_Heide", "AX_Halde", "AX_Hafenbecken",  "AX_Gebietsgrenze",
                                                                                       "AX_Gehoelz", "AX_Friedhof", "AX_Flugverkehr", "AX_Fliessgewaesser", "AX_FlaecheZurZeitUnbestimmbar", "AX_FlaecheGemischterNutzung", "AX_Bahnverkehr", "AX_Bergbaubetrieb", "AX_FlaecheBesondererFunktionalerPraegung"]
                            },
                            {   "type": "tag_equals", "tag_key": "funktion", "value": "1200" },
                            {   "type": "tag_exists", "tag_key": "istWeitereNutzung" }
                        ]
                    }
                }
            }
        }
    }',
    'Im Bereich der Objekte "Tatsächliche Nutzung" dürfen sich Flächen mit der Funktion Parken und IstWeitereNutzung nicht überschneiden.')
ON CONFLICT (id) DO NOTHING;
