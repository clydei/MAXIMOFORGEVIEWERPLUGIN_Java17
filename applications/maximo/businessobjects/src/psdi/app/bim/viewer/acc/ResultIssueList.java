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
    }
    
    /**
     * Get only open issues.
     */
    public List<ACCIssue> getOpenIssues() {
        if (issues == null) {
            return new ArrayList<>();
        }
        return issues.stream()
            .filter(ACCIssue::isOpen)
            .collect(Collectors.toList());
    }
    
    /**
     * Get only overdue issues.
     */
    public List<ACCIssue> getOverdueIssues() {
        if (issues == null) {
            return new ArrayList<>();
        }
        return issues.stream()
            .filter(ACCIssue::isOverdue)
            .collect(Collectors.toList());
    }
    
    /**
     * Get only high priority issues.
     */
    public List<ACCIssue> getHighPriorityIssues() {
        if (issues == null) {
            return new ArrayList<>();
        }
        return issues.stream()
            .filter(ACCIssue::isHighPriority)
            .collect(Collectors.toList());
    }
}

// Made with Bob
