# Feature Roadmap Documentation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking. Do not delegate or use subagents unless the user explicitly requests it.

**Goal:** Establish `docs/ROADMAP.md` as the canonical F01-F12 roadmap, fully define F01.M01-F01.M08, and migrate current documentation from the flat M0-M10 taxonomy without rewriting historical evidence.

**Architecture:** Keep roadmap status separate from architectural intent and dated evidence. `docs/ROADMAP.md` owns current feature/milestone status and gates; the architecture owns system behavior; `MODLOG.md` owns observed evidence; existing M1 artifacts remain at stable paths and gain F01.M02 annotations.

**Tech Stack:** Markdown documentation, repository-local static validation with `rg` and `sed`.

**Spec:** `docs/superpowers/specs/2026-10-06-feature-roadmap-taxonomy-design.md`

## Global Constraints

- Use stable identifiers `F01` through `F12` and feature-local milestone identifiers such as `F01.M01`.
- Fully define only F01.M01-F01.M08 in this revision; F02-F12 receive feature-level scope and dependencies only.
- Preserve all historical `MODLOG.md` entries and existing M1 filenames.
- Do not claim F01.M01 or F01.M02 are Accepted while user-run full-stack gates remain pending.
- Treat `docs/ROADMAP.md` as current status authority, architecture as intended behavior, and `MODLOG.md` as evidence authority.
- The agent may edit documentation and run read-only/static checks only for this task.
- Do not launch Minecraft, a dedicated server, a GameTest runtime, or any other game process.
- Do not modify product code, Gradle configuration, runtime profiles, saves, worlds, or dependency jars.
- Do not perform any Git operation or commit. The user handles all Git operations.

## Review Focus

- Historical M1 statements must remain understandable after F01.M02 becomes the canonical identifier.
- Current statuses must distinguish Implemented from Accepted and must not imply unperformed multiplayer or package tests passed.
- The F01 summary, detailed milestone sections, and gate matrix must use identical names and statuses.
- Architecture references to former M4-M9 gates must point to the correct new feature identifiers.
- No document may revive milestone-specific client or server profiles.

---

### Task 1: Create the canonical roadmap and status dashboard

**Files:**
- Create: `docs/ROADMAP.md`
- Read: `docs/superpowers/specs/2026-10-06-feature-roadmap-taxonomy-design.md`

**Interfaces:**
- Consumes: Approved feature catalog, gate vocabulary, F01 definitions, migration table, and user/agent ownership boundary from the specification.
- Produces: The canonical current-status document linked by every later task.

- [x] **Step 1: Create the roadmap header and governance sections**

Add purpose, identifier syntax, document responsibilities, status vocabulary, gate-result vocabulary, evidence rules, and testing ownership. State that only all required passing gates can produce Accepted status.

- [x] **Step 2: Add the F01-F12 catalog**

Copy the approved names, scope summaries, and principal dependencies exactly from the specification. Explain that dependency readiness, rather than numeric order alone, controls execution.

- [x] **Step 3: Add the current status dashboard and F01 gate matrix**

Set F01.M01 and F01.M02 to Implemented with pending user-run client/server/multiplayer gates. Set F01.M03-F01.M08 to Not started. Show each required gate as Pass, Pending, or Not required only where the available evidence supports it; link evidence to the relevant `MODLOG.md` headings rather than inventing results.

- [x] **Step 4: Add detailed F01 milestone cards**

For F01.M01-F01.M08 include playable result, scope, required gates, acceptance evidence, dependencies, owner split, current status, and next gate. Keep the wording consistent with the approved specification.

- [x] **Step 5: Add legacy identifier migration and update procedure**

Include the approved M0-M10 mapping. Define that future status changes update the dashboard/matrix and append evidence to `MODLOG.md` in the same documentation change.

- [x] **Step 6: Verify internal roadmap consistency**

Run:

```bash
rg -n "^#|F01\.M0[1-8]|\| F(0[1-9]|1[0-2]) \||Implemented|Accepted|Not started|Pending|Pass" docs/ROADMAP.md
```

Expected: all twelve features and all eight F01 milestones appear; only F01.M01 and F01.M02 are Implemented; no milestone is marked Accepted.

### Task 2: Convert the master architecture to the feature taxonomy

**Files:**
- Modify: `docs/Bleach_Fractured_World_Architecture.md:287-358`
- Read: `docs/ROADMAP.md`

**Interfaces:**
- Consumes: Canonical feature names, current status authority, and legacy mapping from Task 1.
- Produces: Architecture references that use feature/milestone identifiers while retaining behavioral invariants.

- [x] **Step 1: Replace section 19's flat M0-M10 table**

Retitle the section to feature roadmap and acceptance gates. Add the identifier model, link to `ROADMAP.md`, include a compact F01-F12 feature catalog, and explain that only F01 milestones are currently decomposed. Do not duplicate the full F01 cards or live gate matrix.

- [x] **Step 2: Update testing and debug-console identifiers**

Change `M0`, `M1`, and `M5` workflow references in sections 20-21 to `F01.M01`, `F01.M02`, and `F07` as appropriate. Preserve the rules about full-stack verification, GameTest limits, debug authority, and evidence.

- [x] **Step 3: Update pending-decision gates and next actions**

