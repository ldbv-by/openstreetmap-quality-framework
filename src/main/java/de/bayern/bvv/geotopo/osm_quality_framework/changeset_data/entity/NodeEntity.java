package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity;

import com.bedatadriven.jackson.datatype.jts.serialization.GeometryDeserializer;
import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.locationtech.jts.geom.Geometry;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "NodesChangesetData")
@Table(name = "nodes", schema = "changeset_data")
@Data
public class NodeEntity extends OsmObjectEntity {

    @Column(name = "geom", columnDefinition = "Geometry(Geometry, 4326)")
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(contentUsing = GeometryDeserializer.class)
    private Geometry geom;
}
