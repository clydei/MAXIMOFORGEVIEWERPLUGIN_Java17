/**
 * Copyright IBM Corporation 2009-2026
 *
 * Licensed under the Eclipse Public License - v 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @Author ACC Plugin Development Team
 * @Since Java 17
 **/
package psdi.app.bim.viewer.dataapi.acc;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Java 17 Record representing a model coordination clash in ACC.
 * 
 * @param id The unique clash ID
 * @param name The clash name/description
 * @param modelSetId The model set this clash belongs to
 * @param status The clash status (new, active, reviewed, resolved, closed)
 * @param severity The clash severity (critical, major, minor)
 * @param elementIds List of element IDs involved in the clash
 * @param location The 3D location of the clash
 * @param detectedAt When the clash was detected
 * @param assignedTo User assigned to resolve the clash
 */
public record ACCClash(
    String id,
    String name,
    String modelSetId,
    String status,
    String severity,
    List<String> elementIds,
    ACCClashLocation location,
    Instant detectedAt,
    String assignedTo
) {
    public ACCClash {
        Objects.requireNonNull(id, "Clash ID cannot be null");
        Objects.requireNonNull(modelSetId, "Model set ID cannot be null");
        
        // Set defaults
        name = (name != null) ? name : "Clash " + id;
        status = (status != null) ? status : "new";
        severity = (severity != null) ? severity : "minor";
        elementIds = (elementIds != null) ? List.copyOf(elementIds) : List.of();
        detectedAt = (detectedAt != null) ? detectedAt : Instant.now();
    }
    
    /**
     * Check if clash is resolved.
     */
    public boolean isResolved() {
        return "resolved".equalsIgnoreCase(status) || "closed".equalsIgnoreCase(status);
    }
    
    /**
     * Check if clash is critical.
     */
    public boolean isCritical() {
        return "critical".equalsIgnoreCase(severity);
    }
    
    /**
     * Get the number of elements involved.
     */
    public int getElementCount() {
        return elementIds.size();
    }
}

/**
 * Record representing the 3D location of a clash.
 * 
 * @param x X coordinate
 * @param y Y coordinate
 * @param z Z coordinate
 * @param viewpointUrn URN of the saved viewpoint
 */
record ACCClashLocation(
    double x,
    double y,
    double z,
    String viewpointUrn
) {
    public ACCClashLocation {
        // Validation if needed
    }
    
    /**
     * Get coordinates as array.
     */
    public double[] toArray() {
        return new double[]{x, y, z};
    }
}

// Made with Bob
