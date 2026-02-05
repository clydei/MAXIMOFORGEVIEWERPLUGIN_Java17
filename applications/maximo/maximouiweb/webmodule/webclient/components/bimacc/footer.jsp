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
%>

<script type="text/javascript">
	// ACC Viewer cleanup and event handlers
	window.addEventListener('beforeunload', function() {
		if (window.accViewer && window.accViewer.finish) {
			window.accViewer.finish();
		}
	});
	
	// Handle viewer resize
	window.addEventListener('resize', function() {
		if (window.accViewer && window.accViewer.resize) {
			window.accViewer.resize();
		}
	});
</script>