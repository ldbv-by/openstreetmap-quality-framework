INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.43002.F.c.002',
    'attribute-check',
    'AX_Wald',
    '{
        "conditions": { "not": { "type": "tag_equals", "tag_key": "zustand", "value": "7100" } },
        "checks": { "type": "tag_exists", "tag_key": "vegetationsmerkmal" }
    }',
    'Ein Objekt ''AX_Wald'' ohne ''zustand'' 7100 muss ein Tag ''vegetationsmerkmal'' haben.')
ON CONFLICT (id) DO NOTHING;