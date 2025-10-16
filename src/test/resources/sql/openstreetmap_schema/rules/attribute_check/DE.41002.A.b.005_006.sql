INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.41002.A.b.005_006',
    'attribute-check',
    'AX_IndustrieUndGewerbeflaeche',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "lagergut" },
        "checks": { "type": "tag_equals", "tag_key": "funktion", "value": "1740" }
    }',
    'Das Tag ''lagergut'' darf nur bei der ''funktion'' 1740 vorkommen.')
ON CONFLICT (id) DO NOTHING;