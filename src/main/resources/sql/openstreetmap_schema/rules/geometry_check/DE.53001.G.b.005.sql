INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53001.G.b.005',
    'geometry-check',
    'AX_BauwerkImVerkehrsbereich',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1900" },
        "checks": {
            "relations": {
                "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                "checks": {
                    "type": "spatial_compare",
                    "reference_feature_role": "under",
                    "operator": "equals_topo",
                    "data_set_filter": {
                        "memberFilter": {
                            "role": "over",
                            "objectTypes": ["AX_Strassenachse", "AX_Fahrbahnachse", "AX_WegPfadSteig", "AX_Fahrwegachse", "AX_Bahnstrecke", "AX_Gewaesserachse"]
                        }
                    },
                    "self_check": true
                }
            }
        }
    }',
    'Ein Objekt mit der ''bauwerksfunktion'' 1900 muss ein Objekt ''AX_Strassenachse'', ''AX_Fahrbahnachse'', ''AX_Fahrwegachse'', ''AX_WegPfadSteig'', ''AX_Gewaesserachse'' oder''AX_Bahnstrecke'' überlagern.')
ON CONFLICT (id) DO NOTHING;
