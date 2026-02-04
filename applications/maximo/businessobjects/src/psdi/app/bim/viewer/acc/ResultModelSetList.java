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
import psdi.app.bim.viewer.dataapi.acc.ACCModelSet;

/**
 * Result class for ACC model set list operations.
 */
public class ResultModelSetList extends Result {
    
    private List<ACCModelSet> modelSets;
    private String nextPageToken;
    private int totalCount;
    
    public ResultModelSetList() {
        super();
        this.modelSets = new ArrayList<>();
        this.totalCount = 0;
    }
    
    public List<ACCModelSet> getModelSets() {
        return modelSets;
    }
    
    public void setModelSets(List<ACCModelSet> modelSets) {
        this.modelSets = modelSets;
        if (modelSets != null) {
            this.totalCount = modelSets.size();
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
    
    public void addModelSet(ACCModelSet modelSet) {
        if (this.modelSets == null) {
            this.modelSets = new ArrayList<>();
        }
        this.modelSets.add(modelSet);
        this.totalCount = this.modelSets.size();
    }
}

// Made with Bob
