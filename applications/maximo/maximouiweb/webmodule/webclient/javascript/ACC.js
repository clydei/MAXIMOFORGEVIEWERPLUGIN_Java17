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
 * 
 * ACC Viewer Integration for Maximo
 * Provides integration between Autodesk Construction Cloud and IBM Maximo
 */

(function(window) {
    'use strict';
    
    /**
     * ACC Viewer Integration Class
     */
    class ACCViewerIntegration {
        constructor() {
            this.viewer = null;
            this.config = null;
            this.clashes = [];
            this.issues = [];
            this.modelSets = [];
            this.currentSelection = null;
        }
        
        /**
         * Initialize the ACC viewer integration
         * @param {Autodesk.Viewing.GuiViewer3D} viewer - The Autodesk viewer instance
         * @param {Object} config - Configuration object
         */
        initialize(viewer, config) {
            this.viewer = viewer;
            this.config = config;
            
            console.log('Initializing ACC Viewer Integration', config);
            
            // Set up event listeners
            this.setupEventListeners();
            
            // Load ACC data
            this.loadACCData();
            
            // Initialize panels
            this.initializePanels();
        }
        
        /**
         * Set up viewer event listeners
         */
        setupEventListeners() {
            if (!this.viewer) return;
            
            // Selection changed
            this.viewer.addEventListener(
                Autodesk.Viewing.SELECTION_CHANGED_EVENT,
                this.onSelectionChanged.bind(this)
            );
            
            // Model loaded
            this.viewer.addEventListener(
                Autodesk.Viewing.GEOMETRY_LOADED_EVENT,
                this.onGeometryLoaded.bind(this)
            );
            
            // Object tree created
            this.viewer.addEventListener(
                Autodesk.Viewing.OBJECT_TREE_CREATED_EVENT,
                this.onObjectTreeCreated.bind(this)
            );
        }
        
        /**
         * Load ACC data (clashes, issues, model sets)
         */
        loadACCData() {
            if (!this.config) return;
            
            const servletUrl = this.config.servletBase + '/servlet/BIMServlet';
            const params = new URLSearchParams({
                action: 'getACCData',
                uisessionid: this.config.uisessionid,
                projectId: this.config.projectId,
                modelSetId: this.config.modelSetId
            });
            
            fetch(servletUrl + '?' + params.toString())
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        this.clashes = data.clashes || [];
                        this.issues = data.issues || [];
                        this.modelSets = data.modelSets || [];
                        
                        console.log('ACC Data loaded:', {
                            clashes: this.clashes.length,
                            issues: this.issues.length,
                            modelSets: this.modelSets.length
                        });
                        
                        // Update UI
                        this.updateClashPanel();
                        this.updateIssuePanel();
                        this.updateModelSetPanel();
                    }
                })
                .catch(error => {
                    console.error('Error loading ACC data:', error);
                });
        }
        
        /**
         * Initialize side panels
         */
        initializePanels() {
            // Close button handlers
            const closeBtns = document.querySelectorAll('.acc-panel-close');
            closeBtns.forEach(btn => {
                btn.addEventListener('click', (e) => {
                    e.target.closest('.acc-panel').style.display = 'none';
                });
            });
        }
        
        /**
         * Show clashes panel
         */
        showClashes() {
            const panel = document.querySelector('.acc-clash-panel');
            if (panel) {
                panel.style.display = 'block';
                this.updateClashPanel();
            }
        }
        
        /**
         * Show issues panel
         */
        showIssues() {
            const panel = document.querySelector('.acc-issue-panel');
            if (panel) {
                panel.style.display = 'block';
                this.updateIssuePanel();
            }
        }
        
        /**
         * Show model sets panel
         */
        showModelSets() {
            const panel = document.querySelector('.acc-modelset-panel');
            if (panel) {
                panel.style.display = 'block';
                this.updateModelSetPanel();
            }
        }
        
        /**
         * Update clash panel content
         */
        updateClashPanel() {
            const listDiv = document.querySelector('.acc-clash-list');
            if (!listDiv) return;
            
            if (this.clashes.length === 0) {
                listDiv.innerHTML = '<div class="acc-empty">No clashes found</div>';
                return;
            }
            
            let html = '<ul class="acc-list">';
            this.clashes.forEach((clash, index) => {
                const severity = clash.severity || 'medium';
                const status = clash.status || 'active';
                html += `
                    <li class="acc-list-item clash-${severity}" data-clash-id="${clash.id}">
                        <div class="acc-list-item-header">
                            <span class="acc-clash-id">#${clash.id}</span>
                            <span class="acc-clash-severity acc-badge-${severity}">${severity}</span>
                            <span class="acc-clash-status">${status}</span>
                        </div>
                        <div class="acc-list-item-body">
                            <div class="acc-clash-title">${clash.title || 'Clash ' + (index + 1)}</div>
                            <div class="acc-clash-objects">
                                ${clash.objectA || 'Object A'} ↔ ${clash.objectB || 'Object B'}
                            </div>
                        </div>
                        <div class="acc-list-item-footer">
                            <button class="acc-btn-small" onclick="window.accViewer.zoomToClash('${clash.id}')">
                                View
                            </button>
                            <button class="acc-btn-small" onclick="window.accViewer.createWorkOrderFromClash('${clash.id}')">
                                Create WO
                            </button>
                        </div>
                    </li>
                `;
            });
            html += '</ul>';
            listDiv.innerHTML = html;
        }
        
        /**
         * Update issue panel content
         */
        updateIssuePanel() {
            const listDiv = document.querySelector('.acc-issue-list');
            if (!listDiv) return;
            
            if (this.issues.length === 0) {
                listDiv.innerHTML = '<div class="acc-empty">No issues found</div>';
                return;
            }
            
            let html = '<ul class="acc-list">';
            this.issues.forEach((issue, index) => {
                const priority = issue.priority || 'normal';
                const status = issue.status || 'open';
                html += `
                    <li class="acc-list-item issue-${priority}" data-issue-id="${issue.id}">
                        <div class="acc-list-item-header">
                            <span class="acc-issue-id">#${issue.id}</span>
                            <span class="acc-issue-priority acc-badge-${priority}">${priority}</span>
                            <span class="acc-issue-status">${status}</span>
                        </div>
                        <div class="acc-list-item-body">
                            <div class="acc-issue-title">${issue.title || 'Issue ' + (index + 1)}</div>
                            <div class="acc-issue-description">${issue.description || ''}</div>
                            ${issue.dueDate ? `<div class="acc-issue-due">Due: ${issue.dueDate}</div>` : ''}
                        </div>
                        <div class="acc-list-item-footer">
                            <button class="acc-btn-small" onclick="window.accViewer.zoomToIssue('${issue.id}')">
                                View
                            </button>
                            <button class="acc-btn-small" onclick="window.accViewer.createWorkOrderFromIssue('${issue.id}')">
                                Create WO
                            </button>
                        </div>
                    </li>
                `;
            });
            html += '</ul>';
            listDiv.innerHTML = html;
        }
        
        /**
         * Update model set panel content
         */
        updateModelSetPanel() {
            const listDiv = document.querySelector('.acc-modelset-list');
            if (!listDiv) return;
            
            if (this.modelSets.length === 0) {
                listDiv.innerHTML = '<div class="acc-empty">No model sets found</div>';
                return;
            }
            
            let html = '<ul class="acc-list">';
            this.modelSets.forEach((modelSet, index) => {
                html += `
                    <li class="acc-list-item" data-modelset-id="${modelSet.id}">
                        <div class="acc-list-item-header">
                            <span class="acc-modelset-name">${modelSet.name || 'Model Set ' + (index + 1)}</span>
                        </div>
                        <div class="acc-list-item-body">
                            <div class="acc-modelset-models">Models: ${modelSet.modelCount || 0}</div>
                            <div class="acc-modelset-date">Updated: ${modelSet.updatedAt || 'N/A'}</div>
                        </div>
                        <div class="acc-list-item-footer">
                            <button class="acc-btn-small" onclick="window.accViewer.loadModelSet('${modelSet.id}')">
                                Load
                            </button>
                        </div>
                    </li>
                `;
            });
            html += '</ul>';
            listDiv.innerHTML = html;
        }
        
        /**
         * Zoom to clash in viewer
         */
        zoomToClash(clashId) {
            const clash = this.clashes.find(c => c.id === clashId);
            if (!clash || !this.viewer) return;
            
            // Isolate and zoom to clash objects
            const dbIds = [clash.dbIdA, clash.dbIdB].filter(id => id);
            if (dbIds.length > 0) {
                this.viewer.isolate(dbIds);
                this.viewer.fitToView(dbIds);
            }
        }
        
        /**
         * Zoom to issue in viewer
         */
        zoomToIssue(issueId) {
            const issue = this.issues.find(i => i.id === issueId);
            if (!issue || !this.viewer) return;
            
            // Zoom to issue location
            if (issue.dbIds && issue.dbIds.length > 0) {
                this.viewer.isolate(issue.dbIds);
                this.viewer.fitToView(issue.dbIds);
            }
        }
        
        /**
         * Create work order from clash
         */
        createWorkOrderFromClash(clashId) {
            const clash = this.clashes.find(c => c.id === clashId);
            if (!clash) return;
            
            // Call Maximo servlet to create work order
            const servletUrl = this.config.servletBase + '/servlet/BIMServlet';
            const formData = new FormData();
            formData.append('action', 'createWorkOrderFromClash');
            formData.append('uisessionid', this.config.uisessionid);
            formData.append('clashId', clashId);
            formData.append('projectId', this.config.projectId);
            
            fetch(servletUrl, {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Work Order created: ' + data.wonum);
                } else {
                    alert('Error creating work order: ' + data.error);
                }
            })
            .catch(error => {
                console.error('Error creating work order:', error);
                alert('Error creating work order');
            });
        }
        
        /**
         * Create work order from issue
         */
        createWorkOrderFromIssue(issueId) {
            const issue = this.issues.find(i => i.id === issueId);
            if (!issue) return;
            
            // Call Maximo servlet to create work order
            const servletUrl = this.config.servletBase + '/servlet/BIMServlet';
            const formData = new FormData();
            formData.append('action', 'createWorkOrderFromIssue');
            formData.append('uisessionid', this.config.uisessionid);
            formData.append('issueId', issueId);
            formData.append('projectId', this.config.projectId);
            
            fetch(servletUrl, {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Work Order created: ' + data.wonum);
                } else {
                    alert('Error creating work order: ' + data.error);
                }
            })
            .catch(error => {
                console.error('Error creating work order:', error);
                alert('Error creating work order');
            });
        }
        
        /**
         * Load a different model set
         */
        loadModelSet(modelSetId) {
            console.log('Loading model set:', modelSetId);
            // Implementation depends on ACC API
            alert('Loading model set: ' + modelSetId);
        }
        
        /**
         * Toggle section tool
         */
        toggleSectionTool() {
            if (!this.viewer) return;
            
            const ext = this.viewer.getExtension('Autodesk.Section');
            if (ext) {
                ext.activate();
            }
        }
        
        /**
         * Toggle markup mode
         */
        toggleMarkup() {
            if (!this.viewer) return;
            
            const ext = this.viewer.getExtension('Autodesk.Viewing.MarkupsCore');
            if (ext) {
                ext.enterEditMode();
            }
        }
        
        /**
         * Toggle measure tool
         */
        toggleMeasure() {
            if (!this.viewer) return;
            
            const ext = this.viewer.getExtension('Autodesk.Measure');
            if (ext) {
                ext.activate();
            }
        }
        
        /**
         * Show settings dialog
         */
        showSettings() {
            alert('Settings dialog - To be implemented');
        }
        
        /**
         * Toggle fullscreen mode
         */
        toggleFullscreen() {
            const container = document.querySelector('.acc-viewer-container');
            if (!container) return;
            
            if (!document.fullscreenElement) {
                container.requestFullscreen();
            } else {
                document.exitFullscreen();
            }
        }
        
        /**
         * Event handler: Selection changed
         */
        onSelectionChanged(event) {
            this.currentSelection = event.dbIdArray;
            console.log('Selection changed:', this.currentSelection);
        }
        
        /**
         * Event handler: Geometry loaded
         */
        onGeometryLoaded(event) {
            console.log('Geometry loaded');
        }
        
        /**
         * Event handler: Object tree created
         */
        onObjectTreeCreated(event) {
            console.log('Object tree created');
        }
    }
    
    // Export to global scope
    window.MaximoACCIntegration = new ACCViewerIntegration();
    
    // Add helper methods to viewer instance
    if (window.accViewer) {
        window.accViewer.showClashes = () => window.MaximoACCIntegration.showClashes();
        window.accViewer.showIssues = () => window.MaximoACCIntegration.showIssues();
        window.accViewer.showModelSets = () => window.MaximoACCIntegration.showModelSets();
        window.accViewer.zoomToClash = (id) => window.MaximoACCIntegration.zoomToClash(id);
        window.accViewer.zoomToIssue = (id) => window.MaximoACCIntegration.zoomToIssue(id);
        window.accViewer.createWorkOrderFromClash = (id) => window.MaximoACCIntegration.createWorkOrderFromClash(id);
        window.accViewer.createWorkOrderFromIssue = (id) => window.MaximoACCIntegration.createWorkOrderFromIssue(id);
        window.accViewer.loadModelSet = (id) => window.MaximoACCIntegration.loadModelSet(id);
        window.accViewer.toggleSectionTool = () => window.MaximoACCIntegration.toggleSectionTool();
        window.accViewer.toggleMarkup = () => window.MaximoACCIntegration.toggleMarkup();
        window.accViewer.toggleMeasure = () => window.MaximoACCIntegration.toggleMeasure();
        window.accViewer.showSettings = () => window.MaximoACCIntegration.showSettings();
        window.accViewer.toggleFullscreen = () => window.MaximoACCIntegration.toggleFullscreen();
    }
    
})(window);

// Made with Bob
