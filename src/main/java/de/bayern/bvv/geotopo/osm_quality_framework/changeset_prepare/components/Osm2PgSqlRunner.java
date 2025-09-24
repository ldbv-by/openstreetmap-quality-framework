package de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.components;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.OsmPrimitive;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Relation;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Way;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.util.ChangesetXml;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class Osm2PgSqlRunner {

    public void appendChangeset(Changeset changeset) {
        this.normalizeChangeset(changeset);

        Path osc = ChangesetXml.toXmlFile(changeset);

        List<String> cmd = List.of(
                "osm2pgsql",
                "--append",
                "--slim",
                "--database=osm_quality_framework",
                "--schema=changeset_prepare_" + changeset.getId(),
                "--user=osm_quality_framework_user",
                "--host=openstreetmap-quality-framework-db",
                "--port=5432",
                "--output=flex",
                "--style=/app/import/openstreetmap_geometries.lua",
                osc.toString()
        );

        try {
            Process process = new ProcessBuilder(cmd).inheritIO().start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
               throw new IllegalStateException("Osm2PgSqlRunner failed with exit code " + exitCode);
            }

            Files.deleteIfExists(osc);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Delete not ok");
        }
    }

    /**
     * Normalize changeset for tool osm2pgsql.
     */
    private void normalizeChangeset(Changeset changesetPrepare) {

        // Use only positive Ids in osm2pgsql.
        for (OsmPrimitive osmPrimitive : changesetPrepare.getAllPrimitives()) {
            // Set Ids to positive number.
            if (osmPrimitive.getId() < 0) {
                osmPrimitive.setId((long) Math.pow(10, 17) + Math.abs(osmPrimitive.getId()));
            }

            // Set Nd to positive number.
            if (osmPrimitive instanceof Way way) {
                for (Way.Nd wayNode : way.getNodeRefs()) {
                    if (wayNode.getRef() < 0) {
                        wayNode.setRef((long) Math.pow(10, 17) + Math.abs(wayNode.getRef()));
                    }
                }
            }

            // Set Relation Members to positive number.
            if (osmPrimitive instanceof Relation relation) {
                for (Relation.Member member : relation.getMembers()) {
                    if (member.getRef() < 0) {
                        member.setRef((long) Math.pow(10, 17) + Math.abs(member.getRef()));
                    }
                }
            }
        }

        // Version is necessary.
        changesetPrepare.getCreatePrimitives().forEach(p -> p.setVersion(0L));

        // Increase Version on all objects.
        changesetPrepare.getAllPrimitives().forEach(p -> p.setVersion(p.getVersion() + 1L));
    }
}
