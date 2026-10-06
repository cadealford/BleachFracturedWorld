# Feature Roadmap Taxonomy Design

## Goal

Replace the flat M0-M10 delivery list with a durable feature-and-milestone roadmap. Large gameplay capabilities use feature identifiers `F01` through `F12`; independently testable increments inside a feature use identifiers such as `F01.M01`. This first roadmap revision fully defines F01 and names the remaining planned features without prematurely fixing all of their milestones.

## Roadmap principles

- A feature is a large, player-facing capability or a release-wide operational concern.
- A milestone is the smallest useful vertical slice that produces a testable result within one feature.
- Feature numbers remain stable. New scope is added as a milestone or a later feature rather than renumbering accepted work.
- Milestone numbers are local to their feature and use two digits: `F01.M01`, `F01.M02`, and so on.
- Cross-cutting requirements such as server authority, persistence, compatibility, recovery, and performance remain acceptance requirements instead of becoming disconnected technical features.
- A successful build is implementation evidence, not gameplay acceptance.
- Every normal client and server acceptance pass uses the complete dependency stack sourced from `run/mods/` and the stable `run/client/` and `run/server/` profiles.
- No milestone-specific runtime profile is permitted.

## Canonical documents

The project documentation has distinct responsibilities:

| Document | Responsibility |
|---|---|
| `docs/ROADMAP.md` | Canonical feature catalog, current milestone status, gates, ownership, dependencies, and evidence links |
| `docs/Bleach_Fractured_World_Architecture.md` | Intended game behavior, system boundaries, invariants, and technical constraints |
| `docs/superpowers/specs/` | Approved behavior and acceptance requirements for an individual feature or milestone |
| `docs/superpowers/plans/` | Implementation tasks derived from an approved specification |
| `MODLOG.md` | Dated observed evidence, failures, environment details, and resolved causes |
| `MODDING_PLAN.md` | Toolchain, runtime layout, safety boundaries, and development workflow |

`docs/ROADMAP.md` is the status authority. It may link to evidence in `MODLOG.md`, but it must not duplicate detailed logs. Architecture describes what should be true; only recorded evidence can move a milestone to Accepted.

## Status and gate model

Each milestone has exactly one status:

| Status | Meaning |
|---|---|
| Not started | Defined but no implementation work has begun |
| In progress | Design, code, or documentation work is active |
| Implemented | Intended implementation exists, but one or more required acceptance gates remain open |
| Accepted | Every required gate has recorded passing evidence |
| Blocked | Progress requires a named external decision, dependency, or failure resolution |

Each milestone declares which of these gates are required:

| Gate | Meaning | Primary owner |
|---|---|---|
| Design | Scope, behavior, exclusions, and acceptance criteria are approved | User and agent |
| Implementation | Required code, data, and documentation exist | Agent |
| Automated | Compilation and applicable non-runtime/unit checks pass | Agent |
| Client | Player-facing behavior passes in the complete client stack | User |
| Server | Dedicated-server authority and persistence pass | User |
| Multiplayer | Multiple-player synchronization, permissions, and abuse cases pass | User |
| Package | Built jar passes in the intended clean distribution profile | User |

A gate result is Pending, Pass, Fail, Blocked, or Not required. A failure never disappears: its cause and later passing rerun are recorded in `MODLOG.md`. The roadmap stores the current result and links to the dated evidence.

The agent owns source changes, documentation, compilation, and non-game unit checks. The user exclusively owns launching or controlling Minecraft clients and servers, multiplayer sessions, process management, and in-game observations. The agent may inspect user-provided logs and update gate records from those observations.

## Feature catalog

