INSERT INTO openstreetmap_schema.tags (object_type, tag_key, multiplicity, tag_datatype_id)

-- object_type
SELECT ot.object_type, 'object_type', '1',    'CharacterString'
  FROM openstreetmap_schema.object_types ot
  UNION ALL

-- type
SELECT ot.object_type, 'type','0..1', 'CharacterString'
  FROM openstreetmap_schema.object_types ot
ON CONFLICT (object_type, tag_key) DO NOTHING;