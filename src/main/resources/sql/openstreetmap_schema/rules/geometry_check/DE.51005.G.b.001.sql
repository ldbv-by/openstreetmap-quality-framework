INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51005.G.b.001',
    'geometry-check',
    'AX_Leitung',
    '{
        "checks": {
            "way_nodes": {
                "all": [
                    { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkOderAnlageFuerIndustrieUndGewerbe" },
                    { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1251" }
                ]
            }
        }
    }',
    'Ein Objekt ''AX_Leitung'' muss an einen Knickpunkt stets einen Freileitungsmast haben.')
ON CONFLICT (id) DO NOTHING;