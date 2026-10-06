# Bleach: Fractured World Roadmap

Updated October 6, 2026.

This is the canonical source for current feature scope, milestone status, acceptance gates, ownership, dependencies, and evidence references. The [master architecture](Bleach_Fractured_World_Architecture.md) defines intended behavior, while the [development log](../MODLOG.md) records dated observations and failures.

## Roadmap model

Large capabilities use stable feature identifiers `F01` through `F12`. Independently testable vertical slices inside a feature use local milestone identifiers such as `F01.M01`. Adding later work does not renumber accepted features or milestones.

Only F01 is decomposed into milestones in this first revision. F02-F12 remain feature-level scope until each is approached through its own approved specification and implementation plan.

### Status vocabulary

| Status | Meaning |
|---|---|
| Not started | Defined, with no implementation work begun |
| In progress | Design, code, or documentation work is active |
| Implemented | Intended implementation exists, but required acceptance gates remain open |
| Accepted | Every required gate has recorded passing evidence |
| Blocked | Progress requires a named external decision, dependency, or failure resolution |

### Gate vocabulary and ownership

| Gate | Meaning | Primary owner |
|---|---|---|
| Design | Scope, behavior, exclusions, and acceptance criteria are approved | User and agent |
| Implementation | Required code, data, and documentation exist | Agent |
| Automated | Compilation and applicable non-runtime/unit checks pass | Agent |
| Client | Player-facing behavior passes in the complete client stack | User |
| Server | Dedicated-server authority and persistence pass | User |
| Multiplayer | Multi-player synchronization, permissions, and abuse cases pass | User |
| Package | Built jar passes in the intended clean distribution profile | User |

Gate results are `Pending`, `Pass`, `Fail`, `Blocked`, or `Not required`. A milestone becomes Accepted only when every required gate is Pass. A build or automated check never substitutes for client, server, multiplayer, or package evidence.

The agent owns source changes, documentation, compilation, and non-game unit checks. The user exclusively owns launching and controlling Minecraft clients and servers, multiplayer sessions, process management, and in-game observations. The agent may inspect resulting logs and record user-reported observations.

Every normal acceptance pass uses the full dependency collection from `run/mods/`, mirrored into stable `run/client/` and `run/server/` profiles. Milestone-specific runtime profiles are prohibited.

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

Feature numbers express stable identity, not an inflexible execution order. Dependency readiness and milestone acceptance determine what can safely begin. F11 and F12 accumulate work and evidence across the project.

## Current status

| Milestone | Name | Status | Next gate |
|---|---|---|---|
| F01.M01 | Development and compatibility baseline | Implemented | User-run simultaneous full-stack client/server and connection pass |
| F01.M02 | Persistent spiritual identity and development console | Implemented | User-run complete-stack persistence and multiplayer pass |
| F01.M03 | Server-authoritative combat and Epic Fight integration | Not started | Approve milestone design |
| F01.M04 | Starter Shinigami combat kit | Not started | Complete F01.M03 |
| F01.M05 | Starter Quincy combat kit | Not started | Complete F01.M03 |
| F01.M06 | First Hollow encounter and reward loop | Not started | Complete both starter combat kits |
| F01.M07 | Shared ability and transformation lifecycle | Not started | Complete the core combat/reward loop |
| F01.M08 | Integrated multiplayer and packaged-build acceptance | Not started | Complete F01.M01-F01.M07 |

### F01 gate matrix

| Milestone | Design | Implementation | Automated | Client | Server | Multiplayer | Package |
|---|---|---|---|---|---|---|---|
| F01.M01 | Pass | Pass | Pass | Pending | Pending | Pending | Not required |
| F01.M02 | Pass | Pass | Pass | Pending | Pending | Pending | Not required |
| F01.M03 | Pending | Pending | Pending | Pending | Pending | Pending | Not required |
| F01.M04 | Pending | Pending | Pending | Pending | Pending | Pending | Not required |
| F01.M05 | Pending | Pending | Pending | Pending | Pending | Pending | Not required |
| F01.M06 | Pending | Pending | Pending | Pending | Pending | Pending | Not required |
| F01.M07 | Pending | Pending | Pending | Pending | Pending | Pending | Not required |
| F01.M08 | Pending | Pending | Pending | Pending | Pending | Pending | Pending |

Existing evidence for F01.M01 and F01.M02 is recorded under “M1 resumption audit,” “Restore the contiguous dependency profile,” and “Stable simultaneous client/server profiles” in the [development log](../MODLOG.md). Those entries establish implementation and automated evidence, but not the outstanding full-stack runtime gates.

## F01 — Core Spiritual Gameplay

F01 delivers the first complete gameplay loop: a player joins the full mod stack, selects and retains a spiritual identity, uses a server-authoritative starter combat kit, defeats a Hollow, receives exactly one reward, exercises one shared transformation lifecycle, and inspects test state through the development console. It must work on a dedicated server with multiple clients and as a packaged jar.

Feature-wide constraints:

