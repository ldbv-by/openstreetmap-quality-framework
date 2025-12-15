INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53002.G.b.002',
    'geometry-check',
    'AX_Strassenverkehrsanlage',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "art", "value": "2000" },
        "checks": {
            "not": {
                "type": "spatial_compare",
                "operator": "covered_by",
                "data_set_filter": {
                    "criteria": {
                        "all": [
                            { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkImGewaesserbereich" },
                            { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["2010", "2011", "2012", "2013"] }
                        ]
                    }
                }
            }
        }
    }',
    'Furt darf ein unter der Erdoberfläche verlaufendes Gewässer nicht überlagern.')
ON CONFLICT (id) DO NOTHING;