INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.40001.G.b.002',
    'geometry-check',
    'AX_TatsaechlicheNutzung',
    '{
        "conditions": {
            "all": [
                {   "type": "tag_equals", "tag_key": "istWeitereNutzung", "value": "1000" },
                {
                    "type": "spatial_compare",
                    "operators": [ "covered_by", "overlaps" ],
                    "data_set_filter": { "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "current:object_type" } }
                }
            ]
        },

        "checks": {
            "all": [
                { "type": "tag_equals", "tag_key": "funktion", "value": "1200" },
                { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Wohnbauflaeche", "AX_IndustrieUndGewerbeflaeche", "AX_Bergbaubetrieb", "AX_TagebauGrubeSteinbruch",
                                                                         "AX_FlaecheGemischterNutzung", "AX_FlaecheBesondererFunktionalerPraegung", "AX_SportFreizeitUndErholungsflaeche",
                                                                         "AX_Friedhof", "AX_Bahnverkehr", "AX_Flugverkehr", "AX_Schiffsverkehr"] }
            ]
        }
    }',
    'Das Tag ''funktion'' ist nicht mit dem Wert 1200 belegt.')
ON CONFLICT (id) DO NOTHING;
