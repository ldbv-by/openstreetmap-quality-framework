INSERT INTO openstreetmap_schema.tags (object_type, tag_key, multiplicity, tag_datatype_id) VALUES
    ('AA_Objekt', 'object_type', '1', 'CharacterString'),
    ('AA_Objekt', 'type', '0..1', 'CharacterString')
ON CONFLICT (object_type, tag_key) DO NOTHING;