- The server owns profiles, energy, combat outcomes, rewards, cooldowns, forms, and debug mutations.
- Clients send bounded intent and render authoritative state; they do not submit trusted damage, energy, identity, reward, or target-state values.
- Epic Fight integration remains behind an adapter rather than becoming the owner of spiritual gameplay rules.
- Every normal F01 acceptance pass uses all canonical development mods.
- Debug controls use ordinary gameplay services where practical and remain auditable and restrictable before release.
- Persistent schemas define safe defaults and migration behavior before changing.
- Player-facing denials explain why an action failed.

### F01.M01 — Development and compatibility baseline

**Status:** Implemented.

**Dependencies:** None.

**Playable result:** Bleach: Fractured World builds on the pinned Minecraft 1.21.1 and NeoForge toolchain. The complete dependency stack can run in simultaneous, separate client and dedicated-server profiles, and a client can join the private server.

**Scope:** Pinned toolchain, canonical `run/mods/` source, stable `run/client/` and `run/server/` profiles, mod synchronization, log locations, safe test-world policy, and client/server class-loading separation.

**Required gates:** Design, Implementation, Automated, Client, Server, Multiplayer.

**Acceptance evidence:** Successful build; exact dependency inventory; complete-stack client reaches a world; dedicated server reaches ready state; client connects; both processes coexist without mutable-file contention; dedicated server does not load client-only BWF classes.

**Ownership:** The agent maintains configuration and build evidence. The user launches both processes and verifies connection and coexistence.

**Next gate:** User-run simultaneous full-stack client/server and connection pass.

### F01.M02 — Persistent spiritual identity and development console

**Status:** Implemented.

**Dependencies:** F01.M01 implementation; final acceptance may be verified in the same combined runtime pass.

**Playable result:** A player selects Shinigami or Quincy, sees synchronized spiritual energy, and inspects or resets the profile through `BWF Debug` from the pause screen.

**Scope:** Persistent profile, immutable ordinary path selection, energy snapshot, revisioned networking, HUD, command route, debug access-policy seam, and bounded debug actions.

**Required gates:** Design, Implementation, Automated, Client, Server, Multiplayer.

**Acceptance evidence:** Invalid and repeated selections reject; debug requests remain server-authoritative; state reaches only the correct client; path and energy survive death, reconnect, and server restart; reset permits another test without corrupting revision order; complete-stack HUD and pause controls behave correctly.

**Ownership:** The agent maintains implementation and automated checks. The user performs client, dedicated-server, persistence, and multiplayer scenarios.

**Next gate:** User-run complete-stack persistence and multiplayer pass.

### F01.M03 — Server-authoritative combat and Epic Fight integration

**Status:** Not started.

**Dependencies:** F01.M01 and F01.M02 implemented; unresolved F01.M01/F01.M02 acceptance failures must be handled before F01.M03 acceptance.

**Playable result:** Spiritual actions coexist with Epic Fight movement and animation, and the debug console can establish repeatable combat-test states without becoming combat authority.

**Scope:** Narrow Epic Fight adapter, combat-action request envelope, cast/action identifiers, target validation, energy admission, server hit resolution, repeat-hit protection, interruption hooks, and diagnostics.

**Required gates:** Design, Implementation, Automated, Client, Server, Multiplayer.

**Acceptance evidence:** Epic Fight movement and ordinary attacks work in the full stack; forged damage and stale/duplicate actions reject; insufficient energy rejects; an accepted action consumes energy once; server and clients agree on start, interruption, hit, and completion; disconnect/death clears active actions.

**Ownership:** The agent designs and implements the adapter and authority rules. The user verifies animation, movement, feel, and multiplayer observation.

**Next gate:** Approve the F01.M03 milestone design.

### F01.M04 — Starter Shinigami combat kit

**Status:** Not started.

**Dependencies:** F01.M03.

**Playable result:** A Shinigami receives or summons one bound starter sword and performs one spiritual melee technique plus one movement or defensive technique.

**Scope:** Bound sword identity and ownership, equip/summon rules, two starter actions, energy costs, cooldowns, animation adapter calls, target filtering, and debug unlock/reset controls.

**Required gates:** Design, Implementation, Automated, Client, Server, Multiplayer.

**Acceptance evidence:** Another player cannot claim or duplicate the weapon; actions require the Shinigami path and correct weapon state; energy and cooldown charge once; protected/friendly targets reject; animation and movement remain usable; death, reconnect, and inventory transitions cannot duplicate or orphan the weapon.

**Ownership:** The agent implements items, rules, and checks. The user verifies controls, animations, feel, inventory behavior, and multiplayer ownership.

**Next gate:** Complete F01.M03, then approve the F01.M04 milestone design.

### F01.M05 — Starter Quincy combat kit

**Status:** Not started.

**Dependencies:** F01.M03.

**Playable result:** A Quincy forms one starter spirit weapon, fires one server-authoritative projectile technique, and uses one movement or defensive technique.

**Scope:** Spirit-weapon state, projectile request and spawn, server collision/damage, energy costs, cooldowns, animation adapter calls, target filtering, and debug unlock/reset controls.

**Required gates:** Design, Implementation, Automated, Client, Server, Multiplayer.

