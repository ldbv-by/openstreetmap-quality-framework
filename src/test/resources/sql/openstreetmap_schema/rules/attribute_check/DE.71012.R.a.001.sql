INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.71012.R.a.001',
    'attribute-check',
    'AX_Schutzzone',
    '{
        "checks": {
            "relations": {
                "loop_info": { "type": "any" },
                "checks": { "type": "tag_in", "tag_key": "object_type", "values": [ "AX_SchutzgebietNachWasserrecht", "AX_SchutzgebietNachNaturUmweltOderBodenschutzrecht"] }
            }
        }
    }',
    'Schutzzone hat keine Relation zu SchutzgebietNachWasserrecht oder SchutzgebietNachNaturUmweltOderBodenschutzrecht.')
ON CONFLICT (id) DO NOTHING;
