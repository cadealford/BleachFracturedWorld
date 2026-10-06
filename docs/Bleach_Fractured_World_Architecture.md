# Bleach: Fractured World — Master Design and Architecture

Version 1.0 · October 4, 2026 · Design baseline for review

This document consolidates the available project conversations, the local BleachCraft architectural investigation, and the current repository baseline. It defines the intended game and its technical boundaries. It is not a claim that these systems already exist. Earlier downloadable master-plan files were not available in this workspace; this consolidation uses the accessible conversations and evidence listed in section 22.

## 1. Vision and success criteria

Build a multiplayer Minecraft world where animated Bleach combat, personal progression, living settlements, and player-run guild politics reinforce each other. The signature moment is a colony alarm, allies arriving through a portal, and an enemy captain releasing Bankai: everyone changes their plans because the battlefield and objectives genuinely change.

The setting is an alternate future after the original cast. A united spiritual government fractured when Squad Zero attempted to become gods by consuming souls and dismantling the Soul King. The rebellion caused Earth and Soul Society to collide; the Arrancar King's sacrifice stabilized the remnants. Shinigami squads, Quincy houses, and Arrancar hosts now compete to rebuild and govern them. This lore supports contested Overworld settlements and distinct realm refuges without forcing every diplomatic relationship along faction lines.

Success means:

- Ordinary swordplay, ranged combat, guard, dodge, and movement feel useful before advanced releases.
- Power, reserves, mastery, player execution, and organization each matter.
- Strong players are dangerous; lower-powered players can still interrupt, escort, scout, defend, and complete objectives.
- Colonies produce real supplies, require governance, and create stakes beyond kill counts.
- Destructive abilities change terrain while respecting explicit server rules and performance limits.
- Death, reconnects, realm travel, and server restarts preserve progression and resolve temporary states safely.
- Each development milestone produces something playable and repeatably verifiable.

## 2. Confirmed baseline and assumptions

Read-only inspection of the actual repository at `C:/Users/squid/Software dev/BleachFracturedWorld` established these settings:

| Setting | Baseline |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.252 |
| Java target | 21 |
| ModDevGradle | 2.0.148 |
| Parchment | Minecraft 1.21.1 / mappings 2024.11.17 |
| Mod ID | `bleachfracturedworld` |
| Java package | `io.github.cadealford.bleachfracturedworld` |
| Mod version | 0.1.0 |
| Current license setting | All Rights Reserved; preserve until deliberately changed |

The renamed entry points exist, but the code inspected still contains template content. The inspected build dependency block does not yet declare the planned gameplay mods. A prior user report confirms the starter development client entered a world; this design pass did not build or launch it or prove dedicated-server operation.

Assumptions: Java Edition, a privately operated multiplayer server, placeholder assets for early slices, and one primary custom mod. Exact balance numbers, rare-release distribution, progression rates, and public-server capacity remain tuning decisions. Proposed defaults below are recommendations, not previously approved numerical rules.

## 3. Architecture choice

Use one modular NeoForge mod, with plain Java services for gameplay rules and narrow adapters for external mods. Grow packages when their feature is implemented; do not scaffold every future subsystem now.

| Approach | Tradeoff | Decision |
|---|---|---|
| One mod with explicit subsystem boundaries | Simple builds and releases; requires disciplined ownership | Recommended |
| Many independent custom mods | Separate releases but more versioning and integration work | Defer until an actual distribution need appears |
| Broad forks of combat, colony, and portal mods | Maximum control but continual upstream maintenance | Use only for a demonstrated missing hook after smaller alternatives fail |

Conceptual structure under the existing base package:

```text
player/       identity, spiritual attributes, persistence
progression/  trials, rewards, unlocks, mastery
combat/       admission, cast lifecycle, targeting, damage policy
ability/      definitions and original execution handlers
zanpakuto/    binding, spirits, release roster
form/         transformations, capture leases, cleanup
social/       guilds, parties, relations, roles
colony/       patronage, satisfaction, protector contracts
war/          admission, objectives, outcome coordination
world/        realms, travel, arenas, encounters, restoration
companion/    recruitment, ownership, orders, recovery
network/      bounded requests and authoritative updates
client/       input, screens, HUD, animations, visual cues
debug/        development-only inspection, controlled mutations, access policy
integration/  external combat, colony, war, portal, VFX adapters
```

Dependencies flow inward: external adapters translate into our domain contracts. Ability handlers call shared combat, relation, and world policies rather than external-mod internals. Common/server classes never depend on client render classes. Prefer public APIs and events; isolate any unavoidable mixin and document its exact version dependency.

## 4. Dependency responsibilities