| Feature | Name | Scope | Principal dependencies |
|---|---|---|---|
| F01 | Core Spiritual Gameplay | Runtime baseline, persistent identity, debug tooling, combat integration, starter Shinigami and Quincy mechanics, first Hollow encounter, shared ability lifecycle, and integrated acceptance | None |
| F02 | Shinigami Journey | Zanpakuto bond, sword spirit, Inner World training, Shikai, Bankai, duties, and long-form progression | F01 |
| F03 | Quincy Journey | Spirit weapons, techniques, resource progression, forms, faction identity, and advanced Quincy play | F01 |
| F04 | Hollow, Arrancar, and Visored | Hollow evolution, Arrancar releases, Visored masks, capture interactions, and alternate progression | F01; portions of F02 and F03 where powers interact |
| F05 | Bodies, Death, and Gigai | Body/spirit separation, death rules, gigai ownership, canonical inventory custody, and recovery | F01 |
| F06 | Settlements and Living Society | Colonies, protectors, governance, prosperity, petitions, guards, visitors, and social persistence | F01 and F05 |
| F07 | Realms and Travel | Soul Society, Hueco Mundo, Dangai, Inner Worlds, anchors, portals, admission, and traveller recovery | F01; F02 for Inner World progression |
| F08 | Guilds, War, and Destruction | Guild authority, relations, war windows, objectives, economy integration, bounded terrain damage, and restoration ownership | F01 and F06 |
| F09 | Seireitei Cycle | Sanctuary, training, authorized infiltration, objective extraction, faction consequences, and resumable city restoration | F07 and F08 |
| F10 | Encounters, Quests, and Companions | Encounter direction, missions, bosses, contribution rewards, followers, travel reconciliation, and permanent-death handling | F01; integrates with F06 and F07 |
| F11 | Presentation and Content | HUD evolution, spiritual sensing, animation, effects, sound, accessibility, asset provenance, and content expansion | Advances alongside F01-F10 |
| F12 | Operations and Release | Performance budgets, configuration, compatibility, migration, backup/restore, packaging, and operator guidance | Advances alongside all features; closes the release |

The catalog is a scope map, not a promise that features execute strictly in numeric order. Dependency gates determine when work can safely begin. F11 and F12 are intentionally cross-cutting and accumulate evidence throughout development.

## F01 — Core Spiritual Gameplay

### Feature outcome

F01 delivers the first complete, testable gameplay loop: a player joins the full mod stack, selects and retains a spiritual identity, uses a server-authoritative starter combat kit, defeats a Hollow, receives exactly one reward, exercises one shared transformation lifecycle, and can inspect test state through the development console. The same code must work on a dedicated server with multiple clients and as a packaged jar.

### Feature-wide constraints

- The server owns profiles, energy, combat outcomes, rewards, cooldowns, forms, and debug mutations.
- Clients send bounded intent and render authoritative state; they do not submit trusted damage, energy, identity, reward, or target-state values.
- Epic Fight integration is isolated behind an adapter so its implementation can change without rewriting spiritual gameplay services.
- Every F01 milestone is tested with all canonical development mods active.
- Debug controls use ordinary gameplay services where possible and remain separately identifiable, auditable, and removable or restrictable before release.
- Persistent schemas require explicit defaults and migration behavior before they change.
- Player-facing denial paths explain why an action failed.

### F01.M01 — Development and compatibility baseline

**Playable result:** Bleach: Fractured World builds on the pinned Minecraft 1.21.1 and NeoForge toolchain. The complete dependency stack can run in simultaneous, separate client and dedicated-server profiles, and a client can join the private server.

**Scope:** Pinned toolchain, canonical `run/mods/` dependency source, stable `run/client/` and `run/server/` profiles, mod synchronization tasks, log locations, safe test-world policy, and client/server class-loading separation.

**Required gates:** Design, Implementation, Automated, Client, Server, Multiplayer.

**Acceptance evidence:** Successful build; exact dependency inventory; complete-stack client reaches a world; dedicated server reaches ready state; client connects; both processes coexist without log/world/config contention; dedicated server does not load client-only BWF classes.

**Current status:** Implemented. Automated configuration/build evidence exists, while the final stable-profile client, server, and multiplayer acceptance pass remains pending.

### F01.M02 — Persistent spiritual identity and development console

**Playable result:** A player selects Shinigami or Quincy, sees synchronized spiritual energy, and inspects or resets the profile through `BWF Debug` from the pause screen.

