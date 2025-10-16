INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.43002.A.b.003',
    'attribute-check',
    'AX_Wald',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "zustand" },
        "checks": { "type": "tag_exists", "tag_key": "nutzung" }
    }',
    'Das Tag ''zustand'' kann nur in Verbindung mit dem Tag ''nutzung'' vorkommen.')
ON CONFLICT (id) DO NOTHING;