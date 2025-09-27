package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.util;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.AppendChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.CreateSchemaDto;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.model.CommandRequest;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command Builder for Osm2PgSql.
 */
@UtilityClass
public class Osm2PgSqlCommandBuilder {

    /**
     * Get command for creating a new schema.
     */
    public CommandRequest createSchema(CreateSchemaDto createSchemaDto) {

        List<String> args = List.of(
                "--create",
                "--slim",
                "--database=" + createSchemaDto.database(),
                "--schema=" + createSchemaDto.databaseSchema(),
                "--user=" + createSchemaDto.databaseUsername(),
                "--host=" + createSchemaDto.databaseHost(),
                "--port=" + createSchemaDto.databasePort(),
                "--output=flex",
                "--style=" + createSchemaDto.luaPath().getFileName().toString(),
                createSchemaDto.pbfPath().getFileName().toString()
        );

        Map<String, String> environments = new HashMap<>();
        environments.put("PGPASSWORD", createSchemaDto.databasePassword());
        environments.put("PGHOST", createSchemaDto.databaseHost());
        environments.put("PGPORT", createSchemaDto.databasePort());
        environments.put("PGUSER", createSchemaDto.databaseUsername());
        environments.put("PGDATABASE", createSchemaDto.database());

        CommandRequest commandRequest = new CommandRequest();
        commandRequest.setArgs(args);
        commandRequest.setEnvironments(environments);
        commandRequest.setUploadPaths(List.of(createSchemaDto.pbfPath(), createSchemaDto.luaPath()));

        return commandRequest;
    }

                                       /**
     * Get command for appending a changeset.
     */
    public CommandRequest appendChangeset(AppendChangesetDto appendChangesetDto, Path sortedOscPath) {

        List<String> args = List.of(
                "--append",
                "--slim",
                "--database=" + appendChangesetDto.database(),
                "--schema=" + appendChangesetDto.databaseSchema(),
                "--user=" + appendChangesetDto.databaseUsername(),
                "--host=" + appendChangesetDto.databaseHost(),
                "--port=" + appendChangesetDto.databasePort(),
                "--output=flex",
                "--style=" + appendChangesetDto.luaPath().getFileName().toString(),
                sortedOscPath.getFileName().toString()
        );

        Map<String, String> environments = new HashMap<>();
        environments.put("PGPASSWORD", appendChangesetDto.databasePassword());
        environments.put("PGHOST", appendChangesetDto.databaseHost());
        environments.put("PGPORT", appendChangesetDto.databasePort());
        environments.put("PGUSER", appendChangesetDto.databaseUsername());
        environments.put("PGDATABASE", appendChangesetDto.database());

        CommandRequest commandRequest = new CommandRequest();
        commandRequest.setArgs(args);
        commandRequest.setEnvironments(environments);
        commandRequest.setUploadPaths(List.of(sortedOscPath, appendChangesetDto.luaPath()));

        return commandRequest;
    }
}
