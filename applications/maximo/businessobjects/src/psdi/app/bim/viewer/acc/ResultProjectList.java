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

import psdi.app.bim.viewer.dataapi.Result;
import psdi.app.bim.viewer.dataapi.acc.ACCProject;

/**
 * Result class for ACC project list operations.
 * Contains a list of ACC projects and pagination information.
 */
public class ResultProjectList extends Result {
    
    private List<ACCProject> projects;
    private String nextPageToken;
    private int totalCount;
    
    public ResultProjectList() {
        super();
        this.projects = new ArrayList<>();
        this.totalCount = 0;
    }
    
    /**
     * Get the list of projects.
     * 
     * @return List of ACCProject objects
     */
    public List<ACCProject> getProjects() {
        return projects;
    }
    
    /**
     * Set the list of projects.
     * 
     * @param projects List of ACCProject objects
     */
    public void setProjects(List<ACCProject> projects) {
        this.projects = projects;
        if (projects != null) {
            this.totalCount = projects.size();
        }
    }
    
    /**
     * Get the next page token for pagination.
     * 
     * @return Next page token or null if no more pages
     */
    public String getNextPageToken() {
        return nextPageToken;
    }
    
    /**
     * Set the next page token.
     * 
     * @param nextPageToken The pagination token
     */
    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
    
    /**
     * Get the total count of projects.
     * 
     * @return Total number of projects
     */
    public int getTotalCount() {
        return totalCount;
    }
    
    /**
     * Set the total count of projects.
     * 
     * @param totalCount Total number of projects
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    /**
     * Check if there are more pages available.
     * 
     * @return true if more pages exist
     */
    public boolean hasMorePages() {
        return nextPageToken != null && !nextPageToken.isEmpty();
    }
    
    /**
     * Add a project to the list.
     * 
     * @param project The project to add
     */
    public void addProject(ACCProject project) {
        if (this.projects == null) {
            this.projects = new ArrayList<>();
        }
        this.projects.add(project);
        this.totalCount = this.projects.size();
    }
}

// Made with Bob
