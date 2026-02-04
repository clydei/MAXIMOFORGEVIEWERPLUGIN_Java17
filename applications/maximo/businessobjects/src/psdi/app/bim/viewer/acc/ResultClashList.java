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
import psdi.app.bim.viewer.dataapi.acc.ACCClash;

/**
 * Result class for ACC clash list operations.
 * Contains a list of clashes from model coordination.
 */
public class ResultClashList extends Result {
    
    private List<ACCClash> clashes;
    private String nextPageToken;
    private int totalCount;
    private String modelSetId;
    
    public ResultClashList() {
        super();
        this.clashes = new ArrayList<>();
        this.totalCount = 0;
    }
    
    public List<ACCClash> getClashes() {
        return clashes;
    }
    
    public void setClashes(List<ACCClash> clashes) {
        this.clashes = clashes;
        if (clashes != null) {
            this.totalCount = clashes.size();
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
    
    public String getModelSetId() {
        return modelSetId;
    }
    
    public void setModelSetId(String modelSetId) {
        this.modelSetId = modelSetId;
    }
    
    public boolean hasMorePages() {
        return nextPageToken != null && !nextPageToken.isEmpty();
    }
    
    public void addClash(ACCClash clash) {
        if (this.clashes == null) {
            this.clashes = new ArrayList<>();
        }
        this.clashes.add(clash);
        this.totalCount = this.clashes.size();
    }
    
    /**
     * Get only unresolved clashes.
     */
    public List<ACCClash> getUnresolvedClashes() {
        if (clashes == null) {
            return new ArrayList<>();
        }
        return clashes.stream()
            .filter(clash -> !clash.isResolved())
            .collect(Collectors.toList());
    }
    
    /**
     * Get only critical clashes.
     */
    public List<ACCClash> getCriticalClashes() {
        if (clashes == null) {
            return new ArrayList<>();
        }
        return clashes.stream()
            .filter(ACCClash::isCritical)
            .collect(Collectors.toList());
    }
    
    /**
     * Get count of unresolved clashes.
     */
    public int getUnresolvedCount() {
        return getUnresolvedClashes().size();
    }
    
    /**
     * Get count of critical clashes.
     */
    public int getCriticalCount() {
        return getCriticalClashes().size();
    }
}

// Made with Bob
