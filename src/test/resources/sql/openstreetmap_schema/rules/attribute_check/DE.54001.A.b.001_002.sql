INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.54001.A.b.001_002',
    'attribute-check',
    'AX_Vegetationsmerkmal',
    '{
        "conditions": { "not": { "type": "tag_equals", "tag_key": "zustand", "value": "5000" } },
        "checks": { "type": "tag_exists", "tag_key": "bewuchs" }
    }',
    'Das Tag ''bewuchs'' ist immer zu belegen, wenn der ''zustand'' nicht mit 5000 belegt ist.')
ON CONFLICT (id) DO NOTHING;