The modpack supplies existing foundations; our mod supplies Bleach rules and connections. Availability of a release does not prove compatibility with the combined stack.

| Foundation | Intended responsibility | Integration boundary |
|---|---|---|
| Epic Fight | Animated melee, stamina, guard, dodge, combat timing | `CombatAdapter`: action state, weapon style, animation and hit integration |
| MineColonies | Colony records, citizens, workers, construction, production, guards | `ColonyAdapter`: stable colony key, permissions, metrics and supported mutations |
| War 'N Taxes | Existing economy, war and siege machinery where compatible | `WarAdapter`: lifecycle, eligibility, treasury and outcome events |
| Immersive Portals candidate | Visible cross-realm openings and optional spatial effects | `PortalBackend`: rendering and traversal callbacks for mod-owned routes |
| Weapons of Miracles, Nightfall, Sword Soaring candidates | Additional styles, animation or flying-weapon inspiration/integration | Optional content adapters after exact-version and license checks |
| Animation/VFX libraries | Models, trails, emitters, sound and presentation | Optional client adapters; never damage authority |

Choose and pin exact artifacts with loader, Minecraft version, required transitive dependencies, source URL, hash, and client/server placement. Add one dependency at a time and verify startup, dedicated server, combat, and reconnect. Do not silently change the base game version to accommodate an optional add-on. Compatible APIs, permissions, and redistribution terms must be checked before copying code or assets.

Each adapter reports its capabilities. If an essential hook is absent, disable that integrated feature with a clear diagnostic or keep it in a separate test profile. A missing visual backend can use vanilla particles or ordinary teleportation; a missing authorization hook must not silently permit a siege.

## 5. State ownership and persistence

The server owns progression, costs, damage, ownership, war outcomes, travel and rewards. Clients request actions and display accepted state.

| State | Owner and storage | Examples |
|---|---|---|
| Durable player profile | Serialized NeoForge player attachment | Path, power, maximum reserves, mastery, unlocks, sword identity, trial progress |
| Durable item identity | Vanilla item data components | Sword binding ID, owner reference, release definition ID, medallion token |
| Durable world records | Versioned SavedData in a designated shared store, normally Overworld | Guilds, colony links, route anchors, arena allocations, capture leases, companion identity, restoration jobs |
| External colony/economy records | Owning dependency | Citizen jobs, building state, colony treasury if dependency-owned |
| Temporary server sessions | Runtime state with explicit teardown | Active casts, charging, form modifiers, hit sets, temporary chunk tickets |
| Client presentation | Client caches rebuilt from sync | HUD, trails, sound loops, cinematic state |

Use schema versions, stable namespaced IDs, validation, and explicit migrations. Preserve durable progression on death; apply a deliberate respawn rule to current energy. Never copy an active cast, form modifier, or duplicate inventory during player cloning. Guild membership has one authoritative guild record; a player field is a reference/cache, not a second membership ledger.

Major release cooldowns use persisted expiry against a persisted monotonic server simulation clock. They advance while the server runs, including when the player is offline; they pause while the server is stopped. This proposed rule prevents reconnect resets without relying on workstation clock changes. UTC deadlines are reserved for calendar events such as restoration schedules.

SavedData and player files are not an atomic database transaction. Ordinary changes rely on Minecraft saves. Cross-record operations with duplication risk use operation IDs and reconciliation, and explicitly force durable checkpoints where supported. Do not promise arbitrary crash-proof rewards merely because a field is serialized. Back up before schema changes and refuse unsupported newer schemas without overwriting them.

## 6. Spiritual progression and balance

Three independent axes define the character:

- **Spiritual power:** output intensity and the permitted scale of certain abilities.
- **Spiritual reserves:** capacity and endurance; current energy is a consumable pool.
- **Mastery:** earned techniques, efficiency, control and transformations unlocked through trials.

Epic Fight stamina remains separate: spiritual reserves do not remove dodge or guard commitment. Rank is a readable progression label, not a single number that determines every combat outcome. Growth rewards meaningful encounters, defense, expeditions and validated training; repeated low-risk farming and cooperative kill trading receive diminishing credit.

Define separate, bounded curves for entity damage, radius, terrain volume and upkeep. Scaling should preserve an ability's identity and counterplay. Larger radius is not free: it increases energy and maintenance demand, while computational budgets remain hard limits. Form activation, ongoing drain, powerful moves and post-release cooldown all matter. Do not multiply every statistic together into uncontrolled exponential growth.

A balance data sheet for each technique records tell, windup, active duration, recovery, range, cost, upkeep, cooldown, interrupt conditions, target policy and destruction category. A weaker player earns contribution through timing and objectives, without automatic immunity to stronger attacks. Exceptional releases remain frightening but have visible commitments and exploitable resource limits.

