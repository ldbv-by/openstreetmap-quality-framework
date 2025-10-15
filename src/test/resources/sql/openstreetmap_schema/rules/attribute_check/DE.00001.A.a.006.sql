INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.00001.A.a.006',
    'attribute-check',
    'AA_Objekt',
    '{
        "checks": {
            "all": [
                {"type": "unique_check", "tag_key": "advStandardModell", "scope": "internal" },
                {"type": "unique_check", "tag_key": "sonstigesModell", "scope": "internal" }
            ]
        }
    }',
    'Eine Modellart muss innerhalb eines Objektes eindeutig sein.')
ON CONFLICT (id) DO NOTHING;
