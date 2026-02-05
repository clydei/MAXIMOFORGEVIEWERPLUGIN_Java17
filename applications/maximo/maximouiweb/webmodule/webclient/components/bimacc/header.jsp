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
<%@page import="psdi.server.MXServer"%>
<%
	MXServer server    = MXServer.getMXServer();
	String accHost     = server.getProperty( "bim.viewer.ACC.host" );
	if( accHost == null ) accHost = "developer.api.autodesk.com";
	
	String accVersion  = server.getProperty( "bim.viewer.ACC.viewer.version" );
	if( accVersion == null ) accVersion = "";
	
	String accTheme    = server.getProperty( "bim.viewer.ACC.theme" );
	if( accTheme == null ) accTheme = "light-theme"; 
	
	// Autodesk Platform Services (APS) Viewer URLs
	String three     = "https://" + accHost + "/modelderivative/v2/viewers/three.min.js" + accVersion;
	String style     = "https://" + accHost + "/modelderivative/v2/viewers/style.css" + accVersion;
	String viewer3D  = "https://" + accHost + "/modelderivative/v2/viewers/viewer3D.min.js" + accVersion;
%>

<!-- Autodesk Viewer Styles -->
<link rel="stylesheet" type="text/css" href="<%=style%>" />
<link rel="stylesheet" type="text/css" href="<%=CSS_PATH%>ACC.css" />

<!-- Autodesk Viewer Scripts -->
<script type="text/javascript" src="<%=three%>"></script>
<script type="text/javascript" src="<%=viewer3D%>"></script>

<!-- Maximo ACC Integration Scripts -->
<script type="text/javascript" src="<%=servletBase%>/javascript/gunzip.min.js"></script>
<script type="text/javascript" src="<%=servletBase%>/javascript/Forge.js"></script>
<script type="text/javascript" src="<%=servletBase%>/javascript/ACC.js"></script>
<script type="text/javascript" src="<%=servletBase%>/javascript/ACC_Markup.js"></script>
<script type="text/javascript" src="<%=servletBase%>/javascript/MaximoACCMarkup.js"></script>

<script type="text/javascript">
	// Initialize ACC viewer configuration
	var ACCConfig = {
		host: "<%=accHost%>",
		theme: "<%=accTheme%>",
		version: "<%=accVersion%>",
		apiVersion: "<%=server.getProperty("bim.viewer.ACC.api.version")%>",
		modelCoordinationEnabled: <%=server.getProperty("bim.viewer.ACC.modelcoordination.enabled", "true")%>,
		clashDetectionEnabled: <%=server.getProperty("bim.viewer.ACC.clashdetection.enabled", "true")%>,
		issuesEnabled: <%=server.getProperty("bim.viewer.ACC.issues.enabled", "true")%>
	};
</script>