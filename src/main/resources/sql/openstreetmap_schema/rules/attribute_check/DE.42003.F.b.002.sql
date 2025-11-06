INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42003.F.b.002',
    'attribute-check',
    'AX_Strassenachse',
    '{
        "conditions": {
            "relations": {
                "loop_info": { "type": "any" },
                "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strasse" },
                "checks": {
                    "all": [
                        { "type": "tag_in", "tag_key": "widmung", "values": ["1301", "1303", "1305", "1306"] },
                        {
                            "relation_members": {
                                "loop_info": { "type": "none" },
                                "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fahrbahnachse" }
                            }
                        }
                    ]
                }
            }
        },
        "checks": { "type": "tag_exists", "tag_key": "breiteDerFahrbahn" }
    }',
    'Das Tag ''breiteDerFahrbahn'' muss belegt sein, wenn eine ''AX_Strassenachse'' in einer Relation ''AX_Strasse'' mit ''widmung'' 1301, 1303, 1305 oder 1306 und ohne ''AX_Fahrbahnachse'' liegt.')
ON CONFLICT (id) DO NOTHING;