## 7. Paths and transformations

| Path | Identity | Planned systems |
|---|---|---|
| Shinigami | Personal sword and disciplined techniques | Zanpakuto, Shikai, Bankai, Shunpo, optional Kido and hand-to-hand branches |
| Quincy | Spiritual weapons, gathering and control | Ranged/melee weapons, movement, mutually constrained offensive/defensive Blut, Vollstandig, later capture |
| Hollow/Arrancar | Evolution and expressed creature identity | Hollow trials/evolution, Cero, Sonido, Hierro, humanoid Arrancar path, Resurreccion |
| Visored extension | Shinigami with controlled Hollow influence | Separate unlock, mask lifecycle, mastery and instability rules; later content |

Launch the initial playable slice with Shinigami and Quincy; preserve Arrancar and Visored in the architecture and add them after shared systems work. Path changes are controlled progression/admin operations, not client-selected mutations during combat.

Use a transformation coordinator with typed transitions: base, releasing, active, recovering, and temporary sealed states where applicable. Define legal combinations explicitly; Visored compatibility cannot emerge accidentally from independent booleans. Forms derive stats and loadouts rather than permanently rewriting base attributes. Stable modifier IDs and idempotent teardown prevent repeated activations stacking bonuses.

Energy depletion, death, disconnect, incompatible travel, binding invalidation and server shutdown all end or reconcile the form. Practice sessions may temporarily override cost rules, but never bypass them in ordinary play. Gameplay uses the same coordinator for player and captured releases.

## 8. Zanpakuto, spirits and Inner Worlds

Bind a sword identity to its owner and release family. Physical item components point to the identity; the server binding record determines eligibility. Replacing a lost sword must invalidate prior copies or make duplicate copies unusable. Never trust editable display names as ownership.

The progression loop is acquisition, spirit encounter, Shikai trial, practice/mastery, Bankai trial, and refinement. Use authored release families and spirit/theme templates with optional personal cosmetic choices. Fully procedural movesets and arbitrary generated powers are deferred; they are difficult to balance and verify.

The roster mixes technical dueling, defense, formations, control and rare catastrophic releases. Earlier illustrative names such as White Silence, Pale Court and Sunless Emperor are concepts, not a locked implementation roster. Rare output should not require every new player to accept a permanently useless roll: acquisition and reassignment policy must be deliberately reviewed before release.

Allocate private arena cells in one Inner World dimension. Persist owner-to-cell mapping and template version; shared themes do not imply shared coordinates. A session contains owner, explicit invited party/spectator IDs, trial ID, attempt ID, return anchors and cleanup status. Bound cell dimensions and interactions so neighboring arenas cannot affect one another. Admission is server-checked at entry and during the session.

Trials use an isolated test inventory/state overlay or restricted ordinary loadout, with exactly one chosen reward path. Death, disconnect, restart and unavailable return dimensions resolve through recovery records and a safe fallback. Clean/reset a cell before reusing it. Trial completion is granted once per validated attempt, never by a client completion packet.

## 9. Ability and network contract

Keep ability metadata data-driven, but execution in original typed handlers. A definition contains stable ID, family, required path/mastery/form, cost policy, cooldown group, targeting rules, action commitments, effect ID and terrain category. Unknown handlers or invalid bounds reject the definition. No arbitrary scripts or expressions from clients.

Proposed request and response contracts:

| Message | Contents | Server rule |
|---|---|---|
| `AbilityIntent` | Slot, press/release action, sequence, loadout revision, bounded aim | Resolve the actual ability from server loadout |
| `CancelCastIntent` | Cast ID and request sequence | Only cancel the sender's own cancellable cast |
| `CastResult` | Sequence, accepted/denied, reason, cast ID, state revision | Owner-facing authoritative outcome |
| `CastCue` | Cast ID, effect ID, phase, position, dimension, presentation parameters | Send only to relevant tracking clients |
| `ProfileSnapshot/Delta` | Revision and permitted profile fields | Owner gets private data; observers get only necessary public state |

Reject invalid slots, stale loadouts, unknown actions, invalid/nonfinite aim, excessive request rate, illegal forms, insufficient energy, restricted regions, and cooldown violations. The network sender supplies identity; never accept player UUID, damage, resource totals or reward amounts from the client. Check range, line of sight, loaded targets and same dimension at execution. Revalidate long channels at relevant checkpoints.

Cast flow:

```text
intent -> server validation -> cost reservation -> windup
       -> commit -> active/channel -> recovery -> complete
                         \-> cancellation -> cleanup
```

