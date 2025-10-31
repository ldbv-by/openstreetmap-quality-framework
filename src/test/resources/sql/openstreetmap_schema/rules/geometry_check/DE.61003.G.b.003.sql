INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.61003.G.b.003',
    'geometry-check',
    'AX_DammWallDeich',
    '{
        "conditions": {
            "all": [
                { "type": "geom_type", "value": "LineString" },
                { "type": "tag_in", "tag_key": "funktion", "values": ["3002", "3003"] }
            ]
        },
        "checks": {
            "any": [
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "aggregator": "union", "featureFilter": { "tags": { "object_type": "AX_Strassenachse|AX_Fahrwegachse|AX_Bahnstrecke|AX_Gleis" } } }
                },
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "aggregator": "union", "featureFilter": { "tags": { "object_type": "AX_WegPfadSteig", "art": "1106|1110" } } }
                }
            ]
        }
    }',
    'Ein linienförmiges Objekt ''AX_DammWallDeich'' mit ''funktion'' 3002 oder 3003 muss auf der Geometrie oder mehrerer verketteter Objekte ''AX_Strassenachse'', ''AX_Fahrwegachse'', ''AX_Bahnstrecke'', ''AX_Gleis'' oder ''AX_WegPfadSteig'' mit ''art'' 1106 oder 1110 liegen.')
ON CONFLICT (id) DO NOTHING;
