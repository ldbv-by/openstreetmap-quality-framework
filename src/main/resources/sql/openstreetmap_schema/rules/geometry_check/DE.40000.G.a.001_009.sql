INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.40000.G.a.001_009',
    'geometry-check',
    'AX_TatsaechlicheNutzung',
    '{
        "conditions": {
            "all": [
                { "not": { "type": "tag_exists", "tag_key": "istWeitereNutzung" } },
                { "not": { "relations": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" } } }
            ]
        },
        "checks": {
            "all": [
                {
                    "type": "spatial_compare",
                    "operator": "surrounded_by",
                    "data_set_filter": {
                        "criteria": {
                            "any": [
                                {
                                    "type": "tag_in", "tag_key": "object_type", "values": ["AX_Wohnbauflaeche", "AX_Weg", "AX_Wald", "AX_UnlandVegetationsloseFlaeche", "AX_TagebauGrubeSteinbruch", "AX_Sumpf", "AX_Strassenverkehr", "AX_StehendesGewaesser", "AX_SportFreizeitUndErholungsflaeche", "AX_Siedlungsflaeche",
                                                                                           "AX_Schiffsverkehr", "AX_Platz", "AX_Moor", "AX_Meer", "AX_Landwirtschaft", "AX_IndustrieUndGewerbeflaeche", "AX_Heide", "AX_Halde", "AX_Hafenbecken",  "AX_Gebietsgrenze",
                                                                                           "AX_Gehoelz", "AX_Friedhof", "AX_Flugverkehr", "AX_Fliessgewaesser", "AX_FlaecheZurZeitUnbestimmbar", "AX_FlaecheGemischterNutzung", "AX_Bahnverkehr", "AX_Bergbaubetrieb", "AX_FlaecheBesondererFunktionalerPraegung"]
                                },
                                {
                                    "all": [
                                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" },
                                        { "type": "tag_in", "tag_key": "artDerGebietsgrenze", "values": ["7101", "7102"] }
                                    ]
                                }
                            ]
                        }
                    }
                },
                {
                    "not": {
                        "type": "spatial_compare",
                        "operators": [ "overlaps", "equals" ],
                        "data_set_filter": {
                            "criteria": {
                                "all": [
                                    {
                                        "type": "tag_in", "tag_key": "object_type", "values": ["AX_Wohnbauflaeche", "AX_Weg", "AX_Wald", "AX_UnlandVegetationsloseFlaeche", "AX_TagebauGrubeSteinbruch", "AX_Sumpf", "AX_Strassenverkehr", "AX_StehendesGewaesser", "AX_SportFreizeitUndErholungsflaeche", "AX_Siedlungsflaeche",
                                                                                               "AX_Schiffsverkehr", "AX_Platz", "AX_Moor", "AX_Meer", "AX_Landwirtschaft", "AX_IndustrieUndGewerbeflaeche", "AX_Heide", "AX_Halde", "AX_Hafenbecken",
                                                                                               "AX_Gehoelz", "AX_Friedhof", "AX_Flugverkehr", "AX_Fliessgewaesser", "AX_FlaecheZurZeitUnbestimmbar", "AX_FlaecheGemischterNutzung", "AX_Bahnverkehr", "AX_Bergbaubetrieb", "AX_FlaecheBesondererFunktionalerPraegung"]
                                    },
                                    { "not": { "type": "tag_exists", "tag_key": "istWeitereNutzung" } },
                                    { "not": {
                                        "type": "relation_exists",
                                        "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" } }
                                    }
                                ]
                            }
                        }
                    }
                }
            ]
        }
    }',
    'Im Bereich der Objekte "Tatsächliche Nutzung" existiert eine Lücke bzw. Überschneidung in der Flächendeckung.')
ON CONFLICT (id) DO NOTHING;
