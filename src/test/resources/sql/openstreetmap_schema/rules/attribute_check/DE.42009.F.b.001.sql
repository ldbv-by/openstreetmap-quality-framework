INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42009.F.b.001',
    'attribute-check',
    'AX_Platz',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "funktion", "value": "5330" },
        "checks": {
            "all": [
                { "type": "tag_exists", "tag_key": "name:unverschluesselt" },
                { "type": "tag_exists", "tag_key": "name:verschluesselt:land" },
                { "type": "tag_exists", "tag_key": "name:verschluesselt:kreis" },
                { "type": "tag_exists", "tag_key": "name:verschluesselt:gemeinde" },
                { "type": "tag_exists", "tag_key": "name:verschluesselt:lage" }
            ]
        }
    }',
    'Beim Objekt ''AX_Platz'' mit ''funktion'' 5330 muss das komplexe Tag ''name'' belegt sein.')
ON CONFLICT (id) DO NOTHING;