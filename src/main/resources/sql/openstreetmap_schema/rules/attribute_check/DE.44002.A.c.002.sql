INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44002.A.c.002',
    'attribute-check',
    'AX_Wasserlauf',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "widmung" },
        "checks": {
            "any": [
                {
                    "type": "tag_exists", "tag_key": "gewaesserkennzahl"
                },
                {
                    "relation_members": {
                        "loop_info": { "type": "none" },
                        "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gewaesserachse" },
                        "checks": { "type": "tag_exists", "tag_key": "hydrologischesMerkmal" }
                    }
                }
            ]
        }
    }',
    'Das Tag ''widmung'' wird nur belegt, wenn ''gewaesserkennzahl'' belegt ist und/oder die zur Relation gehörenden ''AX_Gewaesserachse'' nicht das Tag ''hydrologischesMerkmal'' führen.')
ON CONFLICT (id) DO NOTHING;