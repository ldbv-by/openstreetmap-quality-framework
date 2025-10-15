INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53009.A.c.001',
    'attribute-check',
    'AX_BauwerkImGewaesserbereich',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "zustand" },
        "checks": { "type": "tag_between", "tag_key": "bauwerksfunktion", "from_value": "2030", "to_value": "2090" }
    }',
    'Das Tag ''zustand'' darf nur bei der ''bauwerksfunktion'' 2030 bis 2090 vorkommen.')
ON CONFLICT (id) DO NOTHING;

