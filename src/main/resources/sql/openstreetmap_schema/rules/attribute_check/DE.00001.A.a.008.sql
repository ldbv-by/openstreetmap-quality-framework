INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.00001.A.a.008',
    'attribute-check',
    'AA_Objekt',
    '{
        "checks": { "type": "tag_regex_match", "tag_key": "identifikator:UUIDundZeit", "pattern": "^[\\w]{16}(20[0-9]{6}T[0-9]{6}Z)?$" }
    }',
    'Das Tag ''identifikator:UUIDundZeit'' muss dem regulären Ausdruck ^[\w]{16}(20[0-9]{6}T[0-9]{6}Z)?$ entsprechen.')
ON CONFLICT (id) DO NOTHING;
