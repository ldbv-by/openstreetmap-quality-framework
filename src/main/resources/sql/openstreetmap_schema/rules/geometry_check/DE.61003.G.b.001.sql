INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.61003.G.b.001',
    'geometry-check',
    'AX_DammWallDeich',
    '{
        "conditions": {
            "all": [
                { "type": "geom_type", "value": "Polygon" },
                { "type": "tag_in", "tag_key": "funktion", "values": ["3002", "3003"] }
            ]
        },
        "checks": {
            "type": "spatial_compare",
            "operator": "contains",
            "data_set_filter": {
                "criteria": {
                    "any": [
                        { "type": "tag_in", "tag_key": "object_type", "values": [ "AX_Strassenachse", "AX_Fahrwegachse", "AX_Bahnstrecke", "AX_Gleis" ] },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_WegPfadSteig" },
                                { "type": "tag_in", "tag_key": "art", "values": [ "1106", "1110" ] }
                            ]
                        }
                    ]
                }
            }
        }
    }',
    'Ein flächenförmiges Objekt ''AX_DammWallDeich'' mit ''funktion'' 3002 oder 3003 muss innerhalb ein Objekt ''AX_Strassenachse'', ''AX_Fahrwegachse'', ''AX_Bahnstrecke'', ''AX_Gleis'' oder ''AX_WegPfadSteig'' mit ''art'' 1106 oder 1110 haben.')
ON CONFLICT (id) DO NOTHING;
