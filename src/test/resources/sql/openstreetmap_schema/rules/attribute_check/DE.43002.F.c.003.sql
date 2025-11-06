INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.43002.F.c.003',
    'attribute-check',
    'AX_Wald',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "zustand", "value": "7100" },
        "checks": { "not": { "type": "tag_exists", "tag_key": "vegetationsmerkmal" } }
    }',
    'Ein Objekt ''AX_Wald'' mit ''zustand'' 7100 darf kein Tag ''vegetationsmerkmal'' haben.')
ON CONFLICT (id) DO NOTHING;