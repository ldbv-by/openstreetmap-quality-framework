package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.object_number_assignment.component;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.api.OsmGeometriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObjectNumberGenerator {

    private final OsmGeometriesService osmGeometriesService;

    private static final String IDENTIFIER_PREFIX = "DEBYBDLM";

    private static final String[] ENCRYPTION_ARRAY =
    {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
    };

    /**
     * Get next Object Number.
     */
    public String getNextIdentifier() {
        Long nextIdentifierSequence = this.osmGeometriesService.getNextIdentifierSequence();
        return String.format(IDENTIFIER_PREFIX + this.encryptIdentifierSequence(nextIdentifierSequence));
    }

    /**
     * Encrypt Object Number Sequence.
     */
    private String encryptIdentifierSequence(long identifierSequence) {
        StringBuilder objectNumberSequenceString = new StringBuilder();

        int idx;
        for (int exponent = 7; exponent >= 0; exponent--) {
            if (exponent > 0) {
                idx = (int) (identifierSequence / Math.pow(ENCRYPTION_ARRAY.length, exponent));
                identifierSequence = (long) (identifierSequence - (Math.pow(ENCRYPTION_ARRAY.length, exponent) * idx));
            } else {
                idx = (int) (identifierSequence);
            }

            objectNumberSequenceString.append(ENCRYPTION_ARRAY[idx]);
        }

        return objectNumberSequenceString.toString();
    }

}
