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
 * Java 17 Record representing an ACC issue.
 * 
 * @param id The unique issue ID
 * @param title The issue title
 * @param description The issue description
 * @param projectId The ACC project ID
 * @param status The issue status
 * @param priority The issue priority
 * @param assignedTo User assigned to the issue
 * @param issueType The type of issue
 * @param location The issue location in the model
 * @param attachments List of attachment URNs
 * @param createdAt When the issue was created
 * @param dueDate When the issue is due
 */
public record ACCIssue(
    String id,
    String title,
    String description,
    String projectId,
    String status,
    String priority,
    String assignedTo,
    String issueType,
    ACCIssueLocation location,
    List<String> attachments,
    Instant createdAt,
    Instant dueDate
) {
    public ACCIssue {
        Objects.requireNonNull(id, "Issue ID cannot be null");
        Objects.requireNonNull(title, "Issue title cannot be null");
        Objects.requireNonNull(projectId, "Project ID cannot be null");
        
        // Set defaults
        status = (status != null) ? status : "open";
        priority = (priority != null) ? priority : "normal";
        issueType = (issueType != null) ? issueType : "general";
        attachments = (attachments != null) ? List.copyOf(attachments) : List.of();
        createdAt = (createdAt != null) ? createdAt : Instant.now();
    }
    
    /**
     * Check if issue is open.
     */
    public boolean isOpen() {
        return "open".equalsIgnoreCase(status) || "in_progress".equalsIgnoreCase(status);
    }
    
    /**
     * Check if issue is overdue.
     */
    public boolean isOverdue() {
        return dueDate != null && Instant.now().isAfter(dueDate) && isOpen();
    }
    
    /**
     * Check if issue is high priority.
     */
    public boolean isHighPriority() {
        return "high".equalsIgnoreCase(priority) || "critical".equalsIgnoreCase(priority);
    }
}

/**
 * Record representing the location of an issue in the model.
 * 
 * @param modelUrn The model URN
 * @param elementIds List of element IDs related to the issue
 * @param viewpoint The saved viewpoint
 */
record ACCIssueLocation(
    String modelUrn,
    List<String> elementIds,
    ACCViewpoint viewpoint
) {
    public ACCIssueLocation {
        elementIds = (elementIds != null) ? List.copyOf(elementIds) : List.of();
    }
}

/**
 * Record representing a saved viewpoint.
 * 
 * @param position Camera position [x, y, z]
 * @param target Camera target [x, y, z]
 * @param up Camera up vector [x, y, z]
 * @param fov Field of view
 */
record ACCViewpoint(
    double[] position,
    double[] target,
    double[] up,
    double fov
) {
    public ACCViewpoint {
        Objects.requireNonNull(position, "Position cannot be null");
        Objects.requireNonNull(target, "Target cannot be null");
        Objects.requireNonNull(up, "Up vector cannot be null");
        
        if (position.length != 3) {
            throw new IllegalArgumentException("Position must have 3 coordinates");
        }
        if (target.length != 3) {
            throw new IllegalArgumentException("Target must have 3 coordinates");
        }
        if (up.length != 3) {
            throw new IllegalArgumentException("Up vector must have 3 coordinates");
        }
    }
}

// Made with Bob
