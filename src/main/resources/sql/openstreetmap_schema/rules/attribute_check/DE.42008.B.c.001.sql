INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42008.B.c.001',
    'attribute-check',
    'AX_Fahrwegachse',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "breiteDesVerkehrsweges" },
        "checks": {
            "all": [
                { "type": "number_compare", "tag_key": "breiteDesVerkehrsweges", "operator": ">=", "compare_value": "6" },
                { "type": "number_compare", "tag_key": "breiteDesVerkehrsweges", "operator": "%", "compare_value": "3" }
            ]
        }
    }',
    'Das Attribut ''breiteDesVerkehrsweges'' muss immer mit dem Klassenwert 6, 9, 12 oder 15 usw. belegt sein.')
ON CONFLICT (id) DO NOTHING;
