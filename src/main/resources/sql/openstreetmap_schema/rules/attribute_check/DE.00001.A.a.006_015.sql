INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.00001.A.a.006_015',
    'attribute-check',
    'AA_Objekt',
    '{
        "checks": {
            "all": [
                {"type": "tag_unique", "tag_key": "advStandardModell" },
                {"type": "tag_unique", "tag_key": "sonstigesModell" }
            ]
        }
    }',
    'Eine Modellart muss innerhalb eines Objektes eindeutig sein.')
ON CONFLICT (id) DO NOTHING;
