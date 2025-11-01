INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.71009.G.b.001',
    'geometry-check',
    'AX_Denkmalschutzrecht',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "artDerFestlegung", "value": "2711" },
        "checks": {
            "any": [
                {
                    "type": "spatial_compare",
                    "operator": "equals_topo",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Gebaeude|AX_Turm|AX_HistorischesBauwerkOderHistorischeEinrichtung" } } }
                },
                {
                    "type": "spatial_compare",
                    "operator": "equals_topo",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_SonstigesBauwerkOderSonstigeEinrichtung", "bauwerksfunktion": "1750|1760|1770" } } }
                }
            ]
        }
    }',
    'Ein Objekt ''AX_Denkmalschutzrecht'' mit ''artDerFestlegung'' 2711 muss geometrieidentisch mit einem Objekt ''AX_Gebaeude'', ''AX_Turm'', ''AX_HistorischesBauwerkOderHistorischeEinrichtung'' oder ''AX_SonstigesBauwerkOderSonstigeEinrichtung'' mit ''bauwerksfunktion'' 1750, 1760 oder 1770 sein.')
ON CONFLICT (id) DO NOTHING;

