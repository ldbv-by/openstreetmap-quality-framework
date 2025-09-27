package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.model;

/**
 * A specific Osm2PgSql command.
 */
public class Osm2PgSqlCommand extends Command{

    /**
     * Creates an Osm2PgSql command.
     */
    public Osm2PgSqlCommand(String executable, CommandRequest commandRequest) {
        super("osm2pgsql", executable, commandRequest);
    }
}
