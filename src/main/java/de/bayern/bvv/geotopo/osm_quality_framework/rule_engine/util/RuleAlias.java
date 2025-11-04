package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import lombok.experimental.UtilityClass;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class RuleAlias {

    public DataSetFilter replaceDataSetFilter(DataSetFilter dataSetFilter, TaggedObject taggedObject) {
        return replaceDataSetFilter(dataSetFilter, taggedObject, false);
    }

    public DataSetFilter replaceDataSetFilter(DataSetFilter dataSetFilter, TaggedObject taggedObject, boolean selfCheck) {
        if (dataSetFilter == null) return null;

        boolean osmIdsAreSet = dataSetFilter.osmIds() != null;
        Set<Long> nodeIds = osmIdsAreSet ? dataSetFilter.osmIds().nodeIds() : null;
        Set<Long> wayIds = osmIdsAreSet ? dataSetFilter.osmIds().wayIds() : null;
        Set<Long> areaIds = osmIdsAreSet ? dataSetFilter.osmIds().areaIds() : null;
        Set<Long> relationIds = osmIdsAreSet ? dataSetFilter.osmIds().relationIds() : null;

        // Add osm id of tagged object
        if (selfCheck) {
            nodeIds = new HashSet<>();
            wayIds = new HashSet<>();
            areaIds = new HashSet<>();
            relationIds = new HashSet<>();

            if (taggedObject instanceof Feature feature) {
                if (feature.getGeometry() instanceof Point) {
                    nodeIds.add(feature.getOsmId());
                } else if (feature.getGeometry() instanceof LineString) {
                    wayIds.add(feature.getOsmId());
                } else if (feature.getGeometry() instanceof Polygon) {
                    areaIds.add(feature.getOsmId());
                }
            } else if (taggedObject instanceof Relation relation) {
                relationIds.add(relation.getOsmId());
            }
        }

        OsmIds resolvedOsmIds = new OsmIds(nodeIds, wayIds, areaIds, relationIds);

        // Replace alias name, e.g. current, ...
        Criteria resolvedCriteria = (dataSetFilter.criteria() == null) ? null :
                resolveCriteriaAliases(dataSetFilter.criteria(), taggedObject);

        return new DataSetFilter(
                dataSetFilter.ignoreChangesetData(),
                dataSetFilter.coordinateReferenceSystem(),
                dataSetFilter.aggregator(),
                resolvedOsmIds,
                resolvedCriteria,
                dataSetFilter.memberFilter()
        );
    }

    private Criteria resolveCriteriaAliases(Criteria criteria, TaggedObject taggedObject) {
        if (criteria instanceof Leaf leaf) {
            Map<String, Object> params = leaf.params();
            if (params == null || params.isEmpty() || taggedObject == null) return leaf;

            Map<String, Object> newParams = new LinkedHashMap<>(params.size());
            boolean changed = false;
            for (Map.Entry<String, Object> e : params.entrySet()) {
                Object v = resolveValue(e.getValue(), taggedObject);
                if (v != e.getValue()) changed = true;
                newParams.put(e.getKey(), v);
            }
            return changed ? new Leaf(leaf.type(), newParams) : leaf;
        }

        if (criteria instanceof All all) {
            List<Criteria> items = all.items();
            if (items == null || items.isEmpty()) return all;
            List<Criteria> newItems = items.stream()
                    .map(item -> resolveCriteriaAliases(item, taggedObject))
                    .collect(Collectors.toList());

            return new All(newItems);
        }

        if (criteria instanceof Any all) {
            List<Criteria> items = all.items();
            if (items == null || items.isEmpty()) return all;
            List<Criteria> newItems = items.stream()
                    .map(item -> resolveCriteriaAliases(item, taggedObject))
                    .collect(Collectors.toList());

            return new Any(newItems);
        }

        if (criteria instanceof Not not) {
            Criteria inner = resolveCriteriaAliases(not.expr(), taggedObject);
            return new Not(inner);
        }

        return criteria;
    }

    private Object resolveValue(Object value, TaggedObject tagged) {
        switch (value) {
            case null -> {
                return null;
            }

            case String str -> {
                if (str.startsWith("current:")) {
                    String key = str.substring("current:".length());
                    if (key.isBlank() || tagged.getTags() == null) return null;
                    return tagged.getTags().get(key);
                }
                return str;
            }

            case List<?> list -> {
                boolean changed = false;
                List<Object> out = new ArrayList<>(list.size());
                for (Object item : list) {
                    Object r = resolveValue(item, tagged);
                    if (r != item) changed = true;
                    out.add(r);
                }
                return changed ? out : value;
            }

            case Map<?, ?> map -> {
                boolean changed = false;
                Map<String, Object> out = new LinkedHashMap<>(map.size());
                for (Map.Entry<?, ?> me : map.entrySet()) {
                    String k = String.valueOf(me.getKey());
                    Object r = resolveValue(me.getValue(), tagged);
                    if (r != me.getValue()) changed = true;
                    out.put(k, r);
                }
                return changed ? out : value;
            }

            default -> {}
        }

        return value;
    }
}
