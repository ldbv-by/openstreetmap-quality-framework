INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42002.F.b.002',
    'attribute-check',
    'AX_Strasse',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "internationaleBedeutung", "value": "2001" },
        "checks": { "type": "tag_in", "tag_key": "widmung", "values": ["1301", "1303"] }
    }',
    'Das Tag ''internationaleBedeutung'' kann nur vorkommen, wenn das Tag ''widmung'' 1301 oder 1303 ist.')
ON CONFLICT (id) DO NOTHING;
