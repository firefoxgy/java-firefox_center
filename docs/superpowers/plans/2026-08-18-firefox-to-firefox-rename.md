# CCWB to Firefox Rename Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace every project-owned CCWB identifier with its Firefox equivalent while preserving behavior and existing uncommitted work.

**Architecture:** Establish executable audits, replace text in controlled source/configuration files, then rename files and directories deepest-first. Verify package paths, Maven modules, service routes, frontend references, and builds after the repository root moves.

**Tech Stack:** Java 8, Maven, Spring Boot 2.3.10, Spring Cloud Hoxton SR11, Nacos, Vue 2, npm, POSIX shell, Git

**Spec:** `docs/superpowers/specs/2026-08-18-ccwb-to-firefox-rename-design.md`

## Global Constraints

- Apply `ccwb → firefox`, `CCWB → FIREFOX`, and `Ccwb → Firefox` to project-owned identifiers.
- Rename Java namespace `com.ccwb` to `com.firefox`.
- Rename repository root `java-ccwb_center` to `java-firefox_center`.
- Exclude `.git`, `target`, `logs`, IDE caches, and runtime logs from content rewriting.
- Preserve third-party and externally owned identifiers.
- Preserve all pre-existing uncommitted changes; never use Git restore/reset.
- Do not mutate systems outside this repository.

---

### Task 1: Baseline and Executable Audit

**Files:**
- Create: `scripts/verify-firefox-rename.sh`
- Inspect: all files outside excluded directories

**Interfaces:**
- Consumes: current Git status and filesystem
- Produces: an audit returning zero only when old project identifiers are absent and Java packages match paths

- [ ] **Step 1: Capture existing changes**

Run `git status --short` and targeted `git diff` for the modified parent POM and gateway encryption filter. Preserve the output for final comparison.

- [ ] **Step 2: Create the failing audit**

Create a strict POSIX shell script which prunes `.git`, `target`, `logs`, `.idea`, and `.vscode`; fails on project-owned case variants of CCWB; and checks each Java package declaration against the path below `src/main/java`.

- [ ] **Step 3: Prove the audit fails before migration**

Run `sh scripts/verify-firefox-rename.sh`.

Expected: FAIL with representative old identifiers.

- [ ] **Step 4: Commit only the audit**

```bash
git add scripts/verify-firefox-rename.sh
git commit -m "test: add firefox rename audit"
```

### Task 2: Replace Text Identifiers

**Files:**
- Modify: textual source/configuration files under `ccwb-cloud`, `ui-ant-design-vue`, root documentation, and project scripts
- Exclude: `.git/**`, `**/target/**`, `logs/**`, `.idea/**`, `.vscode/**`, `spy.log`, and binary files

**Interfaces:**
- Consumes: exact casing map from Global Constraints
- Produces: Firefox text identifiers while paths may temporarily retain CCWB names

- [ ] **Step 1: Enumerate candidate text files**

Use tracked files plus explicit untracked source/documentation files, remove excluded paths, classify with `file`, and inspect all non-text results before proceeding.

- [ ] **Step 2: Apply collision-safe replacements**

Replace uppercase `CCWB`, then title-case `Ccwb`, then lowercase `ccwb`. Do not perform a case-insensitive replacement because Java class casing must remain meaningful.

- [ ] **Step 3: Review semantic changes**

Inspect Maven coordinates, Spring application names, Nacos keys, Gateway `lb://` targets, datasource URLs, SQL identifiers, image names, frontend paths, Java packages/imports/class names, and documentation. Correct false-positive external identifiers line-by-line without reverting whole files.

- [ ] **Step 4: Run the residual audit**

Confirm remaining failures are exclusively names of paths awaiting Task 3.

### Task 3: Rename Files, Directories, and Repository Root

**Files:**
- Rename: project-owned basenames containing a mapped CCWB variant
- Rename: `java-ccwb_center` → `java-firefox_center`

**Interfaces:**
- Consumes: migrated text references
- Produces: a tree rooted at `/Users/yungeng/Documents/ChatGPT/micro_service/java-firefox_center`

- [ ] **Step 1: Generate and inspect a rename manifest**

Sort affected paths deepest-first, apply exact casing rules to basenames, and fail before execution if destinations collide or already exist.

- [ ] **Step 2: Rename nested paths**

Apply the inspected manifest deepest-first using explicit validated source/destination paths, with no unresolved move globs.

- [ ] **Step 3: Rename repository root**

From the parent directory, rename the validated root and switch all subsequent commands to `/Users/yungeng/Documents/ChatGPT/micro_service/java-firefox_center`.

- [ ] **Step 4: Run the full audit**

Run `sh scripts/verify-firefox-rename.sh`.

Expected: PASS with no old identifier or Java package/path mismatch.

### Task 4: Cross-Module and Build Verification

**Files:**
- Inspect/repair: `firefox-cloud/pom.xml`, child POMs, Bootstrap/Application configuration, SQL, frontend configuration, and Gateway routes
- Modify: only references that fail consistency checks

**Interfaces:**
- Consumes: migrated Firefox tree
- Produces: consistent Maven coordinates, service names/routes, and frontend references

- [ ] **Step 1: Validate Maven modules and internal coordinates**

Resolve each `<module>` relative to its containing POM and fail if its directory/POM is absent. Compare `com.firefox` internal dependencies with module coordinates.

- [ ] **Step 2: Validate service discovery references**

Compare `spring.application.name` values with Gateway `lb://` targets, distinguishing project-owned Firefox services from documented external services.

- [ ] **Step 3: Compile backend**

From `firefox-cloud`, run `mvn -DskipTests compile`.

Expected: BUILD SUCCESS. If repositories are unavailable, record the exact environmental failure and fix all local migration errors reached before it.

- [ ] **Step 4: Check frontend**

If `node_modules` exists, run `npm run lint` and `npm run build`. If absent, do not install without authorization; perform static reference/configuration checks and report unavailable build verification.

- [ ] **Step 5: Final audits and diff review**

```bash
sh scripts/verify-firefox-rename.sh
git status --short
git diff --check
```

Expected: audit PASS, no whitespace errors/generated output, and the pre-existing POM/gateway edits remain in their renamed files.

- [ ] **Step 6: Commit the migration**

Exclude `spy.log` and generated files, inspect `git diff --cached --stat` and `git diff --cached --check`, then commit:

```bash
git commit -m "refactor: rename ccwb platform to firefox"
```
