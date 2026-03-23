# Git Sync Guide - Check for Remote Changes

This guide shows you how to check if the main branch on GitHub has changed before promoting your local branch.

---

## Quick Command Reference

```bash
# 1. Fetch latest changes from remote (doesn't modify your files)
git fetch origin

# 2. Compare your current branch with remote main
git log HEAD..origin/main --oneline

# 3. Check if there are differences
git diff HEAD origin/main --stat

# 4. See detailed differences
git diff HEAD origin/main
```

---

## Step-by-Step Process

### Step 1: Check Your Current Branch and Status

```bash
# See what branch you're on
git branch

# Check your current status
git status

# See your recent commits
git log --oneline -5
```

**Expected Output:**
```
* performance-optimizations
  main
```

### Step 2: Fetch Latest Changes from GitHub

This downloads the latest information from GitHub **without** modifying your local files:

```bash
# Fetch all branches from origin (GitHub)
git fetch origin

# Or fetch just main branch
git fetch origin main
```

**What this does:**
- Downloads latest commits from GitHub
- Updates your local copy of remote branches (origin/main)
- Does NOT modify your working files
- Safe to run anytime

### Step 3: Compare Your Branch with Remote Main

#### Option A: See if there are new commits on main

```bash
# List commits on origin/main that you don't have
git log HEAD..origin/main --oneline
```

**Interpretation:**
- **No output** = Your branch is up to date with main
- **Shows commits** = Main has new changes you don't have

**Example Output (if main has changed):**
```
abc1234 Fix bug in authentication
def5678 Update documentation
```

#### Option B: See if your branch has diverged

```bash
# Show commits on both sides
git log --left-right --oneline HEAD...origin/main
```

**Interpretation:**
- Lines starting with `<` = Your commits not on main
- Lines starting with `>` = Main's commits you don't have

**Example Output:**
```
< 9876543 Implement token cache manager
< 8765432 Add composite database indexes
> abc1234 Fix bug in authentication
> def5678 Update documentation
```

### Step 4: Check File Differences

```bash
# See which files have changed (summary)
git diff HEAD origin/main --stat

# See detailed differences
git diff HEAD origin/main

# Check specific file
git diff HEAD origin/main -- path/to/file.java
```

### Step 5: Visualize the Branch History

```bash
# See branch graph
git log --graph --oneline --all -20

# More detailed view
git log --graph --oneline --decorate --all -20
```

---

## Decision Matrix

### Scenario 1: No Changes on Main
```bash
git log HEAD..origin/main --oneline
# (no output)
```

**Action:** ✅ Safe to push/merge your changes
```bash
git push origin performance-optimizations
# Create pull request on GitHub
```

### Scenario 2: Main Has New Changes (No Conflicts)
```bash
git log HEAD..origin/main --oneline
# Shows some commits
```

**Action:** Update your branch first
```bash
# Option A: Rebase (cleaner history)
git rebase origin/main

# Option B: Merge (preserves history)
git merge origin/main

# Then push
git push origin performance-optimizations
```

### Scenario 3: Main Has Changes (Potential Conflicts)
```bash
git diff HEAD origin/main --stat
# Shows changes to same files you modified
```

**Action:** Carefully merge and resolve conflicts
```bash
# Start merge
git merge origin/main

# If conflicts occur, Git will tell you which files
# Edit conflicted files, then:
git add <resolved-files>
git commit

# Push updated branch
git push origin performance-optimizations
```

---

## Recommended Workflow

### Before Creating Pull Request

```bash
# 1. Ensure you're on your feature branch
git checkout performance-optimizations

# 2. Commit all your changes
git add .
git commit -m "Complete quick wins implementation"

# 3. Fetch latest from GitHub
git fetch origin

# 4. Check for changes on main
git log HEAD..origin/main --oneline

# 5. If main has changes, update your branch
git rebase origin/main
# OR
git merge origin/main

# 6. Run tests to ensure everything still works
# (your test commands here)

# 7. Push your branch
git push origin performance-optimizations

# 8. Create Pull Request on GitHub
```

---

## Detailed Commands Explained

### `git fetch origin`
- Downloads commits, files, and refs from remote repository
- Updates your local copy of remote branches (origin/main, origin/develop, etc.)
- **Does NOT** modify your working directory or current branch
- Safe to run frequently

### `git log HEAD..origin/main`
- Shows commits that are on origin/main but not on your current branch
- Empty output = you're up to date
- Shows commits = main has moved ahead

### `git diff HEAD origin/main`
- Shows actual file differences between your branch and main
- `--stat` flag shows summary (which files, how many lines changed)
- Without `--stat` shows detailed line-by-line differences