On the server thread, reserve resources and action occupancy together. At commit, consume the reservation and start the specified cooldown. Definitions select whether interruption before commit releases the reservation and imposes a short interruption cooldown; after commit there is no automatic refund. Duplicate request sequences return the existing result within a bounded session history and cannot charge or execute twice. Disconnect ends the request session, but persisted cooldowns remain.

Client anticipation may show a windup; it cannot award a hit. The CombatAdapter defines which Epic Fight attack events supply damage so our handler does not also apply a second hit. Beams, fields and projectiles share hit bookkeeping with a declared repeat-hit interval. All harmful effects, including delayed fire and owned summons, retain responsible owner and cast/war context and pass through the common relationship/protection policy.

Use directional NeoForge payloads, bounded stream codecs and protocol version checks. Handle game mutations on the appropriate server thread. Private states such as sealed release ownership or unrevealed sensing information are not broadcast indiscriminately.

## 10. Quincy Bankai capture

Implement later as a channel with visible connection, limited range, interruption windows and teammate counterplay. Capture targets an eligible active release, not an arbitrary inventory copy. The captured form uses the current wielder's power and reserves within its definition's caps; it does not copy the owner's attributes.

A world-owned lease is authoritative: lease ID, sword identity, original owner, captor, definition version, expiry, status and operation ID. A medallion stores a reference only. The lease state machine is available, capturing, captured, returning, returned/expired. Enforce one live lease per sword and bounded leases per captor.

Capture seals the original release temporarily. It does not remove mastery, erase the sword, or permanently lock the character. Expiry, successful recovery objectives and administrative reconciliation restore access. Item copying, drop/pickup, death, offline owners and restart cannot create another valid lease. A terminal lease invalidates every stale medallion reference. Reconcile profile seals from the lease table on login and startup; multi-file saves cannot be assumed atomic.

Exact duration, eligible releases, success thresholds, and medallion loss policy remain tunable. Do not enable capture until interruptions, expiry and restart recovery pass tests.

## 11. Factions, guilds, parties and relations

Faction defines progression identity and sanctuary rules. A player-run guild defines political membership, territory obligations and diplomacy. A party defines a temporary encounter/travel team. Keep all three separate.

Guild records contain immutable ID, name, faction affiliation, leader, roles, membership, colony links and agreements. Roles cover leadership, officer, governor, protector and ordinary member with explicit permissions. Proposed default: one political guild per player; temporary cross-guild parties are allowed. Cross-faction alliances and contracts are supported; mixed-faction guild membership remains a configurable policy.

Relations resolve effective actor ownership before decisions: player, projectile owner, companion owner or summon owner. Permissions for friendly fire, healing, sensing, travel invitations, colony access and war targets use this shared resolver. Leaving a guild during a raid cannot retroactively erase responsibility or evade participation rules. Leadership transfer and disbanding must reconcile colony links and active obligations.

Avoid parallel guild databases. If War 'N Taxes provides the required membership model, use its records through an adapter and keep Bleach-specific metadata as an extension. Otherwise our guild records are authoritative and the adapter maps external faction IDs. Select that backend before guild implementation and document how external administrative commands are reconciled.

## 12. Colonies, gigai and governance

Ordinary colonies exist only in the Overworld. MineColonies owns citizens, buildings and work; our colony extension owns spiritual patronage, governor/protector assignments, contracts and petition history. Use dimension plus stable colony ID, not colony name alone.

Shinigami and Arrancar use a gigai for ordinary civilian management; Quincy interact physically. Model physical, embodied-gigai and spirit states with a presence service. Dropping into spirit mode creates a persistent body anchor; it must not copy the inventory into an independently lootable second inventory. Proposed default: one canonical player inventory, with the body acting as an owned interaction/recovery anchor. Body vulnerability, range and emergency return are explicit policies. Show a player's interaction restriction in the UI instead of silently failing.

Citizen and guard perception must be integrated deliberately. A visual invisibility flag is not a sufficient rule for whether citizens see Hollows, spiritual players or attacks. Hollow attacks on citizens, guard targeting, protective barriers and physical management privileges use the presence/colony policies.

Sample satisfaction from dependency metrics such as food, housing, safety, production and damage where exposed. Patron support is a separate recorded score; do not falsify MineColonies' happiness fields. Persistent neglect progresses through warning, petition, grace period and patron contest. Replacing a governor does not reset conditions. Aid and fulfilled defense/reconstruction contracts improve standing.

Patron contests require recorded service objectives and a protected resolution window. Combat victory alone does not automatically make citizens satisfied. Prosperous colonies can attract special visitors, traders or recruit candidates with capped event frequency. Economy settlements use the chosen dependency ledger: do not issue duplicate taxes, wages or treasury rewards.

