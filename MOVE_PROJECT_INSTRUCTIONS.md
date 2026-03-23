# Instructions to Move Weekly Menu Planner Project

## Quick Move Commands

Run these commands in your terminal to move the project to `/Users/clydeicuspit/Projects/weekly-menu-planner`:

```bash
# Create the Projects directory if it doesn't exist
mkdir -p /Users/clydeicuspit/Projects

# Move the weekly-menu-planner directory
mv /Users/clydeicuspit/MAXIMOFORGEVIEWERPLUGIN_Java17/weekly-menu-planner /Users/clydeicuspit/Projects/

# Verify the move
ls -la /Users/clydeicuspit/Projects/weekly-menu-planner

# Open the new location in VS Code
code /Users/clydeicuspit/Projects/weekly-menu-planner
```

## Alternative: Copy Instead of Move

If you want to keep a copy in both locations:

```bash
# Create the Projects directory if it doesn't exist
mkdir -p /Users/clydeicuspit/Projects

# Copy the directory
cp -r /Users/clydeicuspit/MAXIMOFORGEVIEWERPLUGIN_Java17/weekly-menu-planner /Users/clydeicuspit/Projects/

# Verify the copy
ls -la /Users/clydeicuspit/Projects/weekly-menu-planner

# Open the new location in VS Code
code /Users/clydeicuspit/Projects/weekly-menu-planner
```

## What Gets Moved

The following files will be moved to the new location:

```
/Users/clydeicuspit/Projects/weekly-menu-planner/
├── README.md
├── ARCHITECTURE.md
├── UI_WIREFRAMES.md
├── IMPLEMENTATION_ROADMAP.md
├── RECIPE_API_INTEGRATION.md
└── PROJECT_SETUP_GUIDE.md
```

## After Moving

1. Open the new location in VS Code:
   ```bash
   code /Users/clydeicuspit/Projects/weekly-menu-planner
   ```

2. Initialize Git repository (if not already done):
   ```bash
   cd /Users/clydeicuspit/Projects/weekly-menu-planner
   git init
   git add .
   git commit -m "Initial commit: Weekly Menu Planner architecture and planning"
   ```

3. Start following the implementation roadmap in `IMPLEMENTATION_ROADMAP.md`

## Cleanup (Optional)

If you moved (not copied) the files and want to remove the reference from the Maximo project:

```bash
# This is only needed if you used 'mv' command
# The directory should already be gone from the Maximo project
```

## Verify Everything Works

After moving, verify all documentation is accessible:

```bash
cd /Users/clydeicuspit/Projects/weekly-menu-planner
ls -la
cat README.md
```

You should see all 6 documentation files in the new location.