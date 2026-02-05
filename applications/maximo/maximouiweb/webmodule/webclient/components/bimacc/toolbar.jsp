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
<%
	BIMViewer bldgMdl = (BIMViewer)request.getAttribute( "bldgMdl" );
	if( bldgMdl == null )
	{
		return;
	}
	
	String id = (String)request.getAttribute( "id" );
	if( id == null || id.length() == 0 )
	{
		return;
	}
	
	String IMAGE_PATH = (String)request.getAttribute( "IMAGE_PATH" );
%>

<div id="<%=id%>_toolbar" class="acc-toolbar">
	<!-- Model Coordination Tools -->
	<div class="acc-toolbar-section">
		<button id="<%=id%>_btn_clashes" class="acc-toolbar-btn" title="View Clashes">
			<img src="<%=IMAGE_PATH%>bim/clash.png" alt="Clashes"/>
			<span>Clashes</span>
		</button>
		<button id="<%=id%>_btn_issues" class="acc-toolbar-btn" title="View Issues">
			<img src="<%=IMAGE_PATH%>bim/issue.png" alt="Issues"/>
			<span>Issues</span>
		</button>
		<button id="<%=id%>_btn_modelsets" class="acc-toolbar-btn" title="Model Sets">
			<img src="<%=IMAGE_PATH%>bim/modelset.png" alt="Model Sets"/>
			<span>Model Sets</span>
		</button>
	</div>
	
	<!-- View Tools -->
	<div class="acc-toolbar-section">
		<button id="<%=id%>_btn_home" class="acc-toolbar-btn" title="Home View">
			<img src="<%=IMAGE_PATH%>bim/home.png" alt="Home"/>
		</button>
		<button id="<%=id%>_btn_fit" class="acc-toolbar-btn" title="Fit to View">
			<img src="<%=IMAGE_PATH%>bim/fit.png" alt="Fit"/>
		</button>
		<button id="<%=id%>_btn_section" class="acc-toolbar-btn" title="Section Tool">
			<img src="<%=IMAGE_PATH%>bim/section.png" alt="Section"/>
		</button>
	</div>
	
	<!-- Markup Tools -->
	<div class="acc-toolbar-section">
		<button id="<%=id%>_btn_markup" class="acc-toolbar-btn" title="Markup">
			<img src="<%=IMAGE_PATH%>bim/markup.png" alt="Markup"/>
		</button>
		<button id="<%=id%>_btn_measure" class="acc-toolbar-btn" title="Measure">
			<img src="<%=IMAGE_PATH%>bim/measure.png" alt="Measure"/>
		</button>
	</div>
	
	<!-- Settings -->
	<div class="acc-toolbar-section">
		<button id="<%=id%>_btn_settings" class="acc-toolbar-btn" title="Settings">
			<img src="<%=IMAGE_PATH%>bim/settings.png" alt="Settings"/>
		</button>
		<button id="<%=id%>_btn_fullscreen" class="acc-toolbar-btn" title="Full Screen">
			<img src="<%=IMAGE_PATH%>bim/fullscreen.png" alt="Full Screen"/>
		</button>
	</div>
</div>

<script type="text/javascript">
	// Initialize toolbar event handlers
	(function() {
		var toolbar = document.getElementById('<%=id%>_toolbar');
		if (!toolbar) return;
		
		// Clash viewer
		document.getElementById('<%=id%>_btn_clashes').addEventListener('click', function() {
			if (window.accViewer) {
				window.accViewer.showClashes();
			}
		});
		
		// Issue viewer
		document.getElementById('<%=id%>_btn_issues').addEventListener('click', function() {
			if (window.accViewer) {
				window.accViewer.showIssues();
			}
		});
		
		// Model sets
		document.getElementById('<%=id%>_btn_modelsets').addEventListener('click', function() {
			if (window.accViewer) {
				window.accViewer.showModelSets();
			}
		});
		
		// Home view
		document.getElementById('<%=id%>_btn_home').addEventListener('click', function() {
			if (window.accViewer) {
				window.accViewer.navigation.setRequestHomeView(true);
			}
		});
		
		// Fit to view
		document.getElementById('<%=id%>_btn_fit').addEventListener('click', function() {
			if (window.accViewer) {
				window.accViewer.navigation.fitToView();
			}
		});
		
		// Section tool
		document.getElementById('<%=id%>_btn_section').addEventListener('click', function() {
			if (window.accViewer) {
				window.accViewer.toggleSectionTool();
			}
		});
		
		// Markup
		document.getElementById('<%=id%>_btn_markup').addEventListener('click', function() {
			if (window.accViewer) {
				window.accViewer.toggleMarkup();
			}
		});
		
		// Measure
		document.getElementById('<%=id%>_btn_measure').addEventListener('click', function() {
			if (window.accViewer) {
				window.accViewer.toggleMeasure();
			}
		});
		
		// Settings
		document.getElementById('<%=id%>_btn_settings').addEventListener('click', function() {
			if (window.accViewer) {
				window.accViewer.showSettings();
			}
		});
		
		// Full screen
		document.getElementById('<%=id%>_btn_fullscreen').addEventListener('click', function() {
			if (window.accViewer) {
				window.accViewer.toggleFullscreen();
			}
		});
	})();
</script>