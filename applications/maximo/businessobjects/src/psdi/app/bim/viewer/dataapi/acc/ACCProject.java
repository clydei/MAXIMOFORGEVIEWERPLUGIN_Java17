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
import java.util.Map;
import java.util.Objects;

/**
 * Java 17 Record representing an Autodesk Construction Cloud project.
 * Immutable data transfer object for ACC project information.
 * 
 * @param id The unique project ID in ACC
 * @param name The project name
 * @param accountId The ACC account ID this project belongs to
 * @param type The project type (e.g., "BIM360", "ACC")
 * @param status The project status
 * @param createdAt When the project was created
 * @param updatedAt When the project was last updated
 * @param attributes Additional project attributes
 */
public record ACCProject(
    String id,
    String name,
    String accountId,
    String type,
    String status,
    Instant createdAt,
    Instant updatedAt,
    Map<String, String> attributes
) {
    public ACCProject {
        Objects.requireNonNull(id, "Project ID cannot be null");
        Objects.requireNonNull(name, "Project name cannot be null");
        Objects.requireNonNull(accountId, "Account ID cannot be null");
        
        // Set defaults
        type = (type != null) ? type : "ACC";
        status = (status != null) ? status : "active";
        attributes = (attributes != null) ? Map.copyOf(attributes) : Map.of();
    }
    
    /**
     * Convenience constructor without timestamps.
     */
    public ACCProject(String id, String name, String accountId, String type) {
        this(id, name, accountId, type, "active", Instant.now(), Instant.now(), Map.of());
    }
    
    /**
     * Get an attribute value.
     */
    public String getAttribute(String key) {
        return attributes.get(key);
    }
    
    /**
     * Check if project is active.
     */
    public boolean isActive() {
        return "active".equalsIgnoreCase(status);
    }
}

// Made with Bob
