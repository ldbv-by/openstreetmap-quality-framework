INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42003.A.a.001',
    'attribute-check',
    'AX_Strassenachse',
    '{
        "conditions": {
            "relations": {
                "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strasse" },
                "checks": {
                    "relation_members": {
                        "loop_info": { "type": "any" },
                        "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fahrbahnachse" }
                    }
                }
            }
        },
        "checks": {
            "all": [
                { "not": { "type": "tag_exists", "tag_key": "besondereFahrstreifen" } },
                { "not": { "type": "tag_exists", "tag_key": "breiteDerFahrbahn" } },
                { "not": { "type": "tag_exists", "tag_key": "funktion" } },
                { "not": { "type": "tag_exists", "tag_key": "anzahlDerFahrstreifen" } },
                { "not": { "type": "tag_exists", "tag_key": "zustand" } },
                { "not": { "type": "tag_exists", "tag_key": "oberflaechenmaterial" } }
            ]
        }
    }',
    'Die Tags ''besondereFahrstreifen'', ''breiteDerFahrbahn'', ''funktion'', ''anzahlDerFahrstreifen'', ''zustand'' und ''oberflaechenmaterial'' dürfen nicht belegt sein, wenn eine ''AX_Strassenachse'' in einer Relation ''AX_Strasse'' mit ''AX_Fahrbahnachse'' liegt.')
ON CONFLICT (id) DO NOTHING;