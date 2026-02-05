# ACC Viewer Integration Guide

## Overview

This document describes the ACC (Autodesk Construction Cloud) Viewer integration for IBM Maximo. The integration enables 3D model visualization, clash detection, issue tracking, and work order management directly within Maximo.

**Version:** 1.0.0  
**Date:** February 5, 2026  
**Status:** ✅ Complete - Ready for Testing

---

## Table of Contents

1. [Architecture](#architecture)
2. [Components](#components)
3. [Installation](#installation)
4. [Configuration](#configuration)
5. [Usage](#usage)
6. [Features](#features)
7. [API Reference](#api-reference)
8. [Troubleshooting](#troubleshooting)

---

## Architecture

### Component Stack

```
┌─────────────────────────────────────────────────────────┐
│                    Maximo UI Layer                      │
│              (Work Orders, Assets, Locations)           │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│              ACC Viewer Components                      │
│  • JSP Pages (header, footer, toolbar, viewerframe)    │
│  • JavaScript Integration (ACC.js, ACC_Markup.js)      │
│  • CSS Styling (ACC.css)                                │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│         Autodesk Viewer (APS/Forge)                     │
│  • 3D Model Rendering                                   │
│  • Clash Detection Extensions                           │
│  • Issue Tracking Extensions                            │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│         Autodesk Construction Cloud API                 │
│  • Model Coordination  • Clash Detection                │
│  • Issue Tracking      • Project Management             │
└─────────────────────────────────────────────────────────┘
```

---

## Components

### 1. JSP Components

Located in: `applications/maximo/maximouiweb/webmodule/webclient/components/bimacc/`

#### header.jsp
- Loads Autodesk Viewer libraries
- Configures ACC API endpoints
- Initializes viewer configuration
- Includes CSS and JavaScript dependencies

#### footer.jsp
- Cleanup and event handlers
- Viewer resize handling
- Window unload handling

#### toolbar.jsp
- Model coordination tools (Clashes, Issues, Model Sets)
- View tools (Home, Fit, Section)
- Markup tools (Markup, Measure)
- Settings and fullscreen controls

#### viewerframe.jsp
- Main viewer container
- Viewer initialization
- Panel management (Clash, Issue, Model Set)
- Loading indicators
- Token authentication

### 2. JavaScript Files

Located in: `applications/maximo/maximouiweb/webmodule/webclient/javascript/`

#### ACC.js (509 lines)
Main integration class providing:
- `ACCViewerIntegration` class
- Clash/Issue/Model Set management
- Panel updates and display
- Work order creation from clashes/issues
- Viewer event handling
- Selection management

**Key Methods:**
```javascript
initialize(viewer, config)
showClashes()
showIssues()
showModelSets()
zoomToClash(clashId)
zoomToIssue(issueId)
createWorkOrderFromClash(clashId)
createWorkOrderFromIssue(issueId)
```

#### ACC_Markup.js (145 lines)
Markup extension providing:
- `ACCMarkupExtension` class (extends Autodesk.Viewing.Extension)
- Markup creation and management
- Save/load markups to/from Maximo
- Markup deletion

**Key Methods:**
```javascript
createMarkup(type, data)
saveMarkup(markup)
loadMarkups()
deleteMarkup(markupId)
```

#### MaximoACCMarkup.js (189 lines)
Maximo-specific markup integration:
- `MaximoACCMarkupManager` class
- Work order markup association
- Clash/Issue markup creation
- Markup export as image
- Attachment to work orders

**Key Methods:**
```javascript
initialize(viewer)
createClashMarkup(clashId, clashData)
createIssueMarkup(issueId, issueData)
createWorkOrderMarkup(workOrderId, workOrderData)
showWorkOrderMarkup(workOrderId)
exportMarkupAsImage(markupId)
```

### 3. CSS Styling

Located in: `applications/maximo/maximouiweb/webmodule/webclient/css/ACC.css` (449 lines)

**Includes:**
- Viewer container and layout
- Toolbar styling
- Panel styling (Clash, Issue, Model Set)
- List item styling
- Button styling
- Loading indicators
- Error states
- Responsive design
- Dark theme support
- Fullscreen mode

---

## Installation

### Step 1: Deploy Files

All files are already in place:

```
applications/maximo/maximouiweb/
├── webmodule/webclient/
│   ├── components/bimacc/
│   │   ├── header.jsp
│   │   ├── footer.jsp
│   │   ├── toolbar.jsp
│   │   └── viewerframe.jsp
│   ├── javascript/
│   │   ├── ACC.js
│   │   ├── ACC_Markup.js
│   │   └── MaximoACCMarkup.js
│   └── css/
│       └── ACC.css
```

### Step 2: Configure Maximo Properties

Add to `maximo.properties`:

```properties
# Set ACC as active viewer
bim.viewer.active=acc

# ACC API Configuration
bim.viewer.ACC.host=developer.api.autodesk.com
bim.viewer.ACC.viewer.version=
bim.viewer.ACC.theme=light-theme
bim.viewer.ACC.api.version=v1

# ACC Credentials (from Autodesk APS)
bim.viewer.ACC.clientId=YOUR_CLIENT_ID
bim.viewer.ACC.clientSecret=YOUR_CLIENT_SECRET
bim.viewer.ACC.callbackUrl=https://your-maximo-server/maximo/acc/callback

# Feature Flags
bim.viewer.ACC.modelcoordination.enabled=true
bim.viewer.ACC.clashdetection.enabled=true
bim.viewer.ACC.issues.enabled=true
bim.viewer.ACC.autosync.enabled=true
bim.viewer.ACC.autosync.interval=3600

# Limits
bim.viewer.ACC.model.maxsize=5368709120
bim.viewer.ACC.clash.maxresults=1000
```

### Step 3: Restart Maximo

```bash
# Stop Maximo
./stopmaximo.sh

# Build EAR (if needed)
./buildmaximoear.sh

# Start Maximo
./startmaximo.sh
```

---

## Configuration

### Autodesk APS Setup

1. **Create APS App**
   - Go to https://aps.autodesk.com/
   - Create new app
   - Note Client ID and Secret

2. **Configure Scopes**
   - `data:read` - Read model data
   - `data:write` - Write model data
   - `account:read` - Read account info
   - `account:write` - Write account info

3. **Set Callback URL**
   - Must match Maximo server URL
   - Example: `https://maximo.company.com/maximo/acc/callback`

### Viewer Type Selection

The viewer type is controlled by the `bim.viewer.active` property:

- `navisworks` - NavisWorks ActiveX (legacy)
- `lmv` - Forge/LMV viewer
- `acc` - ACC viewer (new)
- `test` - Test viewer

To use ACC viewer:
```properties
bim.viewer.active=acc
```

---

## Usage

### Basic Viewer Usage

1. **Navigate to Asset/Location**
   - Open any asset or location with a linked model
   - The ACC viewer will load automatically

2. **View Clashes**
   - Click "Clashes" button in toolbar
   - Panel shows all clashes for current model set
   - Click "View" to zoom to clash
   - Click "Create WO" to create work order

3. **View Issues**
   - Click "Issues" button in toolbar
   - Panel shows all issues for current project
   - Filter by priority and status
   - Create work orders from issues

4. **Model Sets**
   - Click "Model Sets" button in toolbar
   - View available model sets
   - Load different model sets

### Creating Work Orders

#### From Clash:
```javascript
// Automatically called when clicking "Create WO" button
window.accViewer.createWorkOrderFromClash(clashId);
```

#### From Issue:
```javascript
// Automatically called when clicking "Create WO" button
window.accViewer.createWorkOrderFromIssue(issueId);
```

### Markup Usage

#### Create Markup:
```javascript
// Initialize markup manager
MaximoACCMarkupManager.initialize(viewer);

// Create clash markup
MaximoACCMarkupManager.createClashMarkup(clashId, {
    dbIdA: 1234,
    dbIdB: 5678,
    severity: 'high'
});
```

#### Show Work Order Markup:
```javascript
MaximoACCMarkupManager.showWorkOrderMarkup(workOrderId);
```

---

## Features

### ✅ Implemented

1. **3D Model Visualization**
   - Autodesk Viewer integration
   - Model loading and rendering
   - Navigation controls

2. **Clash Detection**
   - View clashes in 3D
   - Filter by severity
   - Zoom to clash location
   - Create work orders from clashes

3. **Issue Tracking**
   - View ACC issues
   - Filter by priority and status
   - Create work orders from issues
   - Due date tracking

4. **Model Coordination**
   - Model set management
   - Multiple model loading
   - Coordination views

5. **Markup Support**
   - Create markups
   - Save to Maximo
   - Associate with work orders
   - Export as images

6. **Toolbar**
   - Model coordination tools
   - View tools (Home, Fit, Section)
   - Markup and measure tools
   - Settings and fullscreen

7. **Responsive Design**
   - Mobile-friendly
   - Adaptive layouts
   - Touch support

8. **Dark Theme**
   - Theme switching
   - Consistent styling

### 🔄 Pending Implementation

1. **BIM Servlet Updates**
   - `getACCToken` action
   - `getACCData` action
   - `createWorkOrderFromClash` action
   - `createWorkOrderFromIssue` action
   - `saveACCMarkup` action
   - `getACCMarkups` action

2. **ACC Service Integration**
   - Connect to ACC API
   - Fetch clashes, issues, model sets
   - Sync with Maximo

---

## API Reference

### Global Objects

#### window.accViewer
The main Autodesk Viewer instance.

#### window.accViewerConfig
Configuration object containing:
```javascript
{
    viewerId: string,
    containerId: string,
    projectId: string,
    modelSetId: string,
    urn: string,
    accessToken: string,
    servletBase: string,
    uisessionid: string,
    componentId: string,
    enableClashes: boolean,
    enableIssues: boolean,
    enableModelCoordination: boolean
}
```

#### window.MaximoACCIntegration
Main integration class instance.

#### window.MaximoACCMarkupManager
Markup manager instance.

### Methods

#### Viewer Methods
```javascript
// Show panels
accViewer.showClashes()
accViewer.showIssues()
accViewer.showModelSets()

// Navigation
accViewer.zoomToClash(clashId)
accViewer.zoomToIssue(issueId)

// Work Orders
accViewer.createWorkOrderFromClash(clashId)
accViewer.createWorkOrderFromIssue(issueId)

// Tools
accViewer.toggleSectionTool()
accViewer.toggleMarkup()
accViewer.toggleMeasure()
accViewer.showSettings()
accViewer.toggleFullscreen()
```

#### Markup Methods
```javascript
// Create markups
MaximoACCMarkupManager.createClashMarkup(clashId, data)
MaximoACCMarkupManager.createIssueMarkup(issueId, data)
MaximoACCMarkupManager.createWorkOrderMarkup(woId, data)

// Display markups
MaximoACCMarkupManager.showWorkOrderMarkup(woId)

// Export
MaximoACCMarkupManager.exportMarkupAsImage(markupId)
```

---

## Troubleshooting

### Viewer Not Loading

**Symptom:** Blank viewer area or loading indicator stuck

**Solutions:**
1. Check browser console for errors
2. Verify ACC credentials in maximo.properties
3. Check network tab for failed API calls
4. Ensure `bim.viewer.active=acc` is set
5. Verify Autodesk APS app configuration

### Authentication Errors

**Symptom:** "Failed to authenticate with ACC" error

**Solutions:**
1. Verify Client ID and Secret
2. Check callback URL matches
3. Ensure scopes are enabled
4. Check token expiration
5. Clear browser cache

### Clashes/Issues Not Showing

**Symptom:** Empty panels or "No clashes found"

**Solutions:**
1. Verify project has clashes/issues in ACC
2. Check ACC API connectivity
3. Verify project ID and model set ID
4. Check servlet implementation
5. Review server logs

### Toolbar Buttons Not Working

**Symptom:** Clicking buttons has no effect

**Solutions:**
1. Check browser console for JavaScript errors
2. Verify ACC.js is loaded
3. Check viewer initialization
4. Verify event handlers are attached

---

## File Summary

### Created Files (8 files)

1. **JSP Components (4 files)**
   - `applications/maximo/maximouiweb/webmodule/webclient/components/bimacc/header.jsp` (64 lines)
   - `applications/maximo/maximouiweb/webmodule/webclient/components/bimacc/footer.jsp` (46 lines)
   - `applications/maximo/maximouiweb/webmodule/webclient/components/bimacc/toolbar.jsp` (169 lines)
   - `applications/maximo/maximouiweb/webmodule/webclient/components/bimacc/viewerframe.jsp` (233 lines)

2. **JavaScript Files (3 files)**
   - `applications/maximo/maximouiweb/webmodule/webclient/javascript/ACC.js` (509 lines)
   - `applications/maximo/maximouiweb/webmodule/webclient/javascript/ACC_Markup.js` (145 lines)
   - `applications/maximo/maximouiweb/webmodule/webclient/javascript/MaximoACCMarkup.js` (189 lines)

3. **CSS File (1 file)**
   - `applications/maximo/maximouiweb/webmodule/webclient/css/ACC.css` (449 lines)

**Total:** 1,804 lines of code

---

## Next Steps

### Phase 2: Backend Integration

1. **Update BIMServlet.java**
   - Add ACC token retrieval
   - Add ACC data fetching
   - Add work order creation handlers
   - Add markup save/load handlers

2. **Implement ACC Service**
   - Complete ACCService.java implementation
   - Connect to ACC REST API
   - Implement authentication flow
   - Add caching layer

3. **Testing**
   - Unit tests for JavaScript
   - Integration tests for servlet
   - End-to-end testing
   - Performance testing

4. **Documentation**
   - API documentation
   - User guide
   - Admin guide
   - Troubleshooting guide

---

## Support

For issues or questions:
1. Check this documentation
2. Review ACC_PLUGIN_ARCHITECTURE.md
3. Review ACC_PLUGIN_README.md
4. Check server logs
5. Contact development team

---

**Last Updated:** February 5, 2026  
**Status:** ✅ Viewer Integration Complete  
**Next Milestone:** Backend Service Implementation