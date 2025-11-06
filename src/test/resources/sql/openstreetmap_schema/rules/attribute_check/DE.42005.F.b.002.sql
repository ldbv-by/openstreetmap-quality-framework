INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42005.F.b.002',
    'attribute-check',
    'AX_Fahrbahnachse',
    '{
        "conditions": {
            "relations": {
                "loop_info": { "type": "any" },
                "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strasse" },
                "checks": { "type": "tag_in", "tag_key": "widmung", "values": ["1301", "1303", "1305", "1306"] }
            }
        },
        "checks": { "type": "tag_exists", "tag_key": "breiteDerFahrbahn" }
    }',
    'Bei einem Objekt ''AX_Fahrbahnachse'' muss das Tag ''breiteDerFahrbahn'' belegt sein, wenn diese in einer Relation ''AX_Strasse'' mit ''widmung'' 1301, 1303, 1305 oder 1306 liegt.')
ON CONFLICT (id) DO NOTHING;