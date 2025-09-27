package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.service;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.config.ToolConfig;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.AppendChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.CommandResponseDto;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.CreateSchemaDto;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.SortOscDto;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.model.CommandRequest;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.model.Osm2PgSqlCommand;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.spi.Osm2PgSqlService;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.spi.OsmosisService;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.util.Osm2PgSqlCommandBuilder;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.util.ZipHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

/**
 * Service for Osm2PgSql commands.
 */
@Service
@RequiredArgsConstructor
public class Osm2PgSqlServiceImpl implements Osm2PgSqlService {

    private final OsmosisService osmosisService;
    private final ToolConfig toolConfig;

    /**
     * Execute an Osm2PgSql command.
     */
    private CommandResponseDto runCommand(CommandRequest commandRequest) {
        Osm2PgSqlCommand osm2pgsqlCommand = new Osm2PgSqlCommand(this.toolConfig.getOsm2PgSql(), commandRequest);
        String commandOutput = osm2pgsqlCommand.execute();

        // Check if command created results
        ZipHelper.Zip zipResult = ZipHelper.createZipForCommandResult(osm2pgsqlCommand);

        if (zipResult != null) {
            return new CommandResponseDto(commandOutput, zipResult.name(), zipResult.resource(), osm2pgsqlCommand.getDownloadFilesDirectory());
        } else {
            return new CommandResponseDto(commandOutput, null, null, osm2pgsqlCommand.getDownloadFilesDirectory());
        }
    }

    /**
     * Creates a new schema with Osm2PgSql.
     */
    @Override
    public CommandResponseDto createSchema(CreateSchemaDto createSchemaDto) {
        return this.runCommand(Osm2PgSqlCommandBuilder.createSchema(createSchemaDto));
    }

    /**
     * Appends a changeset to existing database schema.
     * Changeset will be automatically sorted.
     */
    @Override
    public CommandResponseDto appendChangeset(AppendChangesetDto appendChangesetDto) {
        // Sort OSM change file
        SortOscDto sortOscDto = new SortOscDto(List.of(appendChangesetDto.oscPath()));
        CommandResponseDto sortResponseDto = this.osmosisService.sortOscFiles(sortOscDto);

        String sortedName = appendChangesetDto.oscPath()
                .getFileName().toString()
                .replaceFirst("(?i)\\.osc$", "") + "_sorted.osc";

        Path sortedOscPath = sortResponseDto.downloadDirectory().resolve(sortedName);

        // Append sorted changeset to database
        return this.runCommand(Osm2PgSqlCommandBuilder.appendChangeset(appendChangesetDto, sortedOscPath));
    }
}
