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
 * Maximo-specific ACC Markup Integration
 */

(function(window) {
    'use strict';
    
    /**
     * Maximo ACC Markup Manager
     * Handles markup integration between ACC Viewer and Maximo
     */
    class MaximoACCMarkupManager {
        constructor() {
            this.viewer = null;
            this.markupExtension = null;
            this.workOrderMarkups = new Map();
        }
        
        /**
         * Initialize markup manager
         */
        initialize(viewer) {
            this.viewer = viewer;
            
            // Load markup extension
            viewer.loadExtension('Maximo.ACC.MarkupExtension')
                .then(ext => {
                    this.markupExtension = ext;
                    console.log('Maximo ACC Markup Manager initialized');
                    
                    // Load existing markups
                    this.loadWorkOrderMarkups();
                })
                .catch(err => {
                    console.error('Failed to load markup extension:', err);
                });
        }
        
        /**
         * Load markups associated with work orders
         */
        loadWorkOrderMarkups() {
            if (!this.markupExtension) return;
            
            this.markupExtension.loadMarkups()
                .then(markups => {
                    markups.forEach(markup => {
                        if (markup.workOrderId) {
                            this.workOrderMarkups.set(markup.workOrderId, markup);
                        }
                    });
                    console.log('Loaded work order markups:', this.workOrderMarkups.size);
                });
        }
        
        /**
         * Create markup for clash
         */
        createClashMarkup(clashId, clashData) {
            if (!this.markupExtension) return Promise.reject('Markup extension not loaded');
            
            const markup = this.markupExtension.createMarkup('clash', {
                clashId: clashId,
                ...clashData
            });
            
            return this.markupExtension.saveMarkup(markup);
        }
        
        /**
         * Create markup for issue
         */
        createIssueMarkup(issueId, issueData) {
            if (!this.markupExtension) return Promise.reject('Markup extension not loaded');
            
            const markup = this.markupExtension.createMarkup('issue', {
                issueId: issueId,
                ...issueData
            });
            
            return this.markupExtension.saveMarkup(markup);
        }
        
        /**
         * Create markup for work order
         */
        createWorkOrderMarkup(workOrderId, workOrderData) {
            if (!this.markupExtension) return Promise.reject('Markup extension not loaded');
            
            const markup = this.markupExtension.createMarkup('workorder', {
                workOrderId: workOrderId,
                ...workOrderData
            });
            
            return this.markupExtension.saveMarkup(markup)
                .then(markupId => {
                    this.workOrderMarkups.set(workOrderId, markup);
                    return markupId;
                });
        }
        
        /**
         * Get markup for work order
         */
        getWorkOrderMarkup(workOrderId) {
            return this.workOrderMarkups.get(workOrderId);
        }
        
        /**
         * Show markup for work order
         */
        showWorkOrderMarkup(workOrderId) {
            const markup = this.workOrderMarkups.get(workOrderId);
            if (!markup) {
                console.warn('No markup found for work order:', workOrderId);
                return;
            }
            
            // Display markup in viewer
            if (this.viewer && markup.data.dbIds) {
                this.viewer.isolate(markup.data.dbIds);
                this.viewer.fitToView(markup.data.dbIds);
            }
        }
        
        /**
         * Delete markup
         */
        deleteMarkup(markupId) {
            if (!this.markupExtension) return Promise.reject('Markup extension not loaded');
            
            return this.markupExtension.deleteMarkup(markupId)
                .then(() => {
                    // Remove from work order map
                    for (let [woId, markup] of this.workOrderMarkups.entries()) {
                        if (markup.id === markupId) {
                            this.workOrderMarkups.delete(woId);
                            break;
                        }
                    }
                });
        }
        
        /**
         * Export markup as image
         */
        exportMarkupAsImage(markupId) {
            if (!this.viewer) return Promise.reject('Viewer not initialized');
            
            return new Promise((resolve, reject) => {
                try {
                    const screenshot = this.viewer.getScreenShot(
                        this.viewer.container.clientWidth,
                        this.viewer.container.clientHeight,
                        (blob) => {
                            resolve(blob);
                        }
                    );
                } catch (err) {
                    reject(err);
                }
            });
        }
        
        /**
         * Attach markup to work order
         */
        attachMarkupToWorkOrder(markupId, workOrderId) {
            if (!window.accViewerConfig) return Promise.reject('Config not available');
            
            const servletUrl = window.accViewerConfig.servletBase + '/servlet/BIMServlet';
            const formData = new FormData();
            formData.append('action', 'attachMarkupToWorkOrder');
            formData.append('uisessionid', window.accViewerConfig.uisessionid);
            formData.append('markupId', markupId);
            formData.append('workOrderId', workOrderId);
            
            return fetch(servletUrl, {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    console.log('Markup attached to work order');
                    return true;
                } else {
                    throw new Error(data.error);
                }
            });
        }
    }
    
    // Export to global scope
    window.MaximoACCMarkupManager = new MaximoACCMarkupManager();
    
})(window);

// Made with Bob
