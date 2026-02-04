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
 * Java 17 Record representing an ACC model set for coordination.
 * A model set is a collection of models used together for clash detection.
 * 
 * @param id The unique model set ID
 * @param name The model set name
 * @param projectId The ACC project ID
 * @param modelIds List of model IDs in this set
 * @param createdAt When the model set was created
 * @param status The model set status
 */
public record ACCModelSet(
    String id,
    String name,
    String projectId,
    List<String> modelIds,
    Instant createdAt,
    String status
) {
    public ACCModelSet {
        Objects.requireNonNull(id, "Model set ID cannot be null");
        Objects.requireNonNull(name, "Model set name cannot be null");
        Objects.requireNonNull(projectId, "Project ID cannot be null");
        
        modelIds = (modelIds != null) ? List.copyOf(modelIds) : List.of();
        createdAt = (createdAt != null) ? createdAt : Instant.now();
        status = (status != null) ? status : "active";
    }
    
    /**
     * Get the number of models in this set.
     */
    public int getModelCount() {
        return modelIds.size();
    }
    
    /**
     * Check if the model set is active.
     */
    public boolean isActive() {
        return "active".equalsIgnoreCase(status);
    }
    
    /**
     * Check if a specific model is in this set.
     */
    public boolean containsModel(String modelId) {
        return modelIds.contains(modelId);
    }
}

// Made with Bob
