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

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

import psdi.app.bim.viewer.dataapi.Result;
import psdi.app.bim.viewer.dataapi.ResultAuthentication;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.security.UserInfo;
import psdi.server.AppServiceRemote;
import psdi.util.MXException;

/**
 * Remote interface for the Autodesk Construction Cloud (ACC) service.
 * Provides methods for interacting with ACC API for model coordination.
 * 
 * Pattern follows LMVServiceRemote for consistency with existing Forge integration.
 */
public interface ACCServiceRemote extends AppServiceRemote {
    
    // ========== Authentication ==========
    
    /**
     * Authenticate with ACC using OAuth 2.0.
     * 
     * @param scopes Array of OAuth scopes to request
     * @return ResultAuthentication containing access token
     * @throws IOException If network error occurs
     * @throws URISyntaxException If URI is malformed
     */
    ResultAuthentication authenticate(String[] scopes) 
        throws IOException, URISyntaxException;
    
    /**
     * Get a cached authentication token for viewer.
     * 
     * @return JSON string containing auth token
     * @throws IOException If network error occurs
     * @throws URISyntaxException If URI is malformed
     */
    String getAuthToken() 
        throws IOException, URISyntaxException;
    
    /**
     * Clear the authentication token cache.
     * 
     * @throws RemoteException If RMI error occurs
     */
    void clearAuthCache() 
        throws RemoteException;
    
    // ========== Project Management ==========
    
    /**
     * List all ACC projects for an account.
     * 
     * @param accountId The ACC account ID
     * @return ResultProjectList containing projects
     * @throws IOException If network error occurs
     * @throws URISyntaxException If URI is malformed
     */
    ResultProjectList projectList(String accountId) 
        throws IOException, URISyntaxException;
    
    /**
     * Get details for a specific project.
     * 
     * @param accountId The ACC account ID
     * @param projectId The project ID
     * @return ResultProjectDetail containing project details
     * @throws IOException If network error occurs
     * @throws URISyntaxException If URI is malformed
     */
    ResultProjectDetail projectQueryDetails(String accountId, String projectId) 
        throws IOException, URISyntaxException;
    
    /**
     * Link an ACC project to Maximo.
     * 
     * @param userInfo The user context
     * @param accountId The ACC account ID
     * @param projectId The ACC project ID
     * @param projectName The project name
     * @param orgId The Maximo organization ID
     * @param siteId The Maximo site ID
     * @return The created ACCProject MBO
     * @throws RemoteException If RMI error occurs
     * @throws MXException If Maximo error occurs
     */
    MboRemote linkProject(
        UserInfo userInfo,
        String accountId,
        String projectId,
        String projectName,
        String orgId,
        String siteId
    ) throws RemoteException, MXException;
    
    // ========== Model Coordination ==========
    
    /**
     * List models in a project.
     * 
     * @param projectId The ACC project ID
     * @return ResultModelList containing models
     * @throws IOException If network error occurs
     * @throws URISyntaxException If URI is malformed
     */
    ResultModelList modelList(String projectId) 
        throws IOException, URISyntaxException;
    
    /**
     * List model sets in a project.
     * 
     * @param projectId The ACC project ID
     * @return ResultModelSetList containing model sets
     * @throws IOException If network error occurs
     * @throws URISyntaxException If URI is malformed
     */
    ResultModelSetList modelSetList(String projectId) 
        throws IOException, URISyntaxException;
    
    /**
     * Get details for a specific model set.
     * 
     * @param projectId The ACC project ID
     * @param modelSetId The model set ID
     * @return ResultModelSetDetail containing model set details
     * @throws IOException If network error occurs
     * @throws URISyntaxException If URI is malformed
     */
    ResultModelSetDetail modelSetQueryDetails(String projectId, String modelSetId) 
        throws IOException, URISyntaxException;
    
    // ========== Clash Detection ==========
    
    /**
     * List clashes for a model set.
     * 
     * @param projectId The ACC project ID
     * @param modelSetId The model set ID
     * @return ResultClashList containing clashes
     * @throws IOException If network error occurs
     * @throws URISyntaxException If URI is malformed
     */
    ResultClashList clashList(String projectId, String modelSetId) 
        throws IOException, URISyntaxException;
    
