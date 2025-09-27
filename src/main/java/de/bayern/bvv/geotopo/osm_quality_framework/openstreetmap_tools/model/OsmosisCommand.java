package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.model;

/**
 * A specific Osmosis command.
 */
public class OsmosisCommand extends Command{

    /**
     * Creates an Osmosis command.
     */
    public OsmosisCommand(String executable, CommandRequest commandRequest) {
        super("osmosis", executable, commandRequest);
    }
}
