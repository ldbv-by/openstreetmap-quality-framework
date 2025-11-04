INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.31001.F.c.001',
    'attribute-check',
    'AX_Gebaeude',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "gebaeudekennzeichen" },
        "checks": { "type": "tag_regex_match", "tag_key": "gebaeudekennzeichen", "pattern": "^(0[1-9]|1[0-6])[0-9]{6}[^\\s]{16}$" }
    }',
    'Das Tag ''gebaeudekennzeichen'' muss den regulären Ausdruck ''^(0[1-9]|1[0-6])[0-9]{6}[^\s]{16}$'' entsprechen.')
ON CONFLICT (id) DO NOTHING;