**Acceptance evidence:** Only an eligible Quincy can use the weapon; projectiles do not trust client-reported hits or damage; repeated collision cannot reward multiple hits; energy and cooldown charge once; protected/friendly targets reject; disconnect, dimension change, and death safely clear owned projectiles.

**Ownership:** The agent implements weapon, projectile, and authority rules. The user verifies controls, animation, collision behavior, and multiplayer observation.

**Next gate:** Complete F01.M03, then approve the F01.M05 milestone design.

### F01.M06 — First Hollow encounter and reward loop

**Status:** Not started.

**Dependencies:** F01.M04 and F01.M05.

**Playable result:** Either starter path can locate and defeat one placeholder Hollow in a repeatable encounter and receive progression credit exactly once.

**Scope:** One Hollow archetype, bounded spawn/test command, hostile targeting, combat integration, encounter identity, contribution tracking, completion record, one reward, and debug cleanup/restart controls.

**Required gates:** Design, Implementation, Automated, Client, Server, Multiplayer.

**Acceptance evidence:** The Hollow works with both kits; protected areas can deny spawning; only eligible contributors receive credit; replayed completion cannot duplicate rewards; remote/non-contributors receive nothing; cleanup survives death, unload, disconnect, and restart.

**Ownership:** The agent implements encounter and reward authority. The user verifies encounter behavior, multiplayer contribution, and persistence.

**Next gate:** Complete both starter combat kits, then approve the F01.M06 milestone design.

### F01.M07 — Shared ability and transformation lifecycle

**Status:** Not started.

**Dependencies:** F01.M03-F01.M06.

**Playable result:** A Shinigami demonstrates one initial release progression sequence and a Quincy demonstrates one initial form through the same admission, upkeep, interruption, cooldown, and recovery contract.

**Scope:** Shared ability phases, persistent cooldowns, form modifiers, energy upkeep, interruption, forced teardown, reconnect/death recovery, one Shinigami release slice, and one Quincy form slice. Deeper content belongs to F02 and F03.

**Required gates:** Design, Implementation, Automated, Client, Server, Multiplayer.

**Acceptance evidence:** Repeated activation cannot stack modifiers; insufficient energy and active cooldown reject; upkeep drains only while active; interruption/death removes modifiers; logout/restart cannot bypass cooldown or retain a ghost form; observers receive consistent state; debug controls use the same lifecycle boundaries.

**Ownership:** The agent implements the lifecycle and representative forms. The user verifies presentation, feel, synchronization, interruption, and persistence.

**Next gate:** Complete the core combat/reward loop, then approve the F01.M07 milestone design.

### F01.M08 — Integrated multiplayer and packaged-build acceptance

**Status:** Not started.

**Dependencies:** F01.M01-F01.M07.

**Playable result:** Two players complete the F01 loop on a dedicated server with the packaged mod and full stack: choose different paths, use starter kits, defeat a Hollow, receive valid rewards, exercise forms, disconnect, and return with correct state.

**Scope:** F01 regression suite, cross-path interaction, multiplayer synchronization, persistence rehearsal, packaged installation, debug-access release posture, compatibility observations, and operator test instructions.

**Required gates:** Design, Implementation, Automated, Client, Server, Multiplayer, Package.

**Acceptance evidence:** Earlier F01 gates remain passing; two clients complete the scenario without state leakage; friendly/protected-target policy holds; restart preserves durable and clears transient state; artifact hash and dependency inventory are recorded; third-party diagnostics are classified; debug access cannot accidentally become an unrestricted public default.

**Ownership:** The agent prepares the build, diagnostics, instructions, and non-runtime checks. The user runs the packaged client/server and multiplayer acceptance scenario.

**Next gate:** Complete and retain passing evidence for F01.M01-F01.M07.

## Legacy milestone mapping

Historical references remain valid records. New work uses these canonical destinations:

| Former milestone | Canonical destination |
|---|---|
| M0 — Baseline | F01.M01 |
| M1 — Spiritual identity | F01.M02 |
| M2 — Combat loop | Split across F01.M03-F01.M06 |
| M3 — Release loop | F01.M07 for the shared first slice; deeper progression moves to F02 and F03 |
| M4 — Settlement loop | F05 and F06 |
| M5 — Realms and travel | F07 |
| M6 — Guild conflict | F08 |
| M7 — Living world | F06 and F10 |
| M8 — Seireitei cycle | F09 |
| M9 — Expansion | F02, F03, and F04 |
| M10 — Release candidate | F11 and F12 |

Existing M1 filenames are retained for stable links and annotated as F01.M02 artifacts. Historical development-log text is not rewritten.

## Updating progress

For every material gate result:

1. Append the date, exact jar/version configuration, profile/world, scenario, expected result, observed result, logs, and failure/resolution details to `MODLOG.md`.
2. Update the corresponding gate cell in this document.
3. Update the milestone's status and next gate when justified by the complete gate set.
4. Never erase a failed observation; record the later passing rerun separately.
5. Mark Accepted only when all required gates are Pass.

Specs decide behavior before implementation. Plans decide the implementation tasks. Neither artifact proves a gate passed.
