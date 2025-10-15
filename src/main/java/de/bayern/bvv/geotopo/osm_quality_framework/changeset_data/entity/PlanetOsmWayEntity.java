package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;

@Entity(name = "PlanetWaysChangesetData")
@Table(name = "planet_osm_ways", schema = "changeset_data")
public class PlanetOsmWayEntity {
    @Id
    @Column(name = "id")
    private Long id;
    
    @Column(name = "nodes")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private Long[] nodes;

    @Column(name = "tags")
    @JdbcTypeCode(SqlTypes.JSON)
    private HashMap<String, String> tags;
}
