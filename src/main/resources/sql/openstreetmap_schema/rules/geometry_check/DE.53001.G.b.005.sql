INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53001.G.b.005',
    'geometry-check',
    'AX_BauwerkImVerkehrsbereich',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1900" },
        "checks": {
            "type": "spatial_compare",
            "operator": "equals_topo",
            "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Strassenachse|AX_Fahrbahnachse|AX_Fahrwegachse|AX_WegPfadSteig|AX_Bahnstrecke|AX_Gewaesserachse" } } }
        }
    }',
    'Ein Objekt mit der ''bauwerksfunktion'' 1900 muss ein Objekt ''AX_Strassenachse'', ''AX_Fahrbahnachse'', ''AX_Fahrwegachse'', ''AX_WegPfadSteig'', ''AX_Gewaesserachse'' oder''AX_Bahnstrecke'' überlagern.')
ON CONFLICT (id) DO NOTHING;
