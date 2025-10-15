package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class RelationMemberId implements Serializable {
    private Long relationOsmId;
    private String memberType;
    private Long memberOsmId;
}
