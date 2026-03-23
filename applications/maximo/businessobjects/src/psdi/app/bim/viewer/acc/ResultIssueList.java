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
package psdi.app.bim.viewer.acc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import psdi.app.bim.viewer.dataapi.Result;
import psdi.app.bim.viewer.dataapi.acc.ACCIssue;

/**
 * Result class for ACC issue list operations.
 */
public class ResultIssueList extends Result {
    
    private List<ACCIssue> issues;
    private String nextPageToken;
    private int totalCount;
    
    public ResultIssueList() {
        super();
        this.issues = new ArrayList<>();
        this.totalCount = 0;
    }
    
    public List<ACCIssue> getIssues() {
        return issues;
    }
    
    public void setIssues(List<ACCIssue> issues) {
        this.issues = issues;
        if (issues != null) {
            this.totalCount = issues.size();
        }
        // Invalidate caches when issues are updated
        invalidateCaches();
    }
    
    public String getNextPageToken() {
        return nextPageToken;
    }
    
    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public boolean hasMorePages() {
        return nextPageToken != null && !nextPageToken.isEmpty();
    }
    
    public void addIssue(ACCIssue issue) {
        if (this.issues == null) {
            this.issues = new ArrayList<>();
        }
        this.issues.add(issue);
        this.totalCount = this.issues.size();
        // Invalidate caches when issue is added
        invalidateCaches();
    }
    
    /**
     * Get only open issues.
     * Performance: Cached result, computed once on first access.
     *
     * @return Immutable list of open issues
     */
    public List<ACCIssue> getOpenIssues() {
        if (openIssues == null) {
            if (issues == null) {
                openIssues = List.of();
            } else {
                openIssues = issues.stream()
                    .filter(ACCIssue::isOpen)
                    .toList(); // Java 17 - returns immutable list
            }
        }
        return openIssues;
    }
    
    /**
     * Get only overdue issues.
     * Performance: Cached result, uses parallel stream for large datasets.
     *
     * @return Immutable list of overdue issues
     */
    public List<ACCIssue> getOverdueIssues() {
        if (overdueIssues == null) {
            if (issues == null) {
                overdueIssues = List.of();
            } else {
                // Use parallel stream for large datasets
                if (issues.size() > 1000) {
                    overdueIssues = issues.parallelStream()
                        .filter(ACCIssue::isOverdue)
                        .toList();
                } else {
                    overdueIssues = issues.stream()
                        .filter(ACCIssue::isOverdue)
                        .toList();
                }
            }
        }
        return overdueIssues;
    }
    
    /**
     * Get only high priority issues.
     * Performance: Cached result, computed once on first access.
     *
     * @return Immutable list of high priority issues
     */
    public List<ACCIssue> getHighPriorityIssues() {
        if (highPriorityIssues == null) {
            if (issues == null) {
                highPriorityIssues = List.of();
            } else {
                highPriorityIssues = issues.stream()
                    .filter(ACCIssue::isHighPriority)
                    .toList();
            }
        }
        return highPriorityIssues;
    }
    
    /**
     * Get count of open issues.
     * Performance: Cached count, computed efficiently without creating list.
     *
     * @return Count of open issues
     */
    public int getOpenCount() {
        if (openCount == null) {
            if (issues == null) {
                openCount = 0;
            } else {
                openCount = (int) issues.stream()
                    .filter(ACCIssue::isOpen)
                    .count();
            }
        }
        return openCount;
    }
    
    /**
     * Get count of overdue issues.
     * Performance: Cached count, computed efficiently without creating list.
     *
     * @return Count of overdue issues
     */
    public int getOverdueCount() {
        if (overdueCount == null) {
            if (issues == null) {
                overdueCount = 0;
            } else {
                overdueCount = (int) issues.stream()
                    .filter(ACCIssue::isOverdue)
                    .count();
            }
        }
        return overdueCount;
    }
    
    /**
     * Get count of high priority issues.
     * Performance: Cached count, computed efficiently without creating list.
     *
     * @return Count of high priority issues
     */
    public int getHighPriorityCount() {
        if (highPriorityCount == null) {
            if (issues == null) {
                highPriorityCount = 0;
            } else {
                highPriorityCount = (int) issues.stream()
                    .filter(ACCIssue::isHighPriority)
                    .count();
            }
        }
        return highPriorityCount;
    }
    
    /**
     * Get issues by status.
     * Performance: Direct filtering, not cached (less common use case).
     *
     * @param status The status to filter by
     * @return List of issues with the specified status
     */
    public List<ACCIssue> getIssuesByStatus(String status) {
        if (issues == null || status == null) {
            return List.of();
        }
        return issues.stream()
            .filter(issue -> status.equals(issue.status()))
            .toList();
    }
    
    /**
     * Get issues by priority.
     * Performance: Direct filtering, not cached (less common use case).
     *
     * @param priority The priority to filter by
     * @return List of issues with the specified priority
     */
    public List<ACCIssue> getIssuesByPriority(String priority) {
        if (issues == null || priority == null) {
            return List.of();
        }
        return issues.stream()
            .filter(issue -> priority.equals(issue.priority()))
            .toList();
    }
    
    /**
     * Get issues assigned to a specific user.
     * Performance: Direct filtering, not cached (less common use case).
     *
     * @param assignedTo The user to filter by
     * @return List of issues assigned to the user
     */
    public List<ACCIssue> getIssuesByAssignee(String assignedTo) {
        if (issues == null || assignedTo == null) {
            return List.of();
        }
        return issues.stream()
            .filter(issue -> assignedTo.equals(issue.assignedTo()))
            .toList();
    }
    
    /**
     * Invalidate all cached results.
     * Called when the issue list is modified.
     */
    private void invalidateCaches() {
        this.openIssues = null;
        this.overdueIssues = null;
        this.highPriorityIssues = null;
        this.openCount = null;
        this.overdueCount = null;
        this.highPriorityCount = null;
    }
}

// Made with Bob
