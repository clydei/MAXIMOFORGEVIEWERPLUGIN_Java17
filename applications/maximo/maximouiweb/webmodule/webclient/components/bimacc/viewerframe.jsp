<%--
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
--%>
<%@page import="psdi.webclient.components.*"%>
<%@page import="psdi.server.MXServer"%>
<%@page import="psdi.webclient.system.*"%>

<%
	String rid = request.getParameter( "rid" );
	String cid = request.getParameter( "id" );
	String uisessionid = request.getParameter( "uisessionid" );
	
	if( rid == null || rid.length() == 0 )
	{
		return;
	}
	
	WebClientSession wcs = WebClientSessionFactory.getWebClientSession( request.getSession(false), uisessionid );
	if( wcs == null )
	{
		return;
	}
	
	BIMViewer bldgMdl = (BIMViewer)wcs.findControl( cid );
	if( bldgMdl == null )
	{
		return;
	}
	
	request.setAttribute( "bldgMdl", bldgMdl );
	request.setAttribute( "id", rid );
	
	String servletBase = request.getContextPath();
	String skin = wcs.getSkin();
	String IMAGE_PATH = servletBase + "/" + skin + "images/" + wcs.getImagePath();
	String CSS_PATH = servletBase + "/" + skin + "css/";
	
	request.setAttribute( "IMAGE_PATH", IMAGE_PATH );
	request.setAttribute( "CSS_PATH", CSS_PATH );
	request.setAttribute( "servletBase", servletBase );
%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>ACC Viewer - Maximo</title>
	
	<%@ include file="header.jsp" %>
</head>
<body>
	<!-- Toolbar -->
	<%@ include file="toolbar.jsp" %>
	
	<!-- Viewer Container -->
	<div id="<%=rid%>_viewer_container" class="acc-viewer-container">
		<div id="<%=rid%>_viewer" class="acc-viewer"></div>
		
		<!-- Clash Panel -->
		<div id="<%=rid%>_clash_panel" class="acc-panel acc-clash-panel" style="display:none;">
			<div class="acc-panel-header">
				<h3>Clashes</h3>
				<button class="acc-panel-close">&times;</button>
			</div>
			<div class="acc-panel-content">
				<div id="<%=rid%>_clash_list" class="acc-clash-list"></div>
			</div>
		</div>
		
		<!-- Issue Panel -->
		<div id="<%=rid%>_issue_panel" class="acc-panel acc-issue-panel" style="display:none;">
			<div class="acc-panel-header">
				<h3>Issues</h3>
				<button class="acc-panel-close">&times;</button>
			</div>
			<div class="acc-panel-content">
				<div id="<%=rid%>_issue_list" class="acc-issue-list"></div>
			</div>
		</div>
		
		<!-- Model Set Panel -->
		<div id="<%=rid%>_modelset_panel" class="acc-panel acc-modelset-panel" style="display:none;">
			<div class="acc-panel-header">
				<h3>Model Sets</h3>
				<button class="acc-panel-close">&times;</button>
			</div>
			<div class="acc-panel-content">
				<div id="<%=rid%>_modelset_list" class="acc-modelset-list"></div>
			</div>
		</div>
		
		<!-- Loading Indicator -->
		<div id="<%=rid%>_loading" class="acc-loading" style="display:none;">
			<div class="acc-loading-spinner"></div>
			<div class="acc-loading-text">Loading model...</div>
		</div>
	</div>
	
	<script type="text/javascript">
		// Initialize ACC Viewer
		(function() {
			var viewerId = '<%=rid%>_viewer';
			var containerId = '<%=rid%>_viewer_container';
			
			// Get viewer configuration from BIMViewer component
			var config = {
				viewerId: viewerId,
				containerId: containerId,
				projectId: '<%=bldgMdl.getProjectId()%>',
				modelSetId: '<%=bldgMdl.getModelSetId()%>',
				urn: '<%=bldgMdl.getModelURN()%>',
				accessToken: null, // Will be fetched via AJAX
				servletBase: '<%=servletBase%>',
				uisessionid: '<%=uisessionid%>',
				componentId: '<%=cid%>',
				enableClashes: ACCConfig.clashDetectionEnabled,
				enableIssues: ACCConfig.issuesEnabled,
				enableModelCoordination: ACCConfig.modelCoordinationEnabled
			};
			
			// Fetch access token and initialize viewer
			function initializeViewer() {
				// Show loading
				document.getElementById('<%=rid%>_loading').style.display = 'flex';
				
				// Get access token from Maximo
				fetch(config.servletBase + '/servlet/BIMServlet', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/x-www-form-urlencoded',
					},
					body: 'action=getACCToken&uisessionid=' + config.uisessionid
				})
				.then(response => response.json())
				.then(data => {
					if (data.success && data.token) {
						config.accessToken = data.token;
						
						// Initialize Autodesk Viewer
						var options = {
							env: 'AutodeskProduction',
							api: 'derivativeV2',
							accessToken: config.accessToken
						};
						
						Autodesk.Viewing.Initializer(options, function() {
							var viewerDiv = document.getElementById(viewerId);
							var viewer = new Autodesk.Viewing.GuiViewer3D(viewerDiv);
							
							viewer.start();
							
							// Load model
							if (config.urn) {
								var documentId = 'urn:' + config.urn;
								Autodesk.Viewing.Document.load(documentId, onDocumentLoadSuccess, onDocumentLoadFailure);
							}
							
							// Store viewer instance globally
							window.accViewer = viewer;
							window.accViewerConfig = config;
							
							// Hide loading
							document.getElementById('<%=rid%>_loading').style.display = 'none';
							
							// Initialize ACC extensions
							initializeACCExtensions(viewer, config);
						});
					} else {
						console.error('Failed to get ACC token:', data.error);
						document.getElementById('<%=rid%>_loading').innerHTML = 
							'<div class="acc-error">Failed to authenticate with ACC</div>';
					}
				})
				.catch(error => {
					console.error('Error fetching ACC token:', error);
					document.getElementById('<%=rid%>_loading').innerHTML = 
						'<div class="acc-error">Error: ' + error.message + '</div>';
				});
			}
			
			function onDocumentLoadSuccess(doc) {
				var viewables = doc.getRoot().getDefaultGeometry();
				window.accViewer.loadDocumentNode(doc, viewables);
			}
			
			function onDocumentLoadFailure(errorCode) {
				console.error('Failed to load document:', errorCode);
				document.getElementById('<%=rid%>_loading').innerHTML = 
					'<div class="acc-error">Failed to load model: ' + errorCode + '</div>';
			}
			
			function initializeACCExtensions(viewer, config) {
				// Load clash detection extension
				if (config.enableClashes) {
					viewer.loadExtension('Autodesk.BIM360.Extension.Clash');
				}
				
				// Load issue extension
				if (config.enableIssues) {
					viewer.loadExtension('Autodesk.BIM360.Extension.Issue');
				}
				
				// Load model coordination extension
				if (config.enableModelCoordination) {
					viewer.loadExtension('Autodesk.BIM360.Extension.ModelCoordination');
				}
				
				// Initialize custom Maximo integration
				if (window.MaximoACCIntegration) {
					window.MaximoACCIntegration.initialize(viewer, config);
				}
			}
			
			// Initialize when DOM is ready
			if (document.readyState === 'loading') {
				document.addEventListener('DOMContentLoaded', initializeViewer);
			} else {
				initializeViewer();
			}
		})();
	</script>
	
	<%@ include file="footer.jsp" %>
</body>
</html>