package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.service;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.config.ToolConfig;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.CommandResponseDto;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.SortOscDto;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.model.CommandRequest;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.model.OsmosisCommand;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.api.OsmosisService;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.util.OsmosisCommandBuilder;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.util.ZipHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for Osmosis commands.
 */
@Service
@RequiredArgsConstructor
public class OsmosisServiceImpl implements OsmosisService {

    private final ToolConfig toolConfig;

    /**
     * Execute an Osmosis command.
     */
    private CommandResponseDto runCommand(CommandRequest commandRequest) {
        OsmosisCommand osmosisCommand = new OsmosisCommand(this.toolConfig.getOsmosis(), commandRequest);
        String commandOutput = osmosisCommand.execute();

        // Check if command created results
        ZipHelper.Zip zipResult = ZipHelper.createZipForCommandResult(osmosisCommand);

        if (zipResult != null) {
            return new CommandResponseDto(commandOutput, zipResult.name(), zipResult.resource(), osmosisCommand.getDownloadFilesDirectory());
        } else {
            return new CommandResponseDto(commandOutput, null, null, osmosisCommand.getDownloadFilesDirectory());
        }
    }

    /**
     * Sorts OSM change files.
     */
    @Override
    public CommandResponseDto sortOscFiles(SortOscDto sortOscDto) {
        return this.runCommand(OsmosisCommandBuilder.sortOscFiles(sortOscDto));
    }
}