## 13. War, raids and offline protection

Build objectives around shipments, depots, outposts, gates, resource extraction and territory. Raids can have partial victories; wiping every defender is not required. Formal conquest, resource raids, patron contests and PvE defenses are distinct operation types.

The war coordinator maintains proposed, scheduled, active, extracting, resolved and recovering states. Store participants, guild snapshots, region bounds, war window, objective state, reward operation IDs and protection policy revision. Admission checks take place when the war starts and when actors join or attempt harmful actions.

Offline protection belongs to the defended guild/colony, not solely the governor's login flag. Proposed default: advertised windows plus a minimum eligible defender presence; short disconnects receive a bounded grace period, and closing a window transitions into extraction/resolution. Disconnecting mid-fight cannot immediately erase committed damage. Precise thresholds and grace durations are server settings reviewed before multiplayer release.

Integrate War 'N Taxes where its public hooks satisfy these rules. Its documented war/economy/restoration behavior is a capability claim, not a tested guarantee for our abilities. One authority commits war outcomes and treasury changes. External war commands must respect our admission policy or be disabled/restricted; client UI hiding is insufficient.

For each operation choose exactly one terrain restoration owner: the external war system or our region restoration service. If external restoration does not observe direct ability edits, use our service for those operations or leave destructive integration disabled. Never have both restore the same region. Player blocks and inventories cannot be restored after loot removal in a way that duplicates items.

## 14. Realms, travel and non-Euclidean spaces

| Realm | Role and settlement policy |
|---|---|
| Fractured Overworld | Shared settlements, resources, exploration, guild conflict |
| Soul Society | Rukongai exploration, Seireitei sanctuary/raid city, training |
| Hueco Mundo | Hollow/Arrancar ecosystem, ruins, Menos Forest region, later faction content |
| Dangai | Authored transit corridors, route hazards, stable exits |
| Inner World | Private bounded trial/practice cells |
| Quincy refuge / Wandenreich concept | Later authored hub if gameplay requires it; not a required first-release dimension |

Start with simple terrain and placeholder hubs; detailed worldgen follows the functional loop. Terrain definitions and encounter profiles are independent. New worldgen changes need new-chunk testing and existing-world migration decisions.

The travel service owns a graph of stable route IDs and anchors, access conditions, opening cost, stability, lifetime and return policy. Senkaimon and Garganta use different progression/access rules over this shared mechanism. Persist routes, not render-entity references. Public routes still validate entrants and destination restrictions; invited portals require explicit admission. A portal cannot bypass a protected city, raid window or private trial boundary.

Before traversal: validate actor and route, find safe destination clearance, request bounded chunk loading, save recovery intent, then transfer and confirm arrival. A transit ID prevents repeated crossings from submitting multiple transfers. Disconnect/restart resumes recovery rather than leaving the player stranded. Return anchors must tolerate removed portals, blocked landing blocks and missing dimensions. Provide a known safe fallback.

Immersive rendering is an optional backend. Keep ordinary teleport travel working without it. Recursive views, spatial scaling, folded Dangai passages and larger-on-the-inside rooms require a dedicated compatibility spike with combat hit detection, projectiles, shaders, animation, companions and server tracking. Until proven, attacks do not cross portal boundaries. Restrict ticket counts, lifetimes, view recursion and concurrently active destinations.

## 15. Seireitei, training and restoration

Seireitei is a Shinigami training hub, archive and vault. Same-faction Shinigami hostility is blocked there even when their guilds are enemies elsewhere. Enemy-faction raids occur only through authorized entry routes and active raid windows. Target selection and ability damage must both enforce these rules; guards cannot be bypassed by owned projectiles or hostile summons.

Raid gameplay is infiltrate, acquire a validated objective token, and extract. Store objective claim and extraction state server-side so copying a token is not a second reward. A temporary faction penalty and recovery path can follow participation; exact penalty values remain content policy. Sanctuary users receive warnings and safe areas during raids.

Authored city regions restore daily on a configurable UTC schedule, with the configured display timezone shown to players. A missed run triggers at most one recovery job rather than many queued resets. Ordinary Overworld colonies do not receive this daily authored-city reset; their wartime repair follows section 13.

Restoration is a resumable job: announce, close admissions, evacuate, secure inventories/objectives, restore bounded batches from versioned templates, reconcile NPCs, verify, reopen. Persist job phase and cursor. A restart continues safely. Keep player storage outside restored regions; initially prohibit ordinary containers and permanent building there. Later support requires an explicit item escrow/reconciliation policy. Replenish raid objectives only after the prior cycle is settled, with a new cycle ID.

