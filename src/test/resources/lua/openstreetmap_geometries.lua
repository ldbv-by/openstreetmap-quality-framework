-- For debugging
print('osm2pgsql version: ' .. osm2pgsql.version)

local srid = 4326
local tables = {}

tables.nodes = osm2pgsql.define_table({
	name = 'nodes',
	ids = { type = 'node', id_column = 'osm_id' },
    columns = {
        { column = 'version', type = 'int' },
	    { column = 'changeset_id', type = 'bigint' },
        { column = 'object_type', type = 'text' },
	    { column = 'tags', type = 'jsonb' },
	    { column = 'geom', type = 'geometry', not_null = true, projection = srid }
    }
})


tables.ways = osm2pgsql.define_table({
	name = 'ways',
	ids = { type = 'way', id_column = 'osm_id' },
	columns = {
		{ column = 'version', type = 'int' },
		{ column = 'changeset_id', type = 'bigint' },
		{ column = 'object_type', type = 'text' },
		{ column = 'tags', type = 'jsonb' },
		{ column = 'geom', type = 'geometry', not_null = true, projection = srid }
	}
})


tables.areas = osm2pgsql.define_table({
	name = 'areas',
	ids = { type = 'any', id_column = 'osm_id', type_column = 'osm_geometry_type' },
	columns = {
		{ column = 'version', type = 'int' },
		{ column = 'changeset_id', type = 'bigint' },
		{ column = 'object_type', type = 'text' },
		{ column = 'tags', type = 'jsonb' },
		{ column = 'geom', type = 'geometry', not_null = true, projection = srid }
	}
})


tables.relation_members = osm2pgsql.define_table({
	name = 'relation_members',
	ids = { type = 'relation', id_column = 'relation_osm_id' },
	columns = {
		{ column = 'member_type', type = 'text' },
		{ column = 'member_role', type = 'text' },
		{ column = 'member_osm_id', type = 'bigint' },
	},
	indexes = {
		{ column = { 'relation_osm_id' }, method = 'btree' },
		{ column = { 'member_type', 'member_osm_id' }, method = 'btree' }
	}
})


tables.relations = osm2pgsql.define_table({
	name = 'relations',
	ids = { type = 'relation', id_column = 'osm_id' },
	columns = {
		{ column = 'version', type = 'int' },
		{ column = 'changeset_id', type = 'bigint' },
		{ column = 'object_type', type = 'text' },
		{ column = 'members', type = 'jsonb' },
		{ column = 'tags', type = 'jsonb' }
	}
})


-- Called for every node in the input. The `object` argument contains all the attributes of the node
-- like `id`, `version`, etc. as well as all tags as a Lua table (`object.tags`).
function osm2pgsql.process_node(object)
	--  Uncomment next line to look at the object data:
	local object_type = object.tags and object.tags.object_type

	tables.nodes:insert({
		version = object.version,
		changeset_id = object.changeset,
		object_type = object_type,
		tags = object.tags,
		geom = object:as_point()
	})
end

-- Called for every way in the input. The `object` argument contains the same information as with nodes
-- and additionally a boolean `is_closed` flag and the list of node IDs referenced by the way (`object.nodes`).
function osm2pgsql.process_way(object)
    local object_type = object.tags and object.tags.object_type

	local tag_count = 0
    for _ in pairs(object.tags) do
        tag_count = tag_count + 1
    end

    if tag_count == 1 and object.tags.type ~= nil then
        -- object is outer boundary of multipolygon
        return
    end

	if object.is_closed then
        tables.areas:insert({
			version = object.version,
			changeset_id = object.changeset,
			object_type = object_type,
			tags = object.tags,
			geom = object:as_polygon()
		})
	else
		tables.ways:insert({
			version = object.version,
			changeset_id = object.changeset,
			object_type = object_type,
			tags = object.tags,
			geom = object:as_linestring()
		})
	end
end

-- Called for every relation in the input. The `object` argument contains the same information as with nodes
-- and additionally an array of members (`object.members`).
function osm2pgsql.process_relation(object)
    local object_type = object.tags and object.tags.object_type

	if object.tags.type == 'multipolygon' then
		tables.areas:insert({
			version = object.version,
			changeset_id = object.changeset,
			object_type = object_type,
			tags = object.tags,
			geom = object:as_multipolygon()
		})

		return
	end

	tables.relations:insert({
		version = object.version,
		changeset_id = object.changeset,
		object_type = object_type,
		members = object.members,
		tags = object.tags
	})

	for _, member in ipairs(object.members) do
		tables.relation_members:insert({
			relation_osm_id = object.id,
			member_type = member.type,
			member_role = member.role,
			member_osm_id = member.ref
		})
	end

end

