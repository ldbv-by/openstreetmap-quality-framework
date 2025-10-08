INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.02300.A.a.001',
    'attribute-check',
    'AP_GPO',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "signaturnummer" },
        "checks": { "type": "regex_match", "tag_key": "signaturnummer", "pattern": "^([0-9]{4,5})$|^((BKG|BW|BU|BY|ST|SL|SH|NI|BE|BB|NW|RP|HE|MV|SN|HH|HB|TH)[0-9]{4})$" }
    }',
    'Das Tag signaturnummer dem regulären Ausdruck ^([0-9]{4,5})$|^((BKG|BW|BU|BY|ST|SL|SH|NI|BE|BB|NW|RP|HE|MV|SN|HH|HB|TH)[0-9]{4})$ entsprechen.')
ON CONFLICT (id) DO NOTHING;
