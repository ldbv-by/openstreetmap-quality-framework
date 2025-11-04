INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.57001.F.c.002',
    'attribute-check',
    'AX_Wasserspiegelhoehe',
    '{
        "checks": { "type": "tag_regex_match", "tag_key": "hoeheDesWasserspiegels", "pattern": "^[-]?(\\d+(\\.([0-9]0*)?)?|\\.[0-9]0*)$" }
    }',
    'Die Wasserspiegelhöhe ist in [m] auf [dm] gerundet anzugeben.')
ON CONFLICT (id) DO NOTHING;