INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.75009.G.b.004_005_006_007',
    'geometry-check',
    'AX_Gebietsgrenze',
    '{
        "conditions": { "type": "tag_in", "tag_key": "artDerGebietsgrenze", "values": [ "7101", "7102" ] },
        "checks": {
            "not": { "type": "spatial_compare", "operator": "crosses" }
        }
    }',
    'Alle Objekte müssen an der Landesgrenze enden.')
ON CONFLICT (id) DO NOTHING;


