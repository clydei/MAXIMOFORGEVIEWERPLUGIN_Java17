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
 * ACC Markup Extension for Autodesk Viewer
 */

(function() {
    'use strict';
    
    /**
     * ACC Markup Extension
     * Extends Autodesk Viewer markup capabilities for Maximo integration
     */
    class ACCMarkupExtension extends Autodesk.Viewing.Extension {
        constructor(viewer, options) {
            super(viewer, options);
            this.viewer = viewer;
            this.markups = [];
            this.activeMarkup = null;
        }
        
        load() {
            console.log('ACC Markup Extension loaded');
            return true;
        }
        
        unload() {
            console.log('ACC Markup Extension unloaded');
            return true;
        }
        
        /**
         * Create a new markup
         */
        createMarkup(type, data) {
            const markup = {
                id: this.generateMarkupId(),
                type: type,
                data: data,
                timestamp: new Date().toISOString(),
                author: window.accViewerConfig?.username || 'Unknown'
            };
            
            this.markups.push(markup);
            this.activeMarkup = markup;
            
            return markup;
        }
        
        /**
         * Save markup to Maximo
         */
        saveMarkup(markup) {
            if (!markup || !window.accViewerConfig) return;
            
            const servletUrl = window.accViewerConfig.servletBase + '/servlet/BIMServlet';
            const formData = new FormData();
            formData.append('action', 'saveACCMarkup');
            formData.append('uisessionid', window.accViewerConfig.uisessionid);
            formData.append('markup', JSON.stringify(markup));
            
            return fetch(servletUrl, {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    console.log('Markup saved:', data.markupId);
                    return data.markupId;
                } else {
                    console.error('Failed to save markup:', data.error);
                    throw new Error(data.error);
                }
            });
        }
        
        /**
         * Load markups from Maximo
         */
        loadMarkups() {
            if (!window.accViewerConfig) return Promise.resolve([]);
            
            const servletUrl = window.accViewerConfig.servletBase + '/servlet/BIMServlet';
            const params = new URLSearchParams({
                action: 'getACCMarkups',
                uisessionid: window.accViewerConfig.uisessionid,
                projectId: window.accViewerConfig.projectId,
                modelSetId: window.accViewerConfig.modelSetId
            });
            
            return fetch(servletUrl + '?' + params.toString())
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        this.markups = data.markups || [];
                        return this.markups;
                    } else {
                        console.error('Failed to load markups:', data.error);
                        return [];
                    }
                });
        }
        
        /**
         * Delete a markup
         */
        deleteMarkup(markupId) {
            const index = this.markups.findIndex(m => m.id === markupId);
            if (index !== -1) {
                this.markups.splice(index, 1);
            }
            
            if (!window.accViewerConfig) return Promise.resolve();
            
            const servletUrl = window.accViewerConfig.servletBase + '/servlet/BIMServlet';
            const formData = new FormData();
            formData.append('action', 'deleteACCMarkup');
            formData.append('uisessionid', window.accViewerConfig.uisessionid);
            formData.append('markupId', markupId);
            
            return fetch(servletUrl, {
                method: 'POST',
                body: formData
            })
            .then(response => response.json());
        }
        
        /**
         * Generate unique markup ID
         */
        generateMarkupId() {
            return 'markup_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
        }
    }
    
    // Register extension
    Autodesk.Viewing.theExtensionManager.registerExtension(
        'Maximo.ACC.MarkupExtension',
        ACCMarkupExtension
    );
    
})();

// Made with Bob
