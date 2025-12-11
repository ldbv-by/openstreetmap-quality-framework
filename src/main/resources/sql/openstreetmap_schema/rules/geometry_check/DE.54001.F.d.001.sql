INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.54001.F.d.001',
    'geometry-check',
    'AX_Vegetationsmerkmal',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "zustand", "value": "5000" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "aggregator": "union",
                "criteria": {
                    "any": [
                        {
                            "all": [
                                { "type":  "tag_equals", "tag_key": "object_type", "value": "AX_Landwirtschaft" },
                                { "type":  "tag_in", "tag_key": "vegetationsmerkmal", "values": ["1010", "1020"] }
                            ]
                        },
                        {
                            "all": [
                                { "type":  "tag_equals", "tag_key": "object_type", "value": "AX_UnlandVegetationsloseFlaeche" },
                                { "type":  "tag_in", "tag_key": "funktion", "values": ["1100", "1300"] }
                            ]
                        },
                        { "type":  "tag_in", "tag_key": "object_type", "values": ["AX_Wald", "AX_Gehoelz", "AX_Heide", "AX_Moor"] }
                    ]
                }
            }
        }
    }',
    'Ein ''AX_Vegetationsmerkmal'' mit ''zustand'' 5000 darf nur auf einem Objekt ''AX_Landwirtschaft'' mit ''vegetationsmerkmal'' 1010 oder 1020, ''AX_Wald'', ''AX_Gehoelz'', ''AX_Heide'', ''AX_Moor'' oder ''AX_UnlandVegetationsloseFlaeche'' mit ''funktion'' 1100 oder 1300 liegen.')
ON CONFLICT (id) DO NOTHING;