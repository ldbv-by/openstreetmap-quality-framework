INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51006.G.b.001',
    'geometry-check',
    'AX_BauwerkOderAnlageFuerSportFreizeitUndErholung',
    '{
        "conditions": { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["1431", "1432"] },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_FlaecheBesondererFunktionalerPraegung|AX_SportFreizeitUndErholungsflaeche" } } }
        }
    }',
    'Die Wertearten mit der ''bauwerksfunktion'' 1431 und 1432 müssen ''AX_FlaecheBesondererFunktionalerPraegung'' oder ''AX_SportFreizeitUndErholungsflaeche'' überlagern.')
ON CONFLICT (id) DO NOTHING;