Practice arenas isolate destructive testing and can reset between sessions. They never mint ordinary loot or duplicate player loadouts when restoring. Use the same protection and cleanup paths as the live game.

## 16. Terrain destruction and performance

Centralize terrain changes through a destruction service. Inputs are responsible owner, cast/operation ID, region, shape, requested intensity and destruction category. Region/claim policy, war eligibility, world limits, block tags and resource budget determine what can change.

Protect administrative blocks, inventories, portal anchors and essential infrastructure unless an operation explicitly supports them. Proposed early default: destructible tagged terrain, no inventory-bearing block destruction, no block drops from large spiritual attacks. This avoids both loot multiplication and enormous item-entity loads. An explicit debris/reconstruction economy can be added later.

Queue and batch edits with per-cast, per-region and global limits. Never scan an unbounded sphere or synchronously load a whole city for a beam. Validate permission when edits execute, not only when initially queued. Fire, fluids, secondary explosions and delayed fields must retain the same protection context. If propagation cannot be contained, render the effect without unsafe physical propagation.

When budget is exhausted, reduce/defer terrain edits and presentation density; preserve consistent server hit rules. Large visuals must clearly distinguish their damage footprint from decorative reach. Profile server tick time, queued edits, active entities, network traffic and client frame time before increasing effect scale. Target the ordinary 50 ms server tick budget; measured capacity determines the supported player count.

## 17. Encounters, quests and companions

Encounter profiles separate natural spawn rate, regional caps, species weights, progression eligibility and faction-control suppression. Use biome/region tags and full IDs; avoid one giant per-player tick routine. The encounter director adds scheduled attacks, rare elites and expedition objectives only where dynamic behavior is required. Protected training hubs suppress ordinary hostile spawning.

Start with one placeholder Hollow and one repeatable encounter. Later expand creature tiers, specialized elites, bosses and evolution trials. Reward a validated contribution ledger including damage, healing, protection and objective support; use bounded time/range eligibility so remote party members cannot farm rewards. Mission IDs and completion IDs prevent repeated turn-ins. Defense contracts connect encounters to colony patronage.

Companions are persistent owned characters with identity, role, equipment policy, orders and relationship history. Start with one follower. Owner records survive chunk unload and travel; runtime entities are projections of that record. Persist alive, travelling and dead states and reconcile duplicate entity IDs. Permanent death records a terminal state rather than merely despawning an entity.

Do not mark an unloaded or missing entity dead automatically. Realm transfer requires admission and a safe spawn; if unavailable, the companion waits or returns to a designated safe anchor. Companion damage and hostility use owner relationships. Recruitment, storage and gear drops must prevent reclaiming both the character's equipment and a duplicate record. Permanent loss deserves clear warnings and a dependable recovery distinction between real death and loading failures.

## 18. Presentation and content pipeline

HUD priorities are health, Epic Fight stamina, spiritual energy, form drain, ability slots and cooldowns; expanded screens show mastery, trial requirements, sword spirit, guild/colony obligations and war status. Denials explain the relevant reason, such as low reserves, inactive war or missing gigai. Synchronize meaningful changes rather than a full profile every tick.

Spiritual sensing reveals coarse, permission-appropriate information governed by range, power, concealment and line/realm policy. Clients receive authorized detections, not every hidden player's exact coordinates. Spiritual pressure communicates danger with audiovisual cues; extreme power does not automatically become unrestricted crowd control.

Use vanilla placeholders first, then original/licensed models, sprites, animations, audio and particles. Maintain source files, attribution and generation provenance. Universal Modder's asset and testing skills are development guidance; their presence does not prove `um`, fal authentication, Blender or ffmpeg is installed. Asset tools are not runtime dependencies of the mod.

Presentation uses cast IDs and phase cues. Emitters and loops clean up on completion, interruption, despawn, travel and disconnect. Cap density and distance; provide reduced-effects and shader-compatible fallbacks. Never make a cinematic or particle library responsible for deciding whether an attack landed.

## 19. Delivery sequence and acceptance gates

Preserve the broad game while implementing complete slices. These gates replace speculative calendar promises.

