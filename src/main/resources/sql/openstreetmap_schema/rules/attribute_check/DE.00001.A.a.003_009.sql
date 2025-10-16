INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.00001.A.a.003_009',
    'attribute-check',
    'AA_Objekt',
    '{
        "checks": { "type": "tag_regex_match", "tag_key": "identifikator:UUID", "pattern": "(^DE(BW|BU|BY|ST|SL|SH|NI|BE|BB|NW|RP|HE|MV|SN|HH|HB|TH)[A-Za-z0-9]{12})|(^DEBKG[A-Za-z0-9]{11})|(^DE_[A-Za-z0-9]{13}$)" }
    }',
    'Das Tag ''identifikator:UUID'' muss dem regulären Ausdruck (^DE(BW|BU|BY|ST|SL|SH|NI|BE|BB|NW|RP|HE|MV|SN|HH|HB|TH)[A-Za-z0-9]{12})|(^DEBKG[A-Za-z0-9]{11})|(^DE_[A-Za-z0-9]{13}$) entsprechen.')
ON CONFLICT (id) DO NOTHING;
