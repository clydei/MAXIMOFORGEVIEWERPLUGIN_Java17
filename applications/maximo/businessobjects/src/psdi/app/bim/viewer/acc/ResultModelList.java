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

/**
 * Result class for ACC model list operations.
 * Simplified model representation for listing.
 */
public class ResultModelList extends Result {
    
    private List<ACCModelInfo> models;
    private String nextPageToken;
    private int totalCount;
    
    public ResultModelList() {
        super();
        this.models = new ArrayList<>();
        this.totalCount = 0;
    }
    
    public List<ACCModelInfo> getModels() {
        return models;
    }
    
    public void setModels(List<ACCModelInfo> models) {
        this.models = models;
        if (models != null) {
            this.totalCount = models.size();
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
    
    public void addModel(ACCModelInfo model) {
        if (this.models == null) {
            this.models = new ArrayList<>();
        }
        this.models.add(model);
        this.totalCount = this.models.size();
    }
    
    /**
     * Simple model info class for list results.
     */
    public static class ACCModelInfo {
        private String id;
        private String name;
        private String urn;
        private String version;
        private String fileType;
        
        public ACCModelInfo() {}
        
        public ACCModelInfo(String id, String name, String urn) {
            this.id = id;
            this.name = name;
            this.urn = urn;
        }
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getUrn() { return urn; }
        public void setUrn(String urn) { this.urn = urn; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public String getFileType() { return fileType; }
        public void setFileType(String fileType) { this.fileType = fileType; }
    }
}

// Made with Bob