| Milestone | Playable result | Required evidence |
|---|---|---|
| M0 — Baseline | Renamed mod on pinned toolchain | Build; client world; dedicated server; two clients connect; no client-class load on server |
| M1 — Spiritual identity | Choose Shinigami/Quincy; save profile, show energy, and inspect/reset it through the development console | Death, reconnect and restart preserve durable state; invalid requests rejected; debug actions remain server-authoritative |
| M2 — Combat loop | Bound sword, Quincy projectile, movement, one Hollow, rewards | Server hit authority; one reward per encounter; repeat-hit and energy checks |
| M3 — Release loop | Spirit/trial, one Shikai/Bankai, one Quincy form | Shared admission, upkeep, interruption, modifiers and persistent cooldown verified |
| M4 — Settlement loop | One colony, gigai, protector contract and guard interaction | External permissions respected; no copied body inventory; contract reward once |
| M5 — Realms and travel | Basic Soul Society/Hueco Mundo, anchors, Dangai and private trial cells | Blocked destination, disconnected traveller, restart recovery and arena isolation |
| M6 — Guild conflict | Guild roles, one raid objective, offline policy, bounded destruction | Single outcome/treasury owner; friendly fire; window closure; restoration owner verified |
| M7 — Living world | Prosperity/petitions, visitor and permanent companion | Metrics persist; governor replacement cannot reset neglect; death vs unload distinguished |
| M8 — Seireitei cycle | Raid, extraction, faction recovery, daily city restore | Unauthorized entry denied; no duplicate loot; mid-reset restart and evacuation |
| M9 — Expansion | Arrancar/evolution, Visored, capture and broader release roster | Each new family passes shared lifecycle and recovery tests; captured lease reconciles |
| M10 — Release candidate | Pinned client/server pack with operator guide | Built jar tested in real instance; performance evidence; backup/restore and migration rehearsal |

Dependency checks occur before each affected milestone. Optional visual portal work can progress after basic travel, but does not block the combat demo. Add one content family at a time. A demo may simplify content and presentation; it must still reject forged damage and avoid duplicating persistent ownership.

## 20. Testing, observability and operations

Use small unit tests for pure rules where a test facility exists, and NeoForge GameTests for world-dependent behavior. Add test dependencies/build changes only as a separately authorized development task. Manual multiplayer tests remain necessary for combat feel, portal rendering and real pack compatibility.

Minimum regression cases: duplicate and stale ability requests; zero energy; cancelled channel; repeated form activation; death/logout teardown; blocked destination; two users with the same Inner World theme; guild changes during war; delayed damage after window closure; copied medallion; capture expiry while owner offline; interrupted restoration; companion unload vs death; reward replay; old save migration.

Each milestone records exact jar/version configuration, test scenario, expected result, observed result, logs and optional screenshots. Test development runs and the packaged jar on a clean instance. Do not treat a successful build as proof of gameplay. Keep test worlds separate and backed up before destructive tests.

### NeoForge development lab rules

The pinned production target is Minecraft 1.21.1 with NeoForge 21.1.252, Java 21 and Parchment 2024.11.17. The locally installed Minecraft 26.3 and 26.4 snapshot clients are not compatible test targets for this mod; use a separate 1.21.1 NeoForge profile and dedicated lab worlds so ordinary player worlds remain untouched.

Create `MODLOG.md` at M0. For every material test, record the exact jar set, world/profile, command or scenario, expected and observed result, log location, screenshots when useful, failures and their resolved cause. This log is the source for future operator guidance and any field note; it must distinguish a development-run observation from a packaged-jar result.

Use GameTests for deterministic server-side rules, but retain real-client checks for assets, animation, HUD and portal rendering. `gameTestServer` initializes only the Overworld, so it cannot prove Inner World or other custom-dimension behavior. For M5 dimension, arena and travel checks, run the normal headless development server, invoke `/test runall` there (optionally through an access-controlled local RCON session), and verify the created target dimension and return path in the server log.

Register an explicit network protocol version before the first custom payload. A client or server with a mismatched `bleachfracturedworld` jar must be refused during login with a clear version diagnostic rather than failing later during gameplay.

Operator diagnostics should expose cast denials, active sessions, route tickets, arena allocations, raid phases and restoration status. Privileged repair commands must validate targets, log changes and reconcile through the same services. Log IDs/reasons without leaking credentials or private profile data. Rate-limit denial logs under abusive traffic.

Development builds include a `BWF Debug` entry on Minecraft's pause screen. Its standalone client screen reads only synchronized snapshots and sends bounded action identifiers; it never submits a player UUID, replacement profile, damage value, or reward. The server resolves the connection's player, checks `DebugAccessPolicy`, performs the action through an ordinary gameplay service or an explicitly separated debug service, increments state revision for accepted mutations, logs the outcome, and returns an authoritative snapshot. M1 exposes profile inspection, normal path selection, profile reset, energy presets, and refresh. Development access starts enabled for the private test loop; release hardening must default it off and add operator/environment restrictions before public distribution.