    /**
     * Get details for a specific clash.
     * 
     * @param projectId The ACC project ID
     * @param clashId The clash ID
     * @return ResultClashDetail containing clash details
     * @throws IOException If network error occurs
     * @throws URISyntaxException If URI is malformed
     */
    ResultClashDetail clashQueryDetails(String projectId, String clashId) 
        throws IOException, URISyntaxException;
    
    /**
     * Update clash status.
     * 
     * @param projectId The ACC project ID
     * @param clashId The clash ID
     * @param status The new status
     * @return Result indicating success/failure
     * @throws IOException If network error occurs
     * @throws URISyntaxException If URI is malformed
     */
    Result clashUpdateStatus(String projectId, String clashId, String status) 
        throws IOException, URISyntaxException;
    
    // ========== Issue Management ==========
    
    /**
     * List issues in a project.
     * 
     * @param projectId The ACC project ID
     * @return ResultIssueList containing issues
     * @throws IOException If network error occurs
     * @throws URISyntaxException If URI is malformed
     */
    ResultIssueList issueList(String projectId) 
        throws IOException, URISyntaxException;
    
    /**
     * Get details for a specific issue.
     * 
     * @param projectId The ACC project ID
     * @param issueId The issue ID
     * @return ResultIssueDetail containing issue details
     * @throws IOException If network error occurs
     * @throws URISyntaxException If URI is malformed
     */
    ResultIssueDetail issueQueryDetails(String projectId, String issueId) 
        throws IOException, URISyntaxException;
    
    /**
     * Create a new issue in ACC.
     * 
     * @param projectId The ACC project ID
     * @param title The issue title
     * @param description The issue description
     * @param priority The issue priority
     * @param assignedTo User to assign the issue to
     * @return Result containing created issue ID
     * @throws IOException If network error occurs
     * @throws URISyntaxException If URI is malformed
     */
    Result issueCreate(
        String projectId,
        String title,
        String description,
        String priority,
        String assignedTo
    ) throws IOException, URISyntaxException;
    
    /**
     * Update an existing issue.
     * 
     * @param projectId The ACC project ID
     * @param issueId The issue ID
     * @param status The new status
     * @return Result indicating success/failure
     * @throws IOException If network error occurs
     * @throws URISyntaxException If URI is malformed
     */
    Result issueUpdate(String projectId, String issueId, String status) 
        throws IOException, URISyntaxException;
    
    // ========== Maximo Integration ==========
    
    /**
     * Create a Maximo work order from an ACC clash.
     * 
     * @param userInfo The user context
     * @param clashId The ACC clash ID
     * @return The created work order MBO
     * @throws RemoteException If RMI error occurs
     * @throws MXException If Maximo error occurs
     */
    MboRemote createWorkOrderFromClash(UserInfo userInfo, String clashId) 
        throws RemoteException, MXException;
    
    /**
     * Create an ACC issue from a Maximo work order.
     * 
     * @param userInfo The user context
     * @param workOrderId The work order ID
     * @param projectId The ACC project ID
     * @return Result containing created issue ID
     * @throws RemoteException If RMI error occurs
     * @throws MXException If Maximo error occurs
     */
    Result createIssueFromWorkOrder(UserInfo userInfo, long workOrderId, String projectId) 
        throws RemoteException, MXException;
    
    /**
     * Sync clash status with linked work order.
     * 
     * @param clashId The ACC clash ID
     * @param workOrderNum The work order number
     * @return Result indicating success/failure
     * @throws RemoteException If RMI error occurs
     * @throws MXException If Maximo error occurs
     */
    Result syncClashToWorkOrder(String clashId, String workOrderNum) 
        throws RemoteException, MXException;
    
    /**
     * Get all clashes linked to a work order.
     * 
     * @param userInfo The user context
     * @param workOrderNum The work order number
     * @return MboSetRemote containing linked clashes
     * @throws RemoteException If RMI error occurs
     * @throws MXException If Maximo error occurs
     */
    MboSetRemote getLinkedClashes(UserInfo userInfo, String workOrderNum) 
        throws RemoteException, MXException;
    
    /**
     * Sync all clashes for a project.
     * 
     * @param userInfo The user context
     * @param projectId The ACC project ID
     * @return Result indicating number of clashes synced
     * @throws RemoteException If RMI error occurs
     * @throws MXException If Maximo error occurs
     */
    Result syncProjectClashes(UserInfo userInfo, String projectId) 
        throws RemoteException, MXException;
}

// Made with Bob
