INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.00001.A.a.007_014',
    'attribute-check',
    'AA_zeigtAufExternes',
    '{
        "checks": { "type": "uri_valid", "tag_key": "art", "urn_pattern": "^urn:(bkg|bw|bu|by|st|sl|sh|ni|be|bb|nw|rp|he|mv|sn|hh|hb|th):fdv:[0-9]{4}$" }
    }',
    'Das Tag ''art'' muss eine gültige http-Url sein oder dem regulären Ausdruck ^urn:(bkg|bw|bu|by|st|sl|sh|ni|be|bb|nw|rp|he|mv|sn|hh|hb|th):fdv:[0-9]{4}$ entsprechen.')
ON CONFLICT (id) DO NOTHING;
