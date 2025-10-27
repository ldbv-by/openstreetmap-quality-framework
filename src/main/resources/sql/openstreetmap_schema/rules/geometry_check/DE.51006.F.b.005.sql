INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51006.F.b.005',
    'geometry-check',
    'AX_BauwerkOderAnlageFuerSportFreizeitUndErholung',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1510" },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_SportFreizeitUndErholungsflaeche|AX_Landwirtschaft|AX_Wald|AX_Gehoelz|AX_Heide|AX_Moor|AX_Sumpf|AX_UnlandVegetationsloseFlaeche|AX_StehendesGewaesser" } } }
        }
    }',
    'Ein Objekt mit der ''bauwerksfunktion'' 1510 darf nur auf ''AX_SportFreizeitUndErholungsflaeche'', ''AX_Landwirtschaft'', ''AX_Wald'', ''AX_Gehoelz'', ''AX_Heide'', ''AX_Moor'', ''AX_Sumpf'', ''AX_UnlandVegetationsloseFlaeche''  oder ''AX_StehendesGewaesser'' liegen.')
ON CONFLICT (id) DO NOTHING;