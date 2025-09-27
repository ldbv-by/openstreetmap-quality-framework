package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.util;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.SortOscDto;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.model.CommandRequest;
import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Command Builder for Osmosis.
 */
@UtilityClass
public class OsmosisCommandBuilder {

    /**
     * Get command for sorting Osm Change files.
     */
    public CommandRequest sortOscFiles(SortOscDto sortOscDto) {
        List<String> args = new ArrayList<>();

        for (Path oscPath : sortOscDto.oscPaths()) {
            String name = oscPath.getFileName().toString();
            String sorted = name.replaceFirst("\\.osc$", "") + "_sorted.osc";

            args.add("--read-xml-change");
            args.add("file=" + name);
            args.add("enableDateParsing=no");
            args.add("--sort-change");
            args.add("--write-xml-change");
            args.add("file=" + sorted);
        }

        CommandRequest commandRequest = new CommandRequest();
        commandRequest.setArgs(args);
        commandRequest.setUploadPaths(sortOscDto.oscPaths());
        return commandRequest;
    }
}