Map settlement/gigai decisions to F05/F06, guild/war decisions to F08, capture to F04, Seireitei reset to F09, and performance/release to F12. Replace the old “repeat M1, then begin M2” next step with “complete F01.M01/F01.M02 acceptance, then specify F01.M03.”

- [x] **Step 4: Verify obsolete roadmap identifiers are confined to migration/history**

Run:

```bash
rg -n 'Before M[0-9]+|begin M[0-9]+|repeat the M[0-9]+|Create `MODLOG\.md` at M[0-9]+|M1 exposes' docs/Bleach_Fractured_World_Architecture.md
```

Expected: no matches.

### Task 3: Align workflow, evidence, and historical F01.M02 artifacts

**Files:**
- Modify: `MODDING_PLAN.md`
- Modify: `MODLOG.md`
- Modify: `docs/superpowers/specs/2026-10-05-m1-spiritual-identity-design.md`
- Modify: `docs/superpowers/plans/2026-10-05-m1-spiritual-identity.md`

**Interfaces:**
- Consumes: Canonical taxonomy and status from `docs/ROADMAP.md`.
- Produces: Consistent current terminology without destructive historical rewriting.

- [x] **Step 1: Update the modding plan's current slice and verification language**

Replace current-facing M1 references with F01.M02, link `docs/ROADMAP.md` as the status authority, and state that the next implementation milestone is F01.M03 after pending acceptance gates. Preserve the stable full-stack client/server profile rules.

- [x] **Step 2: Append a taxonomy migration entry to the development log**

Add a dated October 6, 2026 entry recording approval of F01-F12, the F01.M01-F01.M08 decomposition, the legacy mapping, current Implemented-but-not-Accepted status of F01.M01/F01.M02, and the fact that no runtime or gameplay implementation changed. Do not edit prior observations.

- [x] **Step 3: Annotate the historical M1 specification**

Immediately below its title, state that M1 is now canonically F01.M02 and link `docs/ROADMAP.md` plus the taxonomy design. Update forward-looking M2/M3 references in the non-goals section to F01.M03-F01.M07 while preserving the original scope.

- [x] **Step 4: Annotate the historical M1 implementation plan**

Immediately below its title, state that it implements F01.M02 and is retained at the old filename for stable historical links. Link the canonical roadmap and preserve its existing prohibition on agent Git operations.

- [x] **Step 5: Verify current and historical terminology**

Run:

```bash
rg -n "F01\.M0[1-8]|docs/ROADMAP\.md|historical|canonical" MODDING_PLAN.md MODLOG.md docs/superpowers/specs/2026-10-05-m1-spiritual-identity-design.md docs/superpowers/plans/2026-10-05-m1-spiritual-identity.md
```

Expected: each current document points to the canonical roadmap, and both old M1 artifacts identify themselves as F01.M02.

### Task 4: Perform cross-document static verification

**Files:**
- Verify: `docs/ROADMAP.md`
- Verify: `docs/Bleach_Fractured_World_Architecture.md`
- Verify: `MODDING_PLAN.md`
- Verify: `MODLOG.md`
- Verify: `docs/superpowers/specs/2026-10-05-m1-spiritual-identity-design.md`
- Verify: `docs/superpowers/plans/2026-10-05-m1-spiritual-identity.md`
- Verify: `docs/superpowers/specs/2026-10-06-feature-roadmap-taxonomy-design.md`

**Interfaces:**
- Consumes: Documentation edits from Tasks 1-3.
- Produces: Evidence that the taxonomy is complete and internally consistent without launching a game or invoking Git.

- [x] **Step 1: Check feature and milestone coverage**

Run:

```bash
for id in F01 F02 F03 F04 F05 F06 F07 F08 F09 F10 F11 F12; do rg -q "$id" docs/ROADMAP.md || exit 1; done
for id in F01.M01 F01.M02 F01.M03 F01.M04 F01.M05 F01.M06 F01.M07 F01.M08; do rg -q "$id" docs/ROADMAP.md || exit 1; done
```

Expected: exit status 0.

- [x] **Step 2: Check for placeholders and false acceptance claims**

Run:

```bash
rg -n "TBD|TODO|PLACEHOLDER" docs/ROADMAP.md docs/Bleach_Fractured_World_Architecture.md MODDING_PLAN.md
rg -n "F01\.M0[12].*Accepted|Accepted.*F01\.M0[12]" docs/ROADMAP.md MODDING_PLAN.md
```

Expected: neither command reports a match.

- [x] **Step 3: Check prohibited runtime-profile terminology**

Run:

```bash
rg -n "run/(m[0-9]+|f[0-9]+)-(client|server)" docs/ROADMAP.md docs/Bleach_Fractured_World_Architecture.md MODDING_PLAN.md
```

Expected: no matches.

- [x] **Step 4: Review the final documentation diff without Git**

Read every modified section with `sed` and compare it to the approved taxonomy specification. Confirm feature names, F01 milestone names/statuses, gate ownership, migration mappings, and next action agree exactly. Do not run `git diff`; Git operations are reserved for the user.

- [x] **Step 5: Report completion**

List every created/modified file, summarize the canonical current status, provide the next planned milestone (`F01.M03` after pending F01.M01/F01.M02 user gates), and state explicitly that no Git or Minecraft runtime operation was performed.
