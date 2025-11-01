INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.61004.G.b.001_002',
    'geometry-check',
    'AX_Einschnitt',
    '{
        "conditions": { "type": "tag_in", "tag_key": "funktion", "values": ["3002", "3004"] },
        "checks": {
            "type": "spatial_compare",
            "operator": "equals_topo",
            "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Strassenachse|AX_Fahrbahnachse|AX_Fahrwegachse|AX_Bahnstrecke|AX_WegPfadSteig" } } }
        }
    }',
    'Ein Objekt ''AX_Einschnitt'' mit ''funktion'' 3002 oder 3004 überlagert immer ein Objekt ''AX_Strassenachse'', ''AX_Fahrbahnachse'', ''AX_Fahrwegachse'', ''AX_Bahnstrecke'' oder ''AX_WegPfadSteig'' mit identischer Geometrie.')
ON CONFLICT (id) DO NOTHING;