### `git rebase origin/main`
- Replays your commits on top of origin/main
- Creates a linear history (cleaner)
- **Rewrites commit history** (don't use on shared branches)
- Better for feature branches before merging

### `git merge origin/main`
- Creates a merge commit combining both histories
- Preserves complete history
- Safe for shared branches
- Shows explicit merge points

---

## Checking Specific Files

If you want to check if specific files you modified have changed on main:

```bash
# Check if a specific file changed on main
git diff HEAD origin/main -- applications/maximo/businessobjects/src/psdi/app/bim/viewer/acc/ACCTokenCacheManager.java

# List all files that changed on main
git diff --name-only HEAD origin/main

# Check if any of your modified files changed on main
git diff --name-only HEAD origin/main | grep -f <(git diff --name-only HEAD)
```

---

## Visual Tools

### Using Git GUI Tools

If you prefer visual tools:

```bash
# GitKraken, SourceTree, GitHub Desktop, or VS Code Git Graph extension
# These provide visual branch comparisons
```

### VS Code Git Integration

1. Open Source Control panel (Ctrl+Shift+G)
2. Click "..." menu → "Fetch"
3. View "REMOTES" section to see origin/main
4. Right-click your branch → "Compare with..." → "origin/main"

---

## Common Scenarios

### Scenario: You're Behind Main

```bash
$ git log HEAD..origin/main --oneline
abc1234 Fix authentication bug
def5678 Update dependencies

# Solution: Update your branch
$ git rebase origin/main
# Or
$ git merge origin/main
```

### Scenario: You're Ahead of Main

```bash
$ git log origin/main..HEAD --oneline
9876543 Implement token cache manager
8765432 Add composite indexes

# This is normal - these are your new changes
# Ready to create pull request
```

### Scenario: Branches Have Diverged

```bash
$ git log --left-right --oneline HEAD...origin/main
< 9876543 Implement token cache manager
< 8765432 Add composite indexes
> abc1234 Fix authentication bug
> def5678 Update dependencies

# Solution: Merge or rebase to combine changes
$ git rebase origin/main
# Or
$ git merge origin/main
```

---

## Safety Tips

### Before Rebasing or Merging

```bash
# 1. Create a backup branch
git branch backup-performance-optimizations

# 2. Ensure working directory is clean
git status
# Should show "nothing to commit, working tree clean"

# 3. If you have uncommitted changes, stash them
git stash save "WIP before sync"

# 4. Proceed with rebase/merge
git rebase origin/main

# 5. If something goes wrong, abort
git rebase --abort
# Or restore from backup
git reset --hard backup-performance-optimizations

# 6. Restore stashed changes if needed
git stash pop
```

---

## Quick Reference Card

```bash
# Check for remote changes
git fetch origin && git log HEAD..origin/main --oneline

# Update your branch (choose one)
git rebase origin/main    # Clean history
git merge origin/main     # Preserve history

# Check differences
git diff HEAD origin/main --stat

# See branch status
git status
git log --graph --oneline --all -10

# Push your changes
git push origin performance-optimizations

# If push rejected (history rewritten)
git push --force-with-lease origin performance-optimizations
```

---

## For Your Current Situation

Based on your performance optimizations work:

```bash
# 1. Check current status
cd /Users/clydeicuspit/MAXIMOFORGEVIEWERPLUGIN_Java17
git status

# 2. Fetch latest from GitHub
git fetch origin

# 3. Check if main has changed
git log HEAD..origin/main --oneline

# 4. If output is empty: Safe to push
# If output shows commits: Need to sync first

# 5. Check which files changed (if any)
git diff HEAD origin/main --stat

# 6. Update your branch if needed
git rebase origin/main
# Or if you prefer merge
git merge origin/main

# 7. Push your changes
git push origin performance-optimizations

# 8. Create Pull Request on GitHub
```

---

## Troubleshooting

### "Your branch and 'origin/main' have diverged"

```bash
# See what diverged
git log --left-right --oneline HEAD...origin/main

# Choose resolution strategy
git rebase origin/main    # Rewrite history (cleaner)
git merge origin/main     # Preserve history
```

### "Conflict in file X"

```bash
# See conflicted files
git status

# Open conflicted file, look for:
<<<<<<< HEAD
Your changes
=======
Their changes
>>>>>>> origin/main

# Edit to resolve, then:
git add <resolved-file>
git rebase --continue
# Or if merging
git commit
```

### "Push rejected"

```bash
# If you rebased, history changed
git push --force-with-lease origin performance-optimizations

# --force-with-lease is safer than --force
# It checks that remote hasn't changed since you last fetched
```

---

## Best Practices

1. **Fetch frequently** - `git fetch origin` daily
2. **Check before pushing** - Always check for remote changes first
3. **Keep commits clean** - Squash/rebase before merging to main
4. **Test after sync** - Run tests after merging/rebasing
5. **Use --force-with-lease** - Never use plain `--force`
6. **Communicate** - Let team know about major changes

---

## Summary

**To check if main has changed:**
```bash
git fetch origin
git log HEAD..origin/main --oneline
```

**If no output:** ✅ You're up to date, safe to push

**If shows commits:** ⚠️ Main has changed, sync first:
```bash
git rebase origin/main  # or git merge origin/main
```

**Then push:**
```bash
git push origin performance-optimizations
```

---

**Quick Start Command:**
```bash
git fetch origin && git log HEAD..origin/main --oneline && echo "---" && git diff HEAD origin/main --stat
```

This single command will:
1. Fetch latest changes
2. Show new commits on main
3. Show file differences summary

---

**Need Help?** Run `git status` and `git log --graph --oneline --all -10` to see current state.