**Scope:** Persistent player profile, immutable path selection outside explicit debug reset, spiritual energy snapshot, revisioned networking, HUD, command route, debug access-policy seam, and bounded debug actions.

**Required gates:** Design, Implementation, Automated, Client, Server, Multiplayer.

**Acceptance evidence:** Invalid and repeated selection requests reject; debug requests resolve the sending player and remain server-authoritative; state synchronizes to the correct client; path and energy survive death, reconnect, and server restart; reset enables another test without corrupting revision order; complete-stack HUD and pause-screen controls behave correctly.

**Current status:** Implemented. Earlier automated and initial manual evidence exists, but complete-stack persistence and multiplayer acceptance remains pending.

### F01.M03 — Server-authoritative combat and Epic Fight integration

**Playable result:** Spiritual actions coexist with Epic Fight movement and animation, and the development console can place the player into repeatable combat-test states without becoming the combat authority.

**Scope:** Narrow Epic Fight adapter, combat action request envelope, cast/action identifiers, target validation, energy admission, server-side hit resolution, repeat-hit protection, interruption hooks, and combat diagnostics.

**Required gates:** Design, Implementation, Automated, Client, Server, Multiplayer.

**Acceptance evidence:** Epic Fight movement and ordinary attacks still work with the complete stack; forged damage and stale/duplicate actions reject; insufficient energy rejects; one accepted action consumes energy once; server and clients agree on action start, interruption, hit, and completion; disconnect/death cleans up active actions.

**Current status:** Not started.

### F01.M04 — Starter Shinigami combat kit

**Playable result:** A Shinigami receives or summons one bound starter sword and can perform one spiritual melee technique plus one movement/defensive technique.

**Scope:** Bound sword identity and ownership, equip/summon rules, two starter actions, energy costs, cooldowns, animation adapter calls, target filtering, and debug unlock/reset controls.

**Required gates:** Design, Implementation, Automated, Client, Server, Multiplayer.

**Acceptance evidence:** Another player cannot claim or duplicate the bound weapon; actions require the Shinigami path and correct weapon state; energy and cooldown charge once; protected/friendly targets are rejected; animation and movement remain usable; death, reconnect, and inventory transitions cannot duplicate or orphan the weapon.

**Current status:** Not started.

### F01.M05 — Starter Quincy combat kit

**Playable result:** A Quincy forms one starter spirit weapon, fires one server-authoritative projectile technique, and uses one movement/defensive technique.

**Scope:** Spirit-weapon state, projectile request and spawn, server collision/damage, energy costs, cooldowns, animation adapter calls, target filtering, and debug unlock/reset controls.

**Required gates:** Design, Implementation, Automated, Client, Server, Multiplayer.

**Acceptance evidence:** Only an eligible Quincy can form and use the weapon; projectiles cannot trust client-reported hits or damage; repeated collision cannot reward multiple hits; energy and cooldown charge once; protected/friendly targets are rejected; disconnect, dimension change, and death clean up owned projectiles safely.

**Current status:** Not started.

### F01.M06 — First Hollow encounter and reward loop

**Playable result:** Either starter path can locate and defeat one placeholder Hollow in a repeatable encounter and receive progression credit exactly once.

**Scope:** One Hollow archetype, bounded spawn/test command, hostile targeting, health and damage integration, encounter identity, contribution tracking, completion record, one reward, and debug cleanup/restart controls.

**Required gates:** Design, Implementation, Automated, Client, Server, Multiplayer.

**Acceptance evidence:** The Hollow behaves with both combat kits; protected areas can deny spawning; participants receive only authorized credit; replayed death/completion events do not duplicate rewards; remote or non-contributing players do not receive credit; encounter cleanup survives death, unload, disconnect, and restart.

**Current status:** Not started.

### F01.M07 — Shared ability and transformation lifecycle

**Playable result:** A Shinigami can demonstrate one initial release progression sequence and a Quincy can demonstrate one initial form using the same lifecycle contract for admission, upkeep, interruption, cooldown, and recovery.