Pack distribution uses pinned manifests and separate client/server lists. Do not build a custom launcher until a concrete distribution need justifies it. Saves, player data and local credentials are not managed pack artifacts and must never be overwritten by updates.

## 21. Persistent development workflow and pending decisions

Use this document as the product/architecture baseline. The repository's `MODLOG.md` records verified facts, failed approaches, class/registry IDs, tests and the next concrete action. Research belongs under `docs/research/`; focused architecture decisions can be appended here or linked from it. Keep decompiled evidence outside the shipping repository.

At each session: read this baseline and the current log, inspect the actual code, state the smallest slice, implement it, verify it, and record the outcome. Universal Modder supplies recon, reverse engineering, asset, testing and documentation workflows. It does not grant permission to reuse proprietary BleachCraft assets or decompiled implementations. Reuse permitted tools with notices; implement the gameplay architecture originally.

The PC and laptop share code and documents through Git; generated files, local worlds and credentials remain machine-local. Commit/push/pull operations follow the user's project instructions. Architecture documents describe intended behavior; only verified implementation and `MODLOG.md` evidence establish what currently works.

Resolve these decisions at their milestone rather than inventing answers now:

| Decision | Recommended starting policy | Gate |
|---|---|---|
| Combat and colony artifact versions | Pin one tested NeoForge 1.21.1 combination | Before dependency installation |
| External guild and war authority | Choose one owner; map other records | Before M6 |
| War restoration compatibility | Verify direct edits; single owner per operation | Before destructive M6 tests |
| Guild composition | Single political guild; cross-faction alliances | Before M6 |
| PvP loot and death loss | Limited objective loot first; explicit inventory policy | Before M6 |
| Rare release assignment/reassignment | Earned access with a reviewed rarity policy | Before broad roster expansion |
| Gigai vulnerability and distance | Owned anchor, one canonical inventory | Before M4 |
| Capture expiry and recovery | Temporary lease; no permanent mastery loss | Before M9 |
| Daily city reset time | Configurable UTC schedule, announced locally | Before M8 |
| Performance capacity | Measure the intended combat/colony workload | Before public release |

Risk note: the largest uncertainties are combined dependency compatibility and interactions between destruction, inventories and external restoration. Save schemas, ownership records and server admission rules need verification before expanding content.

Next three development steps: verify the current dedicated-server baseline; select and test the minimum combat dependency stack; implement one persisted spiritual profile with authoritative synchronization and death/reconnect behavior. M2 follows immediately after that foundation.

## 22. Evidence and reference boundaries

- Accessible project conversations: **Minecraft mod architecture research**, **Reverse Engineer Mod Systems**, and **Compare Soul Reaper Quincy Powers**. Narrative examples inform intended mechanics; their illustrative abilities and outcomes are not measured balance evidence.
- Actual local `gradle.properties`, `build.gradle` and renamed entry point, inspected October 4, 2026. These establish configuration and code state only.
- [BleachCraft structural investigation](../reports/BleachCraft-reverse-engineering-map.md): original analysis distinguishes binary evidence, inference and recommendations. It does not establish source/asset reuse permission or runtime performance.
- [NeoForge player attachments](https://docs.neoforged.net/docs/1.21.1/datastorage/attachments/) support optional persistence and deliberate death copying; 1.21.1 client synchronization requires explicit packets.
- [NeoForge SavedData](https://docs.neoforged.net/docs/1.21.1/datastorage/saveddata/) provides world-owned persistence. This document's transaction/reconciliation rules are our design, not a guarantee of atomic engine saves.
- [NeoForge data components](https://docs.neoforged.net/docs/1.21.1/items/datacomponents/) are the item-state mechanism used for sword/medallion references.
- [NeoForge payload registration](https://docs.neoforged.net/docs/1.21.1/networking/payload/) provides directional registration and protocol versioning; validation and replay policies above are our design.
- [Epic Fight source](https://github.com/Antikythera-Studios/epicfight) and [MineColonies source](https://github.com/ldtteam/minecolonies) are integration research starting points; exact APIs/artifacts require pinned-version inspection.
- [War 'N Taxes author's feature description](https://github.com/mchivelli/War-N-Taxes-Mod-Minecolonies-Addon/blob/1.20.1/MOD_DESCRIPTION.md) documents NeoForge 1.21.1 support and overlapping economy, diplomacy and restoration features. These have not been exercised with our abilities.
- [Immersive Portals NeoForge releases](https://github.com/iPortalTeam/ImmersivePortalsModForNeo/releases) establish a candidate release line, not combined-stack compatibility.
- [Universal Modder](https://github.com/rehan-remade/universal-modder) supplies installed development skills and a separate tool ecosystem. Only skill-folder presence was checked in this design pass.
