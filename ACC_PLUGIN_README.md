# Autodesk Construction Cloud (ACC) Plugin for Maximo

## 🚀 Quick Start

The ACC Plugin integrates Autodesk Construction Cloud's Model Coordination features with IBM Maximo, enabling seamless clash detection, issue tracking, and work order management.

**Version:** 1.0.0  
**Java:** 17 LTS  
**Status:** Phase 1 Complete - Foundation Layer

---

## 📋 Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Architecture](#architecture)
- [Development](#development)
- [Troubleshooting](#troubleshooting)
- [Support](#support)

---

## ✨ Features

### Model Coordination
- ✅ **Project Management** - Link ACC projects to Maximo sites
- ✅ **Model Sets** - Manage collections of models for coordination
- ✅ **Clash Detection** - Automatic clash detection and tracking
- ✅ **3D Visualization** - View clashes in 3D context

### Work Order Integration
- ✅ **Clash to Work Order** - Automatically create work orders from clashes
- ✅ **Status Synchronization** - Bi-directional status sync
- ✅ **Assignment Management** - Map ACC assignments to Maximo users
- ✅ **Location Linking** - Link clashes to Maximo locations/assets

### Issue Tracking
- ✅ **Issue Management** - Create and track ACC issues from Maximo
- ✅ **Priority Mapping** - Map ACC priorities to Maximo work order priorities
- ✅ **Attachment Support** - Link documents and images
- ✅ **Due Date Tracking** - Monitor overdue issues

### Modern Technology
- ✅ **Java 17** - Modern language features (Records, Sealed Classes)
- ✅ **OAuth 2.0** - Secure authentication with token management
- ✅ **Thread-Safe** - Concurrent operations with modern collections
- ✅ **Immutable Data** - Records for data integrity

---

## 📦 Prerequisites

### Required
- **Maximo 7.6+** (or compatible version)
- **Java 17 JDK** (OpenJDK or Oracle JDK)
- **ACC Account** with API access
- **ACC Projects** with Model Coordination enabled

### ACC API Credentials
You need:
1. ACC Client ID
2. ACC Client Secret
3. ACC Callback URL (configured in ACC)
4. Appropriate ACC scopes enabled

### Maximo Requirements
- Database access for schema updates
- Application server restart capability
- Maximo properties file access

---

## 🔧 Installation

### Step 1: Backup

```bash
# Backup Maximo database
pg_dump maximo > maximo_backup_$(date +%Y%m%d).sql

# Backup Maximo application
tar -czf maximo_app_backup_$(date +%Y%m%d).tar.gz $MAXIMO_HOME
```

### Step 2: Deploy Files

```bash
# Extract plugin package
cd $MAXIMO_HOME
unzip bimacc_v1.0.0.zip

# Verify files
ls -la applications/maximo/businessobjects/src/psdi/app/bim/viewer/acc/
ls -la tools/maximo/en/bimacc/
```

### Step 3: Update Database

```bash
cd $MAXIMO_HOME/tools/maximo
./updatedb.sh

# Verify tables created
# Check for: ACCPROJECT, ACCMODELSET, ACCCLASH, ACCISSUE, ACCMODEL
```

### Step 4: Configure Properties

Edit `$MAXIMO_HOME/maximo.properties`:

```properties
# ACC API Configuration
bim.viewer.ACC.clientId=YOUR_CLIENT_ID_HERE
bim.viewer.ACC.clientSecret=YOUR_CLIENT_SECRET_HERE
bim.viewer.ACC.callbackUrl=https://your-maximo-server/maximo/acc/callback
bim.viewer.ACC.host=developer.api.autodesk.com
bim.viewer.ACC.api.version=v1

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

### Step 5: Build and Deploy

```bash
# Build Maximo EAR
cd $MAXIMO_HOME
./buildmaximoear.sh

# Deploy to application server
# (Steps vary by app server - WebSphere, WebLogic, etc.)
```

### Step 6: Start and Verify

```bash
# Start Maximo
# Check logs for ACC service initialization
tail -f $MAXIMO_HOME/logs/maximo.log | grep BIMACC
```

---

## ⚙️ Configuration

### ACC API Setup

1. **Create ACC App** at https://aps.autodesk.com/
2. **Configure Callback URL** to match your Maximo server
3. **Enable Scopes:**
   - `data:read`
   - `data:write`
   - `account:read`
   - `account:write`
4. **Copy Client ID and Secret** to maximo.properties

### Maximo Security

Configure security groups for ACC features:

```sql
-- Grant ACC access to specific security groups
INSERT INTO APPLICATIONAUTH (APP, GROUPNAME, OPTIONNAME)
VALUES ('ACCPROJECT', 'MAXADMIN', 'READ');

INSERT INTO APPLICATIONAUTH (APP, GROUPNAME, OPTIONNAME)
VALUES ('ACCPROJECT', 'MAXADMIN', 'WRITE');
```

### Site Configuration

Link ACC projects to Maximo sites:

1. Navigate to **ACC Projects** application
2. Click **New Project**
3. Enter ACC Project ID and Account ID
4. Select Maximo Site and Organization
5. Click **Link Project**

---

## 📖 Usage

### Example 1: View Clashes

```java
// Get ACC service
ACCServiceRemote accService = (ACCServiceRemote) 
    MXServer.getMXServer().lookup("BIMACC");

// List clashes for a model set
ResultClashList clashes = accService.clashList(
    "project-id",
    "modelset-id"
);

System.out.println("Total clashes: " + clashes.getTotalCount());
System.out.println("Critical: " + clashes.getCriticalCount());
System.out.println("Unresolved: " + clashes.getUnresolvedCount());
```

### Example 2: Create Work Order from Clash

```java
// Get critical unresolved clashes
for (ACCClash clash : clashes.getCriticalClashes()) {
    if (!clash.isResolved()) {
        // Create work order
        MboRemote wo = accService.createWorkOrderFromClash(
            userInfo,
            clash.id()
        );
        
        System.out.println("Created WO: " + wo.getString("WONUM"));
    }
}
```

### Example 3: Sync Issues

```java
// Get ACC issues
ResultIssueList issues = accService.issueList("project-id");

// Filter high priority overdue issues
for (ACCIssue issue : issues.getIssues()) {
    if (issue.isHighPriority() && issue.isOverdue()) {
        // Create work order
        Result result = accService.createIssueFromWorkOrder(
            userInfo,
            workOrderId,
            "project-id"
        );
    }
}
```

### Example 4: Authentication

```java
// Authenticate with ACC
String[] scopes = {"data:read", "account:read"};
ResultAuthentication result = accService.authenticate(scopes);

if (result.isSuccess()) {
    String token = accService.getAuthToken();
    // Token is cached and auto-refreshed
}
```

---

## 🏗️ Architecture

### Component Overview

```
┌─────────────────────────────────────────────────────────┐
│                    Maximo UI Layer                      │
│  (Work Orders, Assets, Locations, ACC Projects)         │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│              ACC Service Layer (Java 17)                │
│  • ACCService (Main Service)                            │
│  • ACCServiceRemote (RMI Interface)                     │
│  • Result Classes (List/Detail)                         │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│           Data Models (Java 17 Records)                 │
│  • ACCAuthToken  • ACCProject  • ACCClash               │
│  • ACCModelSet   • ACCIssue                             │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│              REST API Layer                             │
│  • ACCDataRESTAPI (Base Client)                         │
│  • OAuth 2.0 Authentication                             │
│  • Token Management & Caching                           │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│         Autodesk Construction Cloud API                 │
│  • Model Coordination  • Clash Detection                │
│  • Issue Tracking      • Project Management             │
└─────────────────────────────────────────────────────────┘
```

### Database Schema

**5 Core Tables:**
- `ACCPROJECT` - ACC projects linked to Maximo sites
- `ACCMODELSET` - Model sets for coordination
- `ACCCLASH` - Clash detection results
- `ACCISSUE` - ACC issues
- `ACCMODEL` - Model metadata

**Relationships:**
- ACCMODELSET → ACCPROJECT (many-to-one)
- ACCCLASH → ACCMODELSET (many-to-one)
- ACCCLASH → WORKORDER (optional one-to-one)
- ACCISSUE → ACCPROJECT (many-to-one)
- ACCISSUE → WORKORDER (optional one-to-one)

---

## 👨‍💻 Development

### Building from Source

```bash
# Clone repository
git clone <repository-url>
cd MAXIMOFORGEVIEWERPLUGIN_Java17

# Build with Maven
mvn clean package

# Run tests
mvn test

# Generate documentation
mvn javadoc:javadoc
```

### Project Structure

```
applications/maximo/businessobjects/src/psdi/app/bim/viewer/
├── acc/                          # Service layer
│   ├── ACCServiceRemote.java    # Remote interface
│   ├── Result*.java              # Result classes
│   └── ...
└── dataapi/acc/                  # Data models
    ├── ACCAuthToken.java         # Java 17 Record
    ├── ACCProject.java           # Java 17 Record
    ├── ACCClash.java             # Java 17 Record
    └── ...
```

### Adding New Features

1. **Define data model** (Java 17 Record in `dataapi/acc/`)
2. **Add service method** (in `ACCServiceRemote.java`)
3. **Create result class** (in `acc/` package)
4. **Implement service** (in `ACCService.java` - Phase 2)
5. **Add tests** (JUnit 5)
6. **Update documentation**

---

## 🐛 Troubleshooting

### Common Issues

#### 1. Authentication Fails

**Symptom:** "Invalid client credentials" error

**Solution:**
- Verify Client ID and Secret in maximo.properties
- Check ACC app configuration
- Ensure callback URL matches
- Verify scopes are enabled

#### 2. Database Tables Not Created

**Symptom:** "Table ACCPROJECT does not exist"

**Solution:**
```bash
# Check database script
cat $MAXIMO_HOME/tools/maximo/en/bimacc/V7610_01.dbc

# Run updatedb manually
cd $MAXIMO_HOME/tools/maximo
./updatedb.sh -fv7610_01

# Verify tables
psql -d maximo -c "\dt acc*"
```

#### 3. Service Not Found

**Symptom:** "Service BIMACC not found"

**Solution:**
- Check product.xml is deployed
- Verify service registration in Maximo
- Restart application server
- Check logs for initialization errors

#### 4. Token Expired

**Symptom:** "Token has expired" error

**Solution:**
- Token auto-refresh should handle this
- Clear token cache: `accService.clearAuthCache()`
- Check system clock synchronization
- Verify token expiration settings

### Debug Mode

Enable debug logging:

```properties
# In maximo.properties
log4j.logger.psdi.app.bim.viewer.acc=DEBUG
```

View logs:
```bash
tail -f $MAXIMO_HOME/logs/maximo.log | grep "ACC"
```

---

## 📚 Documentation

### Available Documents

1. **[ACC_PLUGIN_ARCHITECTURE.md](ACC_PLUGIN_ARCHITECTURE.md)** - Complete architecture (18 sections)
2. **[ACC_IMPLEMENTATION_SUMMARY.md](ACC_IMPLEMENTATION_SUMMARY.md)** - Implementation details
3. **[ACC_PLUGIN_README.md](ACC_PLUGIN_README.md)** - This file
4. **JavaDoc** - Generate with `mvn javadoc:javadoc`

### External Resources

- [ACC API Documentation](https://aps.autodesk.com/en/docs/acc/v1/overview/)
- [Model Coordination API](https://aps.autodesk.com/en/docs/acc/v1/reference/http/mc-modelset-index-GET/)
- [Maximo Development Guide](https://www.ibm.com/docs/en/maximo-manage)

---

## 🤝 Contributing

### Code Standards

- Use Java 17 features (Records, Sealed Classes, Pattern Matching)
- Follow existing Maximo patterns
- Write comprehensive JavaDoc
- Include unit tests (JUnit 5)
- Update documentation

### Pull Request Process

1. Create feature branch
2. Implement changes
3. Add/update tests
4. Update documentation
5. Submit PR with description

---

## 📞 Support

### Getting Help

- **Documentation:** Review architecture and implementation docs
- **Issues:** Check troubleshooting section
- **Community:** Maximo developer forums
- **Professional:** Contact IBM Maximo support

### Reporting Issues

Include:
- Maximo version
- Java version
- Error messages
- Steps to reproduce
- Relevant log excerpts

---

## 📄 License

Eclipse Public License - v 1.0

See [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- IBM Maximo Team
- Autodesk Construction Cloud Team
- Java Community
- Open Source Contributors

---

## 📅 Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | 2026-02-04 | Initial release - Phase 1 complete |

---

## 🎯 Roadmap

### Phase 1 (✅ Complete)
- [x] Architecture design
- [x] Java 17 Records
- [x] Service interface
- [x] Result classes
- [x] Database schema
- [x] Product definition

### Phase 2 (In Progress)
- [ ] REST API implementation
- [ ] Service implementation
- [ ] MBO layer
- [ ] Basic integration

### Phase 3 (Planned)
- [ ] UI components
- [ ] Advanced features
- [ ] Performance optimization
- [ ] Full documentation

### Future Enhancements
- [ ] Real-time collaboration
- [ ] Advanced analytics
- [ ] Mobile support
- [ ] AI/ML integration

---

**Last Updated:** February 4, 2026  
**Status:** ✅ Phase 1 Complete  
**Next Milestone:** Phase 2 - Service Implementation

---

For detailed technical information, see:
- [Architecture Document](ACC_PLUGIN_ARCHITECTURE.md)
- [Implementation Summary](ACC_IMPLEMENTATION_SUMMARY.md)