**Scope:** Shared ability phases, persistent cooldown semantics, form modifiers, energy upkeep, interruption, forced teardown, reconnect/death recovery, one Shinigami release slice, and one Quincy form slice. Deeper path content belongs to F02 and F03.

**Required gates:** Design, Implementation, Automated, Client, Server, Multiplayer.

**Acceptance evidence:** Repeated activation cannot stack modifiers; insufficient energy and active cooldown reject; upkeep drains only while active; interruption and death remove modifiers; logout/restart cannot bypass cooldown or retain a ghost form; observers receive consistent form state; debug controls invoke the same lifecycle boundaries.

**Current status:** Not started.

### F01.M08 — Integrated multiplayer and packaged-build acceptance

**Playable result:** Two players can complete the F01 loop on a dedicated server using the packaged mod and complete dependency stack: choose different paths, use their starter kits, defeat a Hollow, receive valid rewards, exercise forms, disconnect, and return with correct state.

**Scope:** F01 regression suite, cross-path interaction, multiplayer synchronization, persistence rehearsal, packaged-jar installation, debug-access release posture, compatibility observations, and operator-facing test instructions.

**Required gates:** Design, Implementation, Automated, Client, Server, Multiplayer, Package.

**Acceptance evidence:** All prior F01 gates remain passing on the pinned stack; two clients complete the scenario without state leakage; friendly/protected-target policy holds; restart preserves durable state and clears transient state; packaged artifact hash and dependency inventory are recorded; known third-party diagnostics are classified; debug access is explicitly configured for the private development build and cannot accidentally become an unrestricted public-release default.

**Current status:** Not started.

## Migration from the flat milestone list

Existing references remain historically valid but gain canonical replacements:

| Former milestone | Canonical destination |
|---|---|
| M0 — Baseline | F01.M01 |
| M1 — Spiritual identity | F01.M02 |
| M2 — Combat loop | Split across F01.M03 through F01.M06 |
| M3 — Release loop | F01.M07 for the shared first slice; deeper progression moves to F02 and F03 |
| M4 — Settlement loop | F05 and F06 |
| M5 — Realms and travel | F07 |
| M6 — Guild conflict | F08 |
| M7 — Living world | F06 and F10 |
| M8 — Seireitei cycle | F09 |
| M9 — Expansion | F02, F03, and F04 |
| M10 — Release candidate | F11 and F12 |

Historical `M1` headings and filenames do not need destructive renaming. Their first paragraph will identify them as F01.M02 artifacts, and new references will use the canonical identifier. The same rule applies to dated MODLOG entries: preserve the record and add a migration note instead of rewriting history.

## Documentation implementation

After this design is approved:

1. Create `docs/ROADMAP.md` with the feature catalog, F01 milestone cards, current status dashboard, gate matrix, ownership rules, dependencies, and evidence conventions.
2. Replace the flat delivery table in architecture section 19 with the F01-F12 catalog, the identifier rules, and a link to the canonical roadmap. Keep architectural acceptance invariants in sections 20-22.
3. Update architecture pending-decision references from former milestone numbers to feature or milestone identifiers.
4. Update `MODDING_PLAN.md` to name F01.M02 as the current vertical slice and point to the roadmap as the status authority.
5. Add a dated, non-destructive taxonomy migration entry to `MODLOG.md`; do not rewrite its historical M1 observations.
6. Annotate the existing M1 specification and plan as historical F01.M02 artifacts. Preserve filenames so existing links remain valid.
7. Do not alter product code, Gradle configuration, runtime profiles, saves, worlds, or dependency jars.

## Non-goals

- This revision does not fully define milestones for F02-F12.
- It does not estimate calendar dates or promise a release order independent of dependencies.
- It does not claim F01.M01 or F01.M02 are accepted before the missing user-run gates pass.
- It does not launch Minecraft, run a server, control a client, modify a save, or perform Git operations.
- It does not change gameplay implementation.
