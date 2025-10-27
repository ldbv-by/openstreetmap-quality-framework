INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44004.B.c.001',
    'attribute-check',
    'AX_Gewaesserachse',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "breiteDesGewaessers" },
        "checks": {
            "all": [
                { "type": "number_compare", "tag_key": "breiteDesGewaessers", "operator": ">=", "compare_value": "3" },
                { "type": "number_compare", "tag_key": "breiteDesGewaessers", "operator": "%", "compare_value": "3" },
                { "type": "number_compare", "tag_key": "breiteDesGewaessers", "operator": "<=", "compare_value": "12" }
            ]
        }
    }',
    'Das Attribut ''breiteDesGewaessers'' muss immer mit dem Klassenwert 6, 9 oder 12 belegt sein.')
ON CONFLICT (id) DO NOTHING;
