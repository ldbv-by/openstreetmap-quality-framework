package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.util;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Feature;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.TaggedObject;
import lombok.experimental.UtilityClass;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ChangesetEditor {

    public OsmPrimitive getOsmPrimitive(TaggedObject taggedObject, Changeset changeset) {
        OsmPrimitive osmPrimitive;

        Class<? extends OsmPrimitive> primitiveClass;
        if (taggedObject instanceof Feature feature) {
            switch (feature.getGeometry()) {
                case Point _ -> primitiveClass = Node.class;
                case LineString _ -> primitiveClass = Way.class;
                case Polygon polygon -> {
                    boolean hasInnerRings = polygon.getNumInteriorRing() > 0;

                    if (hasInnerRings) {
                        primitiveClass = Relation.class;
                    } else {
                        primitiveClass = Way.class;
                    }
                }
                case null, default -> throw new IllegalArgumentException("Unsupported geometry type");
            }
        } else {
            primitiveClass = Relation.class;
        }

        if (taggedObject.getOsmId() < 0) {
            osmPrimitive = changeset.getCreatePrimitives().stream()
                    .filter(osm -> osm.getId().equals(taggedObject.getOsmId()))
                    .filter(primitiveClass::isInstance)
                    .findFirst()
                    .orElse(null);
        } else {
            osmPrimitive = changeset.getModifyPrimitives().stream()
                    .filter(osm -> osm.getId().equals(taggedObject.getOsmId()))
                    .filter(primitiveClass::isInstance)
                    .findFirst()
                    .orElse(null);

            if (osmPrimitive == null) {
                osmPrimitive = changeset.getDeletePrimitives().stream()
                        .filter(osm -> osm.getId().equals(taggedObject.getOsmId()))
                        .filter(primitiveClass::isInstance)
                        .findFirst()
                        .orElse(null);
            }
        }

        if (osmPrimitive != null) {
            return osmPrimitive;
        } else {
            throw new IllegalArgumentException("Tagged object %d in changeset not found".formatted(taggedObject.getOsmId()));
        }
    }

    public void upsertTag(OsmPrimitive osmPrimitive, String key, String value) {
        List<Tag> tags = osmPrimitive.getTags();

        if (!(tags instanceof ArrayList)) {
            tags = new ArrayList<>(tags == null ? List.of() : tags);
            osmPrimitive.setTags(tags);
        }

        tags.removeIf(tag -> tag.getK().equals(key));
        tags.add(new Tag(key, value));
    }
}
