package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Object Type.
 */
@Data
public class ObjectType {
    private String name;
    private List<Tag> tags = new ArrayList<>();
    private List<Rule> rules = new ArrayList<>();

    /**
     * Get all tag keys.
     */
    public Set<String> getTagKeys() {
        Set<String> tagKeys = new HashSet<>();
        collectTagKeys("", this.tags, tagKeys);

        return tagKeys;
    }

    /**
     * Get all tag groups.
     */
    public Set<String> getTagGroups() {
        Set<String> tagGroups = new HashSet<>();
        collectTagGroups("", this.tags, tagGroups);

        return tagGroups;
    }

    /**
     * Recursive collect tag keys.
     */
    private void collectTagKeys(String prefix, List<Tag> tags, Set<String> tagKeys) {
        for (Tag tag : tags) {
            String fullKey = prefix.isEmpty() ? tag.getKey() : prefix + ":" + tag.getKey();

            if (tag.getType() == Tag.Type.COMPLEX) {
                this.collectTagKeys(fullKey, tag.getSubTags(), tagKeys);
            } else {
                tagKeys.add(fullKey);
            }
        }
    }

    /**
     * Recursive collect tag groups.
     */
    private void collectTagGroups(String prefix, List<Tag> tags, Set<String> tagGroups) {
        for (Tag tag : tags) {
            String fullKey = prefix.isEmpty() ? tag.getKey() : prefix + ":" + tag.getKey();

            if (tag.getType() == Tag.Type.COMPLEX) {
                tagGroups.add(fullKey);
                this.collectTagGroups(fullKey, tag.getSubTags(), tagGroups);
            }
        }